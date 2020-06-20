package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.*;
import com.rawa.cloud.domain.*;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.LangHelper;
import com.rawa.cloud.helper.ZipHelper;
import com.rawa.cloud.model.file.*;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.NasRepository;
import com.rawa.cloud.repository.RecordRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    AppProperties appProperties;

    @Autowired
    AuthorityService authorityService;

    @Autowired
    NasService nasService;

    @Autowired
    PreviewService previewService;

    @Autowired
    RecordService recordService;

    @Autowired
    LogService logService;

    @Autowired
    RecycleService recycleService;

    @Autowired
    FileLogService fileLogService;

    @Autowired
    UserWatermarkService userWatermarkService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NasRepository nasRepository;

    @Autowired
    RecordRepository recordRepository;

    @Override
    @Transactional
    public Long add(FileAddModel model) {
        Long parentId = model.getParentId();
        File parent = fileRepository.findById(parentId).orElse(null);

        // 父级不能为空 文件 用户根目录
        if (parent != null && !parent.getDir() || parent.isUserRoot())
            throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, parentId);

        Umask umask = model.getDir() ? Umask.MK_DIR : Umask.NEW_FILE;
        if (!hasAuthority(parent, true, umask))
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        File file = new File();

        // 设置父级
        file.setParent(parent);

        // 设置个人文件
        if (parent.getUser() != null) {
            file.setUser(parent.getUser());
        }

        // 设置文件名
        String filename = model.getName();
        if (exists(filename, parent))
            throw new AppException(HttpJsonStatus.FILE_EXIST, filename);
        file.setName(filename);

        boolean dir = model.getDir();
        // 设置文件/文件夹
        file.setDir(dir);

        file.setStatus(true); // 设置为有效
        file.setCreationBy(ContextHelper.getCurrentUsername()); // 设置创建人
        file.setLastChangeBy(ContextHelper.getCurrentUsername()); // 设置最后更改人
        file.setCreationTime(new Date()); // 设置创建时间
        file.setLastChangeTime(new Date()); // 设置最后更改时间

        Log log = Log.build(LogModule.FILE, LogType.ADD).lc(parent.getPath());
        log.add("名称", filename, null);
        if (dir) { // 文件夹
            file.setLimitSize(model.getLimitSize()); // 设置文件夹容量
            file.setLimitSuffix(model.getLimitSuffix()); // 设置后缀限制
            log.st("新建文件夹");
            log.add("文件夹容量", FileHelper.formatSize(model.getLimitSize()), null);
            log.add("后缀限制", model.getLimitSuffix() == null ? "未设置" : model.getLimitSuffix(), null);
        } else { // 文件
            String uuid = model.getUuid();
            Nas nas = nasRepository.findNasByUuid(uuid);
            if (nas == null) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
            Long size = nas.getSize();
            String contentType = nas.getContentType();

            if (!checkCapacity(size, parent))
                throw new AppException(HttpJsonStatus.FILE_CAPACITY_OVERFLOW, size);
            if (!checkSuffix(contentType, parent))
                throw new AppException(HttpJsonStatus.FILE_ILLEGAL_SUFFIX, contentType);
            file.setUuid(uuid); // 设置uuid
            file.setContentType(contentType); // 设置文件类型
            file.setSize(size); // 设置文件大小
            log.st("新增文件");
            log.add("大小", FileHelper.formatSize(size), null);
            log.add("类型", contentType, null);
            log.add("uuid", uuid, null);
        }

        file = fileRepository.save(file);
        logService.add(log.end());
        if (!dir) {
            // 保存文件记录
            recordService.add(file, null);
            fileLogService.add(file.getId(), FileOptType.upload, "");
        } else {
            fileLogService.add(file.getId(), FileOptType.mkdir, "");
        }
        return file.getId();
    }

    @Override
    public void update(Long id, FileUpdateModel model) {
//        User user = userRepository.findById(ContextHelper.getCurrentUserId())
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.ACCESS_DENIED, id));
//        File file = fileRepository.findById(id)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        // 个人文件夹 或 文件 不允许修改
//        if (file.getUser() != null || !file.getDir()) throw new AppException(HttpJsonStatus.ACCESS_DENIED, id);
//
//        boolean access = false;
//        if (user.hasSuperRole() || (user.hasAdminRole() && user.isAdminFile(file))) {
//            access = true;
//        } else access = false;
//        if (!access) throw new AppException(HttpJsonStatus.ACCESS_DENIED, id);
    }

    @Override
    public List<File> query(FileQueryModel model) {
        Long parentId = model.getParentId();
        File parent = fileRepository.findById(parentId).orElse(null);
        boolean access = hasAuthority(parent, true, Umask.ACCESS);
        if (!access) return Collections.emptyList();
        Boolean dir = model.getDir();
        if (parent == null || !parent.getStatus()) return Collections.emptyList();
//        File query = new File();
//        query.setParent(parent);
//        query.setDir(dir);
//        query.setStatus(true); // 只能查询有效文件

        boolean isRoot = parent.isRoot();

        final boolean admin = isAdminFile(parent, false);

        List<File> ret = fileRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("status"), true));
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("parent"), parent));
            if (dir != null) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("dir"), dir));
            }

            return predicate;
        });
        ret = ret.stream().filter(s -> {
            if (isRoot && s.isUserRoot()) return false; // 去除用户目录
            if (!hasAuthority(s, false, Umask.ACCESS)) return false; // 无查看权限
            // 有权限判断是否有子集 暂时不计算是否有子集， 这样会损失很多性能
//            if (dir) {
//                boolean leaf = s.getChildren().stream()
//                        .filter(m -> Boolean.TRUE.equals(model.getDir()) ? m.getDir() : true) // 如果只查询文件夹， 默认子节点也全部是文件夹)
//                        .anyMatch(m -> hasAuthority(m, true, Umask.ACCESS));
//                s.setLeaf(!leaf);
//            } else {
//                s.setLeaf(false);
//            }

            if (!admin) {
                s.setAdmin(isAdminFile(s, true));
            } else {
                s.setAdmin(true);
            }
            return true;
        })
        .collect(Collectors.toList());
        return ret;
    }

    @Override
    public File get(Long id) {
        File file = fileRepository.findById(id).orElse(null);
        if(!hasAuthority(file, true, Umask.ACCESS)) return null;
        return file;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        Long id = ids.get(0);
        if (id == null) return;
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        File parent = file.getParent();
        boolean access = hasAuthority(parent, true, Umask.DELETE);
        List<File> ret = parent.getChildren()
                .stream().filter(s -> {
                    return ids.contains(s.getId()) && !s.isSystemFile(); // 非系统文件
                })
                .filter(s -> {
                    Boolean has = hasAuthority(s, false, Umask.DELETE);
                    boolean r = has == null ? access : has;
                    if (r) {
                        s.setStatus(false);
                        traverse(s.getChildren(), f -> {
                            f.setStatus(false);
                            fileRepository.save(f);
                            return null;
                        });
                        logService.add(Log.build(LogModule.FILE, LogType.DELETE).lc(file.getPath()).end());
                    }
                    return r;
                })
                .collect(Collectors.toList());
        fileRepository.saveAll(ret);
        recycleService.addInBatch(ret);

    }

    @Override
    public File getRootFile() {
        File file = fileRepository.findFileByParentIsNull();
        return file != null ? get(file.getId()) : null;
    }

    @Override
    public File getUserRootFile() {
        File userRoot = getUserDir();
        File file = fileRepository.findByParentAndName(userRoot, ContextHelper.getCurrentUsername());
        if(file == null) file = createUserFile(ContextHelper.getCurrentUsername());
        return get(file.getId());
    }

    @Override
    public java.io.File download(Long id, boolean watermark) {
        User user = userRepository.findById(ContextHelper.getCurrentUserId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, ContextHelper.getCurrentUserId()));
        return download(id, user, watermark);
    }

    @Override
    public java.io.File download(Long id, User user, boolean watermark) {
        fileLogService.add(id, FileOptType.download, "");
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        if(!hasAuthority(file, user,true, Umask.DOWNLOAD))
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, Umask.DOWNLOAD);
        if(!file.getDir()) {
            java.io.File f = nasService.download(file.getUuid(), true);
            if (watermark) {
                return userWatermarkService.generateWatermark(f, user.getUsername());
            } else {
                return f;
            }
        }
        java.io.File base = new java.io.File(appProperties.getTemp(), "zip_" + UUID.randomUUID().toString());
        base.mkdir();
        java.io.File dir = downloadDir(file, base, user, watermark);
        java.io.File zipFile = new java.io.File(base, file.getName() + ".zip");
        ZipHelper.compress(dir, zipFile);
        return zipFile;
    }

    @Override
    public java.io.File preview(Long id) {
        fileLogService.add(id, FileOptType.preview, "");
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        if(!hasAuthority(file, true, Umask.PREVIW))
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, Umask.PREVIW);
        return previewService.preview(nasService.download(file.getUuid(), true), ContextHelper.getCurrentUsername());
    }

    @Override
    public void rename(Long id, FileRenameModel model) {
        Umask umask = Umask.RENAME;
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        if (file.getName().equals(model.getName())) return; // 名称无更改
        File parent = file.getParent();

        // 无效文件 以及 根目录
        if (!file.getStatus() || file.isRoot() || file.isUserRoot())
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        // 判断权限
        if (!hasAuthority(file, true, umask))
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        // 判断名称是否重复
        String filename = model.getName();
        if (exists(filename, parent))
            throw new AppException(HttpJsonStatus.FILE_EXIST, filename);
        logService.add(Log.build(LogModule.FILE, LogType.UPDATE).st("重命名").lc(file.getPath()).add("名称", filename, file.getName()).end());
        fileLogService.add(id, FileOptType.rename, file.getName());
        file.setName(filename);
        fileRepository.save(file);
    }

    @Override
    @Transactional
    public void renew(Long id, FileRenewModel model) {
        String uuid = model.getUuid();
        Umask umask = Umask.UPDATE_FILE;
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));

        // 无权操作文件夹以及无效文件
        if (file.getDir() || !file.getStatus())
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        // 无更改，不操作
        if (file.getUuid().equals(uuid)) return;

        // 判断权限
        if (!hasAuthority(file, true, umask))
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        // 原文件丢失
        Nas nas = nasRepository.findNasByUuid(uuid);
        if (nas == null)
            throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);

        // 名称不一致，不允许更改
//        if (!file.getName().equals(nas.getName()))
//            throw new AppException(HttpJsonStatus.VALID_FAIL, "名称不一致");

        Long size = nas.getSize();
        if (size > file.getSize()) {
            if(!checkCapacity(size - file.getSize(), file.getParent()))
                throw new AppException(HttpJsonStatus.FILE_CAPACITY_OVERFLOW, size);
        }
        Log log = Log.build(LogModule.FILE, LogType.UPDATE).lc(file.getPath()).st("更新文件");
        log.add("大小", FileHelper.formatSize(size), FileHelper.formatSize(file.getSize()))
            .add("uuid", uuid, file.getUuid());
        logService.add(log.end());
        fileLogService.add(id, FileOptType.update, "");
        file.setUuid(uuid);
        file.setLastChangeBy(ContextHelper.getCurrentUsername());
        file.setLastChangeTime(new Date());
        file.setSize(size);
        nas.setStatus(true);
        fileRepository.save(file);
        nasRepository.save(nas);
        // 保存文件记录
        recordService.add(file, model.getRemark());
    }

    @Override
    @Transactional
    public void autoSave(Long id, String uuid, String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) throw new AppException(HttpJsonStatus.USER_NOT_FOUND, username);
        Umask umask = Umask.UPDATE_FILE;
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));

        // 无权操作文件夹以及无效文件
        if (file.getDir() || !file.getStatus())
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        // 无更改，不操作
        if (file.getUuid().equals(uuid)) return;

        // 判断权限
        if (!hasAuthority(file, user, true, umask))
            throw new AppException(HttpJsonStatus.ACCESS_DENIED, umask);

        // 原文件丢失
        Nas nas = nasRepository.findNasByUuid(uuid);
        if (nas == null)
            throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);

        // 名称不一致，不允许更改
        if (!file.getName().equals(nas.getName()))
            throw new AppException(HttpJsonStatus.VALID_FAIL, "名称不一致");

        Long size = nas.getSize();
        if (size > file.getSize()) {
            if(!checkCapacity(size - file.getSize(), file.getParent()))
                throw new AppException(HttpJsonStatus.FILE_CAPACITY_OVERFLOW, size);
        }
        Log log = Log.build(LogModule.FILE, LogType.UPDATE).lc(file.getPath()).st("更新文件");
        log.add("大小", FileHelper.formatSize(size), FileHelper.formatSize(file.getSize()))
                .add("uuid", uuid, file.getUuid());
        logService.add(log.end(), username);
        fileLogService.add(id, FileOptType.update, username, "");
        file.setUuid(uuid);
        file.setLastChangeBy(username);
        file.setLastChangeTime(new Date());
        file.setSize(size);
        nas.setStatus(true);
        fileRepository.save(file);
        nasRepository.save(nas);
        // 保存文件记录
        recordService.add(file, "自动保存");
    }

    @Override
    public List<Record> getRecords(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, null));
        return recordRepository.findAllByFileOrderByLastChangeTimeDesc(file);
    }

    @Override
    public List<File> getParents(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        List<File> ret = new ArrayList<>();
        ret.add(file);
        if (Boolean.TRUE.equals(file.getSystem())) return ret;
        File current = file.getParentId() == null ? null
                : fileRepository.findById(file.getParentId()).orElse(null);
        while (current != null) {
            ret.add(0, current);
            if (Boolean.TRUE.equals(current.getSystem())) break;
            current = current.getParentId() == null ? null
                    : fileRepository.findById(current.getParentId()).orElse(null);
        }
        return ret;
    }

    @Override
    public Boolean hasAuthority(File file, boolean implicit, Umask... umasks) throws AppException {
        User user = userRepository.findById(ContextHelper.getCurrentUserId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.ACCESS_DENIED, null));
        return hasAuthority(file, user, implicit, umasks);
    }

    @Override
    public Boolean hasAuthority(File file, User user, boolean implicit, Umask... umasks) throws AppException {
        if (file == null) return false;

        // 个人文件夹只要所有者拥有所以权限
        if (file.getUser() != null) {
            boolean has = file.getUser().getId().equals(user.getId());
            file.setUmask(has ? -1L : 0L);
            return has;
        }
        if(user.hasSuperRole()) {
            file.setUmask(-1L);
            return true;
        }
        if(user.hasAdminRole() && user.isAdminFile(file)) {
            file.setUmask(-1L);
            return true;
        }
        if (!implicit) {
            Long umask = authorityService.umask(user.getId(), file.getId(), true, false);
            if (umask == null) {
                umask = file.getParent() != null ? file.getParent().getUmask() : null;
            }
            if (umask == null) return null;
            file.setUmask(umask);
            if (Umask.hasAny(umask, umasks)) return true;
            return false;
        }
        Long umask = authorityService.umask(user.getId(), file.getId(), true, true);
        file.setUmask(umask);
        return umask == null ? false : Umask.hasAny(umask, umasks);
    }

    @Override
    @Transactional
    public List<Long> batchAdd(List<FileBatchAddModel> model) {
        // TODO 将来可以做进一步优化
        List<Long> ret = new ArrayList<>();
        for(FileBatchAddModel m : model) {
            FileAddModel am = new FileAddModel();
            am.setParentId(m.getParentId());
            am.setName(m.getName());
            am.setDir(m.getDir());
            am.setUuid(m.getUuid());
            Long id = add(am);
            ret.add(id);
            List<FileBatchAddModel> children = m.getChildren();
            if (children != null && children.size() > 0) {
                for (int i = 0; i < children.size(); i++) {
                    children.get(i).setParentId(id);
                }
                batchAdd(m.getChildren());
            }
        }
        return ret;
    }

    @Override
    public int move(FileMoveModel model) {
        List<File> sources = fileRepository.findAllById(model.getSources())
                .stream()
                .filter(s -> {
                    if (Boolean.TRUE.equals(s.getSystem())) return false; // 系统文件不能移动
                    if (!hasAuthority(s, true, Umask.DELETE)) return false; // 无删除权限不能移动
                    return true;
                }).collect(Collectors.toList());
        if (sources.size() < 1) return 0;
        File target = fileRepository.findById(model.getTarget())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, model.getTarget()));
        accessTarget(sources, target);

        // 更新文件属性
        String path = target.getPath();
        int total = 0;
        if (!CollectionUtils.isEmpty(sources)) {
            Log log = Log.build(LogModule.FILE, LogType.ADD).lc(path).st("移动到");
            sources = sources.stream().filter(s -> {
                if (!Boolean.TRUE.equals(s.getStatus())) return false; // 无效文件
                if (Boolean.FALSE.equals(hasAuthority(s, false, Umask.DELETE))) {
                    throw new AppException(HttpJsonStatus.OPT_NOT_ALLOWED, s.getName());
                }
                if (exists(s.getName(), target)) {
                    throw new AppException(HttpJsonStatus.FILE_EXIST, s.getName());
                }
                return true;
            }).collect(Collectors.toList());
            for (File f : sources) {
                total += 1;
                log.add("文件" + String.valueOf(total), path, f.getPath());
                if (!LangHelper.equals(f.getUser(), target.getUser())) { // 公用、私用之间挪动
                    traverse(f.getChildren(), s -> {
                        s.setUser(target.getUser());
                        fileRepository.save(s);
                        return null;
                    });
                }
                f.setUser(target.getUser());
                f.setParent(target);
                f.setLastChangeTime(new Date());
                f.setLastChangeBy(ContextHelper.getCurrentUsername());

            }
            fileRepository.saveAll(sources);
            logService.add(log.add("数量", String.valueOf(total), null).end());
        }
        return total;
    }

    @Override
    public int copy(FileMoveModel model) {
        List<File> sources = fileRepository.findAllById(model.getSources())
                .stream()
                .filter(s -> {
                    if (Boolean.TRUE.equals(s.getSystem())) return false; // 系统文件不能复制
                    if (!hasAuthority(s, true, Umask.DOWNLOAD)) return false; // 无下载权限不能复制
                    return true;
                }).collect(Collectors.toList());
        if (sources.size() < 1) return 0;
        File target = fileRepository.findById(model.getTarget())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, model.getTarget()));

        accessTarget(sources, target);

        // 复制文件
        Log log = Log.build(LogModule.FILE, LogType.ADD).lc(target.getPath()).st("复制到");
        int num = traverseAddForCopy(sources, target, log);
//        fileRepository.saveAll(copy);
        log.add("数量", String.valueOf(num), null);
        logService.add(log.end());
        return num;
    }

    /**
     * 只显示前100条记录
     * @param model
     * @return
     */
    @Override
    @Transactional
    public List<File> search(FileSearchModel model) {
        int page = 0;
        List<File> ret = new LinkedList<>();
        List<File> files = searchByPage(model, page);
        int max = 20;
        while (files.size() > 0) {
            for (File f : files) {
                if (hasAuthority(f, true, Umask.ACCESS)) {
                    f.setFilePath(f.getPath());
                    ret.add(f);
                    if (ret.size() >= max) break;
                }
            }
            page += 1;
            files = searchByPage(model, page);
        }
        return ret;
    }

    @Override
    public File createUserFile(String username) {
        File userRoot = getUserDir();
        File file = fileRepository.findByParentAndName(userRoot, username);
        if (file != null) return file;
        File userFile = new File();
        userFile.setStatus(true);
        userFile.setDir(true);
        userFile.setName(username);
        userFile.setCreationBy(ContextHelper.getCurrentUsername());
        userFile.setLastChangeBy(ContextHelper.getCurrentUsername());
        userFile.setLastChangeTime(new Date());
        userFile.setCreationTime(new Date());
        userFile.setParent(userRoot);
        userFile.setSystem(true);
        userFile.setUser(userRepository.findUserByUsername(username));

        return fileRepository.save(userFile);
    }

    @Override
    public List<File> getAdminRoots() {
        User user = userRepository.findById(ContextHelper.getCurrentUserId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, ContextHelper.getCurrentUserId()));
        List<File> ret = new ArrayList<>();
        if (user.hasSuperRole()) {
            ret.add(getRootFile());
        } else if (user.hasAdminRole()) {
            List<File> files = user.getFileList();
            files.forEach(s -> {
                List<File> children = s.getChildren().stream().filter(k -> k.getStatus()).collect(Collectors.toList());
                s.setLeaf(children.size() < 1);
            });
            ret.addAll(files);
        }
        return ret;
    }

    @Override
    public void deleteUserFile(String username) {
        File userRoot = getUserDir();
        File userFile = fileRepository.findByParentAndName(userRoot, username);
        if (userFile != null) {
            fileRepository.delete(userFile);
        }
    }

    @Override
    public java.io.File exportFile(java.io.File base) {
        File root = fileRepository.findFileByParentIsNull();
        User user = userRepository.findUserByUsername("root");
        return downloadDir(root, base, user, false);
    }

    @Override
    @Transactional
    public void importFile(java.io.File importFile, File parent) {
        String filename = importFile.getName();
        if (exists(filename, parent)) {
            log.error("文件已存在：" + filename);
            throw new RuntimeException("文件已存在：" + filename);
        }
        traverseImportFile(importFile, parent);
    }

    private void traverseImportFile (java.io.File importFile, File parent) {
        boolean dir = importFile.isDirectory();
        String name = importFile.getName();
        File file = new File();

        file.setName(name);
        file.setParent(parent);
        file.setUser(parent.getUser());
        file.setStatus(true);
        file.setSystem(false);
        file.setCreationTime(new Date());
        file.setLastChangeTime(new Date());
        file.setCreationBy("root");
        file.setLastChangeBy("root");
        file.setDir(dir);

        if (!dir) {
            long size = importFile.length();
            String contentType = FileHelper.getSuffix(name);
            String uuid = nasService.upload(importFile, "root");
            file.setSize(size);
            file.setContentType(contentType);
            file.setUuid(uuid);
        }
        File savedFile = fileRepository.save(file);
        if (dir) {
            java.io.File[] children = importFile.listFiles();
            if (children.length > 0) {
                for (java.io.File item : children) {
                    traverseImportFile(item, savedFile);
                }
            }
        }
    }

//    @Override
//    public Long add(FileAddModel model) {
//        Long parentId = model.getParentId();
//        Umask umask = model.getDir() ? Umask.MK_DIR : Umask.NEW_FILE;
//        hasAuthority(null, parentId, true, umask);
//        File file = generateAddFile(model);
//        file = fileRepository.save(file);
//        return file.getId();
//    }
//
//    @Override
//    public void update(Long id, FileUpdateModel model) {
//        File file = generatePatchFile(id, model);
//        fileRepository.save(file);
//    }
//
//    @Override
//    public List<File> query(FileQueryModel model) {
//        List<File> files = fileRepository.findAll(generateQueryFile(model));
//        return files.stream().filter(s -> {
//            if (!hasOwnPersonal(s)) return false;
//            try {
//                hasAuthority(null, s.getId(), true, Umask.ACCESS);
//                return true;
//            } catch (AppException e) {
//                return false;
//            }
//
//        }).collect(Collectors.toList());
//    }
//
//    @Override
//    public File get(Long id) {
//        hasAuthority(null, id, true, Umask.ACCESS);
//        return fileRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public void delete(List<Long> ids) {
//
//    }
//
//    @Override
//    public File getRootFile() {
//        return null;
//    }
//
//    @Override
//    public File getUserRootFile() {
//        return null;
//    }
//
//    @Override
//    @Transactional
//    public void delete(Long id) {
////        hasAuthority(null, id, true, Umask.DELETE);
//        File file = fileRepository.findById(id)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        deleteFile(file);
//    }
//
//    @Override
//    @Transactional
//    public void recover(Long id) {
//        File file = fileRepository.findById(id)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        recoverFile(file);
//    }
//
//    @Override
//    public java.io.File download(Long id) {
//        hasAuthority(null, id, true, Umask.DOWNLOAD);
//        File file = fileRepository.findById(id).orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        return nasService.download(file.getUuid(), true);
//    }
//
//    @Override
//    public java.io.File preview(Long id) {
//        hasAuthority(null, id, true, Umask.PREVIW);
//        File file = fileRepository.findById(id).orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        return previewService.preview(nasService.download(file.getUuid(), true));
//    }
//
//    @Override
//    public void hasAuthority(Long principleId, Long fileId, boolean isUser, Umask... umasks) throws AppException {
////        if (principleId == null) {
////            principleId = ContextHelper.getCurrentUserId();
////            isUser = true;
////        }
//        User user = userRepository.findById(principleId)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, principleId));
////        if (fileId == null) {
////            if (user.hasSuperRole()) return;
////            throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, Arrays.stream(umasks).map(s -> s.desc));
////        }
//        File file = fileRepository.findById(fileId)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
//        if (user.hasSuperRole()) return;
//
//        if (user.hasAdminRole()) {
//            if (user.isAdminFile(file)) return;
//        }
//
//        if (fileId == null)
//            throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, Arrays.stream(umasks).map(s -> s.desc));
//
//        boolean access = authorityService.hasAuthority(principleId, fileId, isUser, umasks);
//        if (!access) throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, Arrays.stream(umasks).map(s -> s.desc));
//    }
//
////    @Override
////    public Long addRootFile(String username) {
////        File file = fileRepository.findFileByParentIsNullAndPersonalIsTrueAndStatusIsTrueAndCreationBy(username);
////        if (file != null) return file.getId();
////        file = new File();
////        file.setDir(true);
////        file.setStatus(true);
////        file.setPersonal(true);
////        file.setName("个人文件夹");
////        file.setLastChangeTime(new Date());
////        file.setLastChangeBy(username);
////        file.setCreationTime(new Date());
////        file.setCreationBy(username);
////        file = fileRepository.save(file);
////        return file.getId();
////    }
//
////    @Override
////    @Transactional
////    public void deleteRootFile(String username) {
////        File file = fileRepository.findFileByParentIsNullAndPersonalIsTrueAndStatusIsTrueAndCreationBy(username);
////        if (file == null) return;
////        deleteFile(file);
////    }
//
////    @Override
////    public String getUuidForDownload(Long id, Long recordId) {
////        File file = fileRepository.findById(id)
////                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
////        if (!file.getStatus()) throw new AppException(HttpJsonStatus.FILE_DELETED, id);
////        if (file.getDir()) throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, id);
////        Record record = null;
////        if (recordId != null) record = file.getRecords().stream().filter(s -> s.getId().equals(recordId)).findAny()
////                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_RECORD_NOT_FOUND, recordId));
////        if (record != null) {
////            hasAuthority(null, id, true, Umask.RECORD_DOWNLOAD);
////            return record.getUuid();
////        }
////        hasAuthority(null, id, true, Umask.DOWNLOAD);
////        return file.getUuid();
////    }
//
//
//    // inner
//    private File generateAddFile(FileAddModel model) {
//        File file = new File();
//        Long parentId = model.getParentId();
//        File parent = parentId == null ? null : fileRepository.findById(parentId)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
//        if (parent != null && !parent.getDir())
//            throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, parentId); // 不能是文件作为父级
//        Boolean personal = model.getPersonal() != null ? model.getPersonal()
//                : (parent != null ? parent.getPersonal() : false);
//        boolean dir = model.getDir();
//
//        if (exists(model.getName(), parentId)) throw new AppException(HttpJsonStatus.FILE_EXIST, model.getName());
//
//        file.setName(model.getName()); // 设置名称
//        file.setParent(parent); // 设置父级
//        file.setDir(dir); // 设置文件/文件夹
//        file.setPersonal(personal); // 设置是否是个人文件
//
//        if (dir) { // 文件夹
//            file.setLimitSize(model.getLimitSize()); // 设置文件夹容量
//            file.setLimitSuffix(model.getLimitSuffix()); // 设置后缀限制
//        } else { // 文件
//            String uuid = model.getUuid();
//            Nas nas = nasRepository.findNasByUuid(uuid);
//            if (nas == null) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
//            Long size = nas.getSize();
//            String contentType = nas.getContentType();
//            if (parent != null) {
//                if (!checkCapacity(size, parentId)) throw new AppException(HttpJsonStatus.FILE_CAPACITY_OVERFLOW, size);
//                if (!checkSuffix(contentType, parentId))
//                    throw new AppException(HttpJsonStatus.FILE_ILLEGAL_SUFFIX, contentType);
//            }
//
//            file.setUuid(uuid); // 设置uuid
//            file.setContentType(contentType); // 设置文件类型
//            file.setSize(size); // 设置文件大小
//        }
//
//        file.setStatus(true); // 设置为有效
//        file.setCreationBy(ContextHelper.getCurrentUsername()); // 设置创建人
//        file.setLastChangeBy(ContextHelper.getCurrentUsername()); // 设置最后更改人
//        file.setCreationTime(new Date()); // 设置创建时间
//        file.setLastChangeTime(new Date()); // 设置最后更改时间
//
//        return file;
//    }
//
//    private File generatePatchFile(Long id, FileUpdateModel model) {
//        File file = fileRepository.findById(id)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        File parent = file.getParent();
//        String name = model.getName();
//        String uuid = model.getUuid();
//        if (name != null) {
//            hasAuthority(null, id, true, Umask.RENAME);
//            Long parentId = parent == null ? null : parent.getId();
//            if (exists(name, parentId)) throw new AppException(HttpJsonStatus.FILE_EXIST, model.getName());
//            file.setName(name);
//        }
//        if (uuid != null && !file.getDir()) { // 更新文件内容
//            Nas nas = nasRepository.findNasByUuid(uuid);
//            if (nas == null) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
//            Long size = nas.getSize();
//            file.setUuid(uuid);
//            file.setSize(size);
//            file.setLastChangeBy(ContextHelper.getCurrentUsername()); // 设置最后更改人
//            file.setLastChangeTime(new Date()); // 设置最后更改时间
//            // 记录更新轨迹
//            Record record = new Record();
//            record.setSize(size);
//            record.setUuid(uuid);
//            record.setRemark(model.getRemark());
//            record.setLastChangeBy(ContextHelper.getCurrentUsername());
//            record.setLastChangeTime(new Date());
//            file.getRecords().add(record);
//        }
//        return file;
//    }
//
//    private Example<File> generateQueryFile(FileQueryModel model) {
//        Long parentId = model.getParentId();
//        File file = new File();
//        file.setDir(model.getDir());
//        file.setCreationBy(model.getCreationBy());
//        file.setStatus(true); // 只能查询有效的文件
//        file.setPersonal(model.getPersonal());
//        ExampleMatcher matcher = null;
//        if (parentId != null) {
//            if (parentId < 0) {
//                matcher = ExampleMatcher.matching()
//                        .withIncludeNullValues()
//                        .withIgnorePaths(BeanHelper.getNullFields(file, "parent"));
//            } else {
//                File parent = fileRepository.findById(parentId)
//                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
//                file.setParent(parent);
//            }
//        }
//        return matcher == null ? Example.of(file) : Example.of(file, matcher);
//    }
//
    /**
     * 判断是否存在相同文件名
     *
     * @param filename
     * @param
     * @return
     */
    private boolean exists(String filename, File parent) {
        List<File> children = parent.getChildren();
        if (children == null) return false;
        return parent.getChildren().stream()
                .filter(s -> s.getStatus())
                .anyMatch(s -> s.getName().equals(filename));
    }

    /**
     * 检查文件空间是否足够
     *
     * @param size
     * @param
     * @return
     */
    private boolean checkCapacity(Long size, File parent) {
        if (parent.getLimitSize() == null) return true;
        Long fullSize = parent.getLimitSize();
        Long usedSize = 0L;
        List<File> list = parent.getChildren().stream().filter(s -> Boolean.TRUE.equals(s.getStatus())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            usedSize = list.stream().map(s -> s.getActualSize(false)).reduce((a, b) -> a + b).get();
        }
        return fullSize >= usedSize + size;
    }

    /**
     * 检查文件后缀
     *
     * @param suffix
     * @param
     * @return
     */
    private boolean checkSuffix(String suffix, File parent) {
        String limitSuffixes = parent.getLimitSuffix();
        if (limitSuffixes == null || limitSuffixes.isEmpty()) return true;
        return limitSuffixes.contains(suffix);
    }


    /**
     * 文件复制层级生成
     * @param files
     * @param parent
     */
    private int traverseAddForCopy (List<File> files, File parent, Log log) {
        if (files == null || files.size() < 1) return 0;
        files = files.stream().filter(s -> {
            if (!Boolean.TRUE.equals(s.getStatus())) return false; // 无效文件
            if (Boolean.FALSE.equals(hasAuthority(s, false, Umask.DOWNLOAD))) return false; // 直接下载禁用权限不能复制
            if (exists(s.getName(), parent)) {
                throw new AppException(HttpJsonStatus.FILE_EXIST, s.getName());
            }
            return true;
        }).collect(Collectors.toList());
        int total = 0;
        for (File f : files) {
            File copy = new File();
            copy.setLastChangeBy(ContextHelper.getCurrentUsername());
            copy.setLastChangeTime(new Date());
            copy.setUser(parent.getUser());
            copy.setSystem(false);
            copy.setStatus(true);
            copy.setParent(parent);
            copy.setDir(f.getDir());
            copy.setUuid(f.getUuid());
            copy.setName(f.getName());
            copy.setCreationTime(f.getCreationTime());
            copy.setCreationBy(f.getCreationBy());
            copy.setContentType(f.getContentType());
            copy.setSize(f.getSize());
            copy.setLimitSize(f.getLimitSize());
            copy.setLimitSuffix(f.getLimitSuffix());
            copy = fileRepository.save(copy);
            copy.setUser(parent.getUser());
            if (!copy.getDir()) {
                total += 1;
                if (log != null) {
                    log.add("文件" + total, copy.getPath(), f.getPath());
                }
            }
            total += traverseAddForCopy(f.getChildren(), copy, log);
        }
        return total;
    }

    /**
     * 尝试访问目标文件 （移动或复制）
     * @param sources
     * @param target
     */
    private void accessTarget (List<File> sources, File target) {
        // 检测重名
        sources.stream()
                .anyMatch(s -> {
                    if (exists(s.getName(), target)) throw new AppException(HttpJsonStatus.FILE_EXIST, s.getName());
                    return false;
                });
        boolean hasDir = sources.stream().anyMatch(s -> {
            return s.getDir();
        });
        boolean hasFile = sources.stream().anyMatch(s -> {
            return !s.getDir();
        });
        boolean access = false;
        if (hasDir && hasFile) access = hasAuthority(target, true, Umask.NEW_FILE, Umask.MK_DIR);
        else if (hasDir) access = hasAuthority(target, true, Umask.MK_DIR);
        else if (hasFile) access = hasAuthority(target, true, Umask.NEW_FILE);
        if (!access) throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, target.getId());
    }

    private java.io.File downloadDir (File dir, java.io.File parent, User user, boolean watermark) {
        java.io.File root =  new java.io.File(parent, dir.getName());
        root.mkdir();
        dir
            .getChildren()
            .stream()
             .filter(s -> {
                 return !Boolean.FALSE.equals(hasAuthority(s, user,false, Umask.DOWNLOAD)) && Boolean.TRUE.equals(s.getStatus());
             })
            .forEach(s -> {
               if (s.getDir()) {
                   downloadDir(s, root, user, watermark);
               } else {
                   java.io.File f = new java.io.File(root, s.getName());
                   java.io.File rawFile = nasService.download(s.getUuid(), true);
                   if (watermark) {
                       rawFile = userWatermarkService.generateWatermark(f, user.getUsername());
                   }
                   try {
                       FileCopyUtils.copy(rawFile, f);
                       log.info("下载文件: " + f.getName());
                   } catch (IOException e) {
                       throw new AppException(HttpJsonStatus.ERROR, e);
                   }
               }
            });
        return root;
    }

    /**
     * 判断当前用户是否对该文件有管理权限
     * @param file
     * @param root 只是根文件夹
     * @return
     */
    private boolean isAdminFile (File file, boolean root) {
        User user = userRepository.findById(ContextHelper.getCurrentUserId())
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, ContextHelper.getCurrentUserId()));
        boolean admin = false;
        if (user.hasSuperRole()) admin = true;
        else if (user.hasAdminRole()) {
            if (root) {
                admin = user.isAdminRootFile(file);
            } else {
                admin = user.isAdminFile(file);
            }
        }
        else admin = false;
        return admin;
    }

    private File getUserDir () {
        File root = fileRepository.findFileByParentIsNull();
        File userRoot = root.getChildren().stream()
                .filter(s -> s.isUserRoot())
                .collect(Collectors.toList()).get(0);
        if (userRoot == null) throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, "用户根目录");
        return userRoot;
    }

    private void traverse (List<File> list, Function<File, Void> fn) {
        if (CollectionUtils.isEmpty(list)) return;
        for(File f : list) {
            traverse(f.getChildren(), fn);
            fn.apply(f);
        }
    }
//
//    /**
//     * 判断个人文件是否属于自己
//     *
//     * @param file
//     * @return
//     */
//    private boolean hasOwnPersonal(File file) {
//        if (!file.getPersonal()) return true;
//        return file.getCreationBy().equals(ContextHelper.getCurrentUsername());
//    }
//
//    /**
//     * 删除文件以及子文件的相关资源
//     *
//     * @param file
//     */
//    private void deleteFile(File file) {
//        List<File> files = file.getChildren();
//        for (File f : files) {
//            deleteFile(f);
//        }
//        // 删除文件权限
////        authorityService.deleteByFile(file);
//        fileRepository.delete(file);
//    }
//
//    /**
//     * 恢复文件以及子文件
//     *
//     * @param file
//     */
//    private void recoverFile(File file) {
//        List<File> files = file.getChildren();
//        for (File f : files) {
//            recoverFile(f);
//        }
//        // 删除文件权限
//        file.setStatus(true);
//        fileRepository.save(file);
//    }

    private List<File> searchByPage(FileSearchModel model, int page) {
        Pageable pageable = PageRequest.of(page, 100);
        List<File> ret = fileRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("status"), true));
            predicate.getExpressions().add(criteriaBuilder.isNull(root.get("system")));
            if (!StringUtils.isEmpty(model.getName())) {
                predicate.getExpressions().add(
                        criteriaBuilder.like(root.get("name"), "%" + model.getName() + '%'));
            }
            if (!StringUtils.isEmpty(model.getCreationBy())) {
                predicate.getExpressions().add(
                        criteriaBuilder.equal(root.get("creationBy"), model.getCreationBy()));
            }
            if (model.getCreationTimeStart() != null && model.getCreationTimeEnd() != null) {
                predicate.getExpressions().add(
                        criteriaBuilder.between(root.<Date>get("creationTime"), model.getCreationTimeStart(), model.getCreationTimeEnd())
                );
            }
            return predicate;
        }), pageable).getContent();
        return ret;
    }
}

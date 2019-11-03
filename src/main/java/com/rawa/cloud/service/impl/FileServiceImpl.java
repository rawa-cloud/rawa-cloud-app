package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.HttpJsonStatus;
import com.rawa.cloud.constant.Umask;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.Nas;
import com.rawa.cloud.domain.Record;
import com.rawa.cloud.domain.User;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.BeanHelper;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.model.file.FileAddModel;
import com.rawa.cloud.model.file.FilePatchModel;
import com.rawa.cloud.model.file.FileQueryModel;
import com.rawa.cloud.repository.FileRepository;
import com.rawa.cloud.repository.NasRepository;
import com.rawa.cloud.repository.UserRepository;
import com.rawa.cloud.service.AuthorityService;
import com.rawa.cloud.service.FileService;
import com.rawa.cloud.service.NasService;
import com.rawa.cloud.service.PreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    AuthorityService authorityService;

    @Autowired
    NasService nasService;

    @Autowired
    PreviewService previewService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NasRepository nasRepository;

    @Override
    public Long add(FileAddModel model) {
        Long parentId = model.getParentId();
        Umask umask = model.getDir() ? Umask.MK_DIR : Umask.NEW_FILE;
        hasAuthority(null, parentId, true, umask);
        File file = generateAddFile(model);
        file = fileRepository.save(file);
        return file.getId();
    }

    @Override
    public void update(Long id, FilePatchModel model) {
        File file = generatePatchFile(id, model);
        fileRepository.save(file);
    }

    @Override
    public List<File> query(FileQueryModel model) {
        List<File> files = fileRepository.findAll(generateQueryFile(model));
        return files.stream().filter(s -> {
            if (!hasOwnPersonal(s)) return false;
            try {
                hasAuthority(null, s.getId(), true, Umask.ACCESS);
                return true;
            } catch (AppException e) {
                return false;
            }

        }).collect(Collectors.toList());
    }

    @Override
    public File get(Long id) {
        hasAuthority(null, id, true, Umask.ACCESS);
        return fileRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void delete(Long id) {
//        hasAuthority(null, id, true, Umask.DELETE);
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        deleteFile(file);
    }

    @Override
    @Transactional
    public void recover(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        recoverFile(file);
    }

    @Override
    public java.io.File download(Long id) {
        hasAuthority(null, id, true, Umask.DOWNLOAD);
        File file = fileRepository.findById(id).orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        return nasService.download(file.getUuid(), true);
    }

    @Override
    public java.io.File preview(Long id) {
        hasAuthority(null, id, true, Umask.PREVIW);
        File file = fileRepository.findById(id).orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        return previewService.preview(nasService.download(file.getUuid(), true));
    }

    @Override
    public void hasAuthority(Long principleId, Long fileId, boolean isUser, Umask... umasks) throws AppException {
        if (principleId == null) {
            principleId = ContextHelper.getCurrentUserId();
            isUser = true;
        }
        User user = userRepository.findById(principleId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.USER_NOT_FOUND, principleId));
        if (fileId == null) {
            if (user.hasSuperRole()) return;
            throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, Arrays.stream(umasks).map(s -> s.desc));
        }
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        if (user.hasSuperRole()) return;

        if (user.hasAdminRole()) {
            if (user.isAdminFile(file)) return;
        }

        if (fileId == null)
            throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, Arrays.stream(umasks).map(s -> s.desc));

        boolean access = authorityService.hasAuthority(principleId, fileId, isUser, umasks);
        if (!access) throw new AppException(HttpJsonStatus.FILE_OPT_DENIED, Arrays.stream(umasks).map(s -> s.desc));
    }

//    @Override
//    public Long addRootFile(String username) {
//        File file = fileRepository.findFileByParentIsNullAndPersonalIsTrueAndStatusIsTrueAndCreationBy(username);
//        if (file != null) return file.getId();
//        file = new File();
//        file.setDir(true);
//        file.setStatus(true);
//        file.setPersonal(true);
//        file.setName("个人文件夹");
//        file.setLastChangeTime(new Date());
//        file.setLastChangeBy(username);
//        file.setCreationTime(new Date());
//        file.setCreationBy(username);
//        file = fileRepository.save(file);
//        return file.getId();
//    }

//    @Override
//    @Transactional
//    public void deleteRootFile(String username) {
//        File file = fileRepository.findFileByParentIsNullAndPersonalIsTrueAndStatusIsTrueAndCreationBy(username);
//        if (file == null) return;
//        deleteFile(file);
//    }

//    @Override
//    public String getUuidForDownload(Long id, Long recordId) {
//        File file = fileRepository.findById(id)
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
//        if (!file.getStatus()) throw new AppException(HttpJsonStatus.FILE_DELETED, id);
//        if (file.getDir()) throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, id);
//        Record record = null;
//        if (recordId != null) record = file.getRecords().stream().filter(s -> s.getId().equals(recordId)).findAny()
//                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_RECORD_NOT_FOUND, recordId));
//        if (record != null) {
//            hasAuthority(null, id, true, Umask.RECORD_DOWNLOAD);
//            return record.getUuid();
//        }
//        hasAuthority(null, id, true, Umask.DOWNLOAD);
//        return file.getUuid();
//    }


    // inner
    private File generateAddFile(FileAddModel model) {
        File file = new File();
        Long parentId = model.getParentId();
        File parent = parentId == null ? null : fileRepository.findById(parentId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
        if (parent != null && !parent.getDir())
            throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, parentId); // 不能是文件作为父级
        Boolean personal = model.getPersonal() != null ? model.getPersonal()
                : (parent != null ? parent.getPersonal() : false);
        boolean dir = model.getDir();

        if (exists(model.getName(), parentId)) throw new AppException(HttpJsonStatus.FILE_EXIST, model.getName());

        file.setName(model.getName()); // 设置名称
        file.setParent(parent); // 设置父级
        file.setDir(dir); // 设置文件/文件夹
        file.setPersonal(personal); // 设置是否是个人文件

        if (dir) { // 文件夹
            file.setLimitSize(model.getLimitSize()); // 设置文件夹容量
            file.setLimitSuffix(model.getLimitSuffix()); // 设置后缀限制
        } else { // 文件
            String uuid = model.getUuid();
            Nas nas = nasRepository.findNasByUuid(uuid);
            if (nas == null) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
            Long size = nas.getSize();
            String contentType = nas.getContentType();
            if (parent != null) {
                if (!checkCapacity(size, parentId)) throw new AppException(HttpJsonStatus.FILE_CAPACITY_OVERFLOW, size);
                if (!checkSuffix(contentType, parentId))
                    throw new AppException(HttpJsonStatus.FILE_ILLEGAL_SUFFIX, contentType);
            }

            file.setUuid(uuid); // 设置uuid
            file.setContentType(contentType); // 设置文件类型
            file.setSize(size); // 设置文件大小
        }

        file.setStatus(true); // 设置为有效
        file.setCreationBy(ContextHelper.getCurrentUsername()); // 设置创建人
        file.setLastChangeBy(ContextHelper.getCurrentUsername()); // 设置最后更改人
        file.setCreationTime(new Date()); // 设置创建时间
        file.setLastChangeTime(new Date()); // 设置最后更改时间

        return file;
    }

    private File generatePatchFile(Long id, FilePatchModel model) {
        File file = fileRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, id));
        File parent = file.getParent();
        String name = model.getName();
        String uuid = model.getUuid();
        if (name != null) {
            hasAuthority(null, id, true, Umask.RENAME);
            Long parentId = parent == null ? null : parent.getId();
            if (exists(name, parentId)) throw new AppException(HttpJsonStatus.FILE_EXIST, model.getName());
            file.setName(name);
        }
        if (uuid != null && !file.getDir()) { // 更新文件内容
            Nas nas = nasRepository.findNasByUuid(uuid);
            if (nas == null) throw new AppException(HttpJsonStatus.RAW_FILE_LOST, uuid);
            Long size = nas.getSize();
            file.setUuid(uuid);
            file.setSize(size);
            file.setLastChangeBy(ContextHelper.getCurrentUsername()); // 设置最后更改人
            file.setLastChangeTime(new Date()); // 设置最后更改时间
            // 记录更新轨迹
            Record record = new Record();
            record.setSize(size);
            record.setUuid(uuid);
            record.setRemark(model.getRemark());
            record.setLastChangeBy(ContextHelper.getCurrentUsername());
            record.setLastChangeTime(new Date());
            file.getRecords().add(record);
        }
        return file;
    }

    private Example<File> generateQueryFile(FileQueryModel model) {
        Long parentId = model.getParentId();
        File file = new File();
        file.setDir(model.getDir());
        file.setCreationBy(model.getCreationBy());
        file.setStatus(true); // 只能查询有效的文件
        file.setPersonal(model.getPersonal());
        ExampleMatcher matcher = null;
        if (parentId != null) {
            if (parentId < 0) {
                matcher = ExampleMatcher.matching()
                        .withIncludeNullValues()
                        .withIgnorePaths(BeanHelper.getNullFields(file, "parent"));
            } else {
                File parent = fileRepository.findById(parentId)
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
                file.setParent(parent);
            }
        }
        return matcher == null ? Example.of(file) : Example.of(file, matcher);
    }

    /**
     * 判断是否存在相同文件名
     *
     * @param filename
     * @param parentId
     * @return
     */
    private boolean exists(String filename, Long parentId) {
        List<File> siblings;
        if (parentId != null) {
            File parent = fileRepository.findById(parentId)
                    .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
            siblings = parent.getChildren();
        } else {
            siblings = fileRepository.findFilesByParentIsNullAndPersonalIsFalseAndStatusIsTrue();
        }
        return siblings.stream().anyMatch(s -> s.getName().equals(filename));
    }

    /**
     * 检查文件空间是否足够
     *
     * @param size
     * @param parentId
     * @return
     */
    private boolean checkCapacity(Long size, Long parentId) {
        File parent = fileRepository.findById(parentId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
        if (parent.getLimitSize() == null) return true;
        Long fullSize = parent.getLimitSize();
        Long usedSize = 0L;
        if (parent.getChildren() != null) {
            usedSize = parent.getChildren().stream().map(s -> s.getSize()).reduce((a, b) -> a + b).get();
        }
        return fullSize >= usedSize + size;
    }

    /**
     * 检查文件后缀
     *
     * @param suffix
     * @param parentId
     * @return
     */
    private boolean checkSuffix(String suffix, Long parentId) {
        File parent = fileRepository.findById(parentId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, parentId));
        String limitSuffixes = parent.getLimitSuffix();
        if (limitSuffixes == null || limitSuffixes.isEmpty()) return true;
        return limitSuffixes.contains(suffix);
    }

    /**
     * 判断个人文件是否属于自己
     *
     * @param file
     * @return
     */
    private boolean hasOwnPersonal(File file) {
        if (!file.getPersonal()) return true;
        return file.getCreationBy().equals(ContextHelper.getCurrentUsername());
    }

    /**
     * 删除文件以及子文件的相关资源
     *
     * @param file
     */
    private void deleteFile(File file) {
        List<File> files = file.getChildren();
        for (File f : files) {
            deleteFile(f);
        }
        // 删除文件权限
        authorityService.deleteByFile(file);
        fileRepository.delete(file);
    }

    /**
     * 恢复文件以及子文件
     *
     * @param file
     */
    private void recoverFile(File file) {
        List<File> files = file.getChildren();
        for (File f : files) {
            recoverFile(f);
        }
        // 删除文件权限
        file.setStatus(true);
        fileRepository.save(file);
    }
}

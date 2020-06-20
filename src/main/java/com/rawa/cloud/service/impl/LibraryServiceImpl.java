package com.rawa.cloud.service.impl;

import com.rawa.cloud.constant.*;
import com.rawa.cloud.core.excel.ExcelData;
import com.rawa.cloud.domain.*;
import com.rawa.cloud.exception.AppException;
import com.rawa.cloud.helper.ContextHelper;
import com.rawa.cloud.helper.ExcelHelper;
import com.rawa.cloud.model.library.*;
import com.rawa.cloud.properties.AppProperties;
import com.rawa.cloud.repository.*;
import com.rawa.cloud.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibraryServiceImpl implements LibraryService {

    @Autowired
    LogService logService;

    @Autowired
    PreviewService previewService;

    @Autowired
    NasService nasService;

    @Autowired
    FileService fileService;

    @Autowired
    LibraryCatalogRepository libraryCatalogRepository;

    @Autowired
    LibraryFieldDefRepository libraryFieldDefRepository;

    @Autowired
    LibraryTypeDictRepository libraryTypeDictRepository;

    @Autowired
    LibraryRepository libraryRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    AppProperties appProperties;

    @Override
    public List<LibraryCatalog> getLibCatalogs() {
        if (ContextHelper.getCurrentUser().hasSuperRole()) return libraryCatalogRepository.findAll();
        return libraryCatalogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            Predicate p1 = criteriaBuilder.equal(root.get("visibility"), LibraryVisibility.all);

            Predicate p2_1 = criteriaBuilder.equal(root.get("visibility"), LibraryVisibility.assign);
            Predicate p2_2 = criteriaBuilder.equal(root.join("authorityList", JoinType.LEFT).get("username"), ContextHelper.getCurrentUsername());
            Predicate p2 = criteriaBuilder.and(p2_1, p2_2);

            predicate.getExpressions().add(criteriaBuilder.or(p1, p2));

            criteriaQuery.groupBy(root.get("id"));
            return predicate;
        });
    }

    @Override
    public LibraryCatalog getLibCatalog(Long id) {
        return libraryCatalogRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Long addLibraryCatalog(LibraryCatalogAddModel model) {
        String name = model.getName();
        List<LibraryCatalog> exists = libraryCatalogRepository.findAllByName(model.getName());
        if (exists.size() > 0) {
            throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_EXISTS, name);
        }
        LibraryCatalog catalog = new LibraryCatalog();
        catalog.setName(name);
        catalog.setVisibility(model.getVisibility());
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.ADD).lc(name).end());
        return libraryCatalogRepository.save(catalog).getId();
    }

    @Override
    @Transactional
    public Long addLibraryCatalogField(LibraryCatalogFieldAddModel model) {
        Long catalogId = model.getCatalogId();
        String name = model.getName();
        LibraryFieldType type = model.getType();
        LibraryCatalog catalog = libraryCatalogRepository.findById(catalogId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_NOT_FOUND, catalogId));
        List<LibraryFieldDef> exists = libraryFieldDefRepository.findAllByNameAndCatalog(name, catalog);
        if (exists.size() > 0) {
            throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_FIELD_EXISTS, name);
        }
        LibraryFieldDef def = new LibraryFieldDef();
        def.setCatalog(catalog);
        def.setName(name);
        def.setType(type);
        def.setVisibility(model.getVisibility());
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.UPDATE).lc(catalog.getName()).st("新增字段")
                .add("名称", name, null)
                .add("类型", type.name(), null)
                .end());
        return libraryFieldDefRepository.save(def).getId();
    }

    @Override
    @Transactional
    public Long addLibraryCatalogFieldDict(LibraryCatalogFieldDictAddModel model) {
        Long fieldDefId = model.getFiledDefId();
        String name = model.getName();
        LibraryFieldDef fieldDef = libraryFieldDefRepository.findById(fieldDefId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, fieldDefId));
        if (!isEnumType(fieldDef)) {
            throw new AppException(HttpJsonStatus.LIBRARY_NO_ENUM_FIELD_TYPE, fieldDef.getType());
        }
        List<LibraryTypeDict> exists = libraryTypeDictRepository.findAllByNameAndFieldDef(name, fieldDef);
        if (exists.size() > 0) {
            throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_FIELD_DICT_EXISTS, name);
        }
        LibraryTypeDict dict = new LibraryTypeDict();
        dict.setName(name);
        dict.setFieldDef(fieldDef);
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.UPDATE)
                .lc(fieldDef.getFullName())
                .st("新增选项")
                .add("名称", name, null)
                .end());
        return libraryTypeDictRepository.save(dict).getId();
    }

    @Override
    @Transactional
    public void updateLibraryCatalog(Long id, LibraryCatalogUpdateModel model) {
        LibraryCatalog catalog = libraryCatalogRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_NOT_FOUND, id));
        String name = model.getName();
        if (!name.equals(catalog.getName())) {
            List<LibraryCatalog> exists = libraryCatalogRepository.findAllByName(model.getName());
            if (exists.size() > 0) {
                throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_EXISTS, name);
            }
        }
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.UPDATE)
                .lc(catalog.getName())
                .st("更新模版")
                .add("名称", name, catalog.getName())
                .add("可见性", model.getVisibility().name(), catalog.getVisibility() == null ? "" : catalog.getVisibility().name())
                .end());
        catalog.setName(name);
        catalog.setVisibility(model.getVisibility());
        libraryCatalogRepository.save(catalog);
    }

    @Override
    @Transactional
    public void updateLibraryCatalogField(Long id, LibraryCatalogFieldUpdateModel model) {
        LibraryFieldDef fieldDef = libraryFieldDefRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, id));
        String name = model.getName();
        LibraryFieldType type = model.getType();
        if (!name.equals(fieldDef.getName())) {
            List<LibraryFieldDef> exists = libraryFieldDefRepository.findAllByNameAndCatalog(name, fieldDef.getCatalog());
            if (exists.size() > 0) {
                throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_FIELD_EXISTS, name);
            }
        }
        Log log = Log.build(LogModule.LIB_CONFIG, LogType.UPDATE)
                .lc(fieldDef.getFullName())
                .st("更新字段");

        if(!fieldDef.getName().equals(name)){
            log.add("名称", name, fieldDef.getName());
            fieldDef.setName(name);
        }
        if(!fieldDef.getType().equals(type)){
            log.add("类型", type.name(), fieldDef.getType().name());
            fieldDef.setType(type);
        }
        fieldDef.setVisibility(model.getVisibility());
        logService.add(log.end());
        libraryFieldDefRepository.save(fieldDef);
        if (!isEnumType(fieldDef)) {
            libraryTypeDictRepository.deleteInBatch(fieldDef.getTypeDictList());
        }
    }

    @Override
    public void updateLibraryCatalogFieldDict(Long id, LibraryCatalogFieldDictUpdateModel model) {
        LibraryTypeDict dict = libraryTypeDictRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_DICT_NOT_FOUND, id));
        String name = model.getName();
        if (!name.equals(dict.getName())) {
            List<LibraryTypeDict> exists = libraryTypeDictRepository.findAllByNameAndFieldDef(name, dict.getFieldDef());
            if (exists.size() > 0) {
                throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_FIELD_DICT_EXISTS, name);
            }
        }
        Log log = Log.build(LogModule.LIB_CONFIG, LogType.UPDATE)
                .lc(dict.getFullName())
                .st("更新选项");
        if(!dict.getName().equals(name)){
            log.add("名称", name, dict.getName());
            dict.setName(name);
        }
        logService.add(log.end());
        libraryTypeDictRepository.save(dict);
    }

    @Override
    @Transactional
    public void deleteLibraryCatalog(Long id) {
        LibraryCatalog catalog = libraryCatalogRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_NOT_FOUND, id));
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.DELETE).st("模版").lc(catalog.getName()).end());
        libraryCatalogRepository.delete(catalog);
    }

    @Override
    @Transactional
    public void deleteLibraryCatalogField(Long id) {
        LibraryFieldDef fieldDef = libraryFieldDefRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, id));
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.DELETE).st("字段").
                lc(fieldDef.getFullName()).end());
        libraryFieldDefRepository.delete(fieldDef);
    }

    @Override
    public void deleteLibraryCatalogFieldDict(Long id) {
        LibraryTypeDict dict = libraryTypeDictRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_DICT_NOT_FOUND, id));
        logService.add(Log.build(LogModule.LIB_CONFIG, LogType.DELETE).st("选项").
                lc(dict.getFullName()).end());
        libraryTypeDictRepository.delete(dict);
    }

    @Override
    public Page<Library> getLibraries(LibraryQueryModel model) {
        return libraryRepository.findAll(getLibrarySpec(model), model.toPage(false, "updatedDate"));
    }

    @Override
    public java.io.File exportLibraries(LibraryExportModel model) {
        Long catalogId = model.getCatalogId();
        LibraryCatalog catalog = libraryCatalogRepository.findById(catalogId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_NOT_FOUND, catalogId));
        List<String> titles = catalog.getFieldDefs().stream()
                .filter(s -> Boolean.TRUE.equals(s.getVisible()))
                .map(s -> s.getName())
                .collect(Collectors.toList());
        List<Library> data = libraryRepository.findAll(getLibrarySpec(model.toQueryModel()), Sort.by(Sort.Direction.DESC, "updatedDate"));
        List<List<Object>> rows = data.stream()
                .map(s -> {
                    return titles.stream()
                            .map(p -> {
                                String value = "";
                                for (LibraryField f: s.getFields()) {
                                    if (p.equals(f.getName())) {
                                        if (!StringUtils.isEmpty(f.getValue())) {
                                            value = f.getValue().replaceAll("\"", "");
                                        }
                                        break;
                                    }
                                }
                                return (Object) value;
                            }).collect(Collectors.toList());
                }).collect(Collectors.toList());
        ExcelData excelData = new ExcelData();
        excelData.setTitles(titles);
        excelData.setRows(rows);
        java.io.File file = new java.io.File(appProperties.getTemp(), UUID.randomUUID().toString() + ".xlsx");
        ExcelHelper.generateExcel(excelData, file);
        return file;
    }

    @Override
    @Transactional
    public Long addLibrary(LibraryAddModel model) {
        Long catalogId = model.getCatalogId();
//        String name = model.getName();
        LibraryCatalog catalog = libraryCatalogRepository.findById(catalogId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_NOT_FOUND, catalogId));
//        List<Library> exists = libraryRepository.findAllByNameAndCatalog(name, catalog);
//        if (exists.size() > 0) {
//            throw new AppException(HttpJsonStatus.LIBRARY_EXISTS, name);
//        }
        Library library = new Library();
        library.setName(UUID.randomUUID().toString());
        library.setVisibility(model.getVisibility());
        library.setCatalog(catalog);
        // 添加权限
        LibraryAuthority authority = new LibraryAuthority();
        authority.setUsername(ContextHelper.getCurrentUsername());
        authority.setOpt(LibraryAuthorityOpt.a);
        library = libraryRepository.save(library);
        authority.setLibrary(library);
        List<LibraryAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        library.setAuthorityList(authorities);
        libraryRepository.save(library);
        logService.add(Log.build(LogModule.LIB, LogType.ADD).st("添加库").
                lc(catalog.getName()).add("可见性", library.getVisibility().name(), null).end());
        return library.getId();
    }

    @Override
    @Transactional
    public Long copyLibrary(Long id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        Library entity = new Library();
        entity.setName(library.getName());
        entity.setCatalog(library.getCatalog());

        entity = libraryRepository.save(entity);

        List<LibraryField> fields = new LinkedList<>();
        if (!CollectionUtils.isEmpty(library.getFieldList())) {
            for (LibraryField f: library.getFieldList()) {
                LibraryField item = new LibraryField();
                item.setFieldDef(f.getFieldDef());
                item.setLibrary(entity);
                item.setValue(f.getValue());
                fields.add(item);
            }
        }
        entity.setFieldList(fields);
        entity.setVisibility(library.getVisibility());
        if(LibraryVisibility.assign.equals(entity.getVisibility())) {
            List<LibraryAuthority> authorities = new LinkedList<>();
            if (!CollectionUtils.isEmpty(library.getAuthorityList())) {
                for (LibraryAuthority a: library.getAuthorityList()) {
                    LibraryAuthority item = new LibraryAuthority();
                    item.setLibrary(entity);
                    item.setOpt(a.getOpt());
                    item.setUsername(a.getUsername());
                    authorities.add(item);
                }
            }
            entity.setAuthorityList(authorities);
        }
        return entity.getId();
    }

    @Override
    public void updateLibrary(Long id, LibraryUpdateModel model) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
//        String name = model.getName();
//        if (!name.equals(library.getName())) {
//            List<Library> exists = libraryRepository.findAllByNameAndCatalog(name, library.getCatalog());
//            if (exists.size() > 0) {
//                throw new AppException(HttpJsonStatus.LIBRARY_CATALOG_FIELD_DICT_EXISTS, name);
//            }
//        }
        logService.add(Log.build(LogModule.LIB, LogType.UPDATE).st("更新库").
                lc(library.getFullName()).add("名称", model.getVisibility().name(), library.getVisibility() == null ? "" : library.getVisibility().name()).end());
        library.setVisibility(model.getVisibility());
        libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void deleteLibrary(Long id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        logService.add(Log.build(LogModule.LIB, LogType.DELETE)
                .lc(library.getFullName()).end());
        libraryRepository.delete(library);
    }

    @Override
    @Transactional
    public void updateLibraryFields(Long id, List<LibraryFieldAddModel> model) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        List<LibraryField> fields = library.getFieldList();
        Log log = Log.build(LogModule.LIB, LogType.UPDATE).lc(library.getFullName()).st("更新字段");
        for(LibraryFieldAddModel row : model) {
            LibraryField target = null;
            for (LibraryField field : fields) {
                if (field.getFieldDef().getId().equals(row.getFieldDefId())) target = field;
            }
            if (target == null) {
                target = new LibraryField();
                LibraryFieldDef fieldDef = libraryFieldDefRepository.findById(row.getFieldDefId())
                        .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, row.getFieldDefId()));
                target.setFieldDef(fieldDef);
                target.setLibrary(library);
                fields.add(target);
            }
            if (!target.getFieldDef().getDisabled()) {
                log.add(target.getName(), row.getValue(), target.getValue());
                target.setValue(row.getValue());
            }
        }
        logService.add(log.end());
        libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void addLibraryFile(Long id, Long fileId) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        File file = fileRepository.findById(fileId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.FILE_NOT_FOUND, fileId));
        library.getFileList().clear();
        library.getFileList().add(file);
        logService.add(Log.build(LogModule.LIB, LogType.UPDATE).st("更新文件")
                .lc(library.getFullName()).add("文件路径", file.getPath(), null).end());
        libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void updateLibraryAuthorities(Long id, List<LibraryAuthorityAddModel> model) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        List<LibraryAuthority> authorities = library.getAuthorityList();
        List<LibraryAuthority> ret = new ArrayList<>();
        for(LibraryAuthorityAddModel row : model) {
            LibraryAuthority target = null;
            for (LibraryAuthority item : authorities) {
                if (item.getUsername().equals(row.getUsername())) target = item;
            }
            if (target == null) {
                target = new LibraryAuthority();
                target.setLibrary(library);
                target.setUsername(row.getUsername());
            }
            target.setOpt(row.getOpt());
            ret.add(target);
        }
        library.getAuthorityList().clear();
        library.getAuthorityList().addAll(ret);

        String auth = library.getAuthorityList().stream().map(s -> s.getUsername() + "," + (s.getOpt() == null ? "n" : s.getOpt().name()))
                .collect(Collectors.toList()).toString();

        logService.add(Log.build(LogModule.LIB, LogType.UPDATE).st("更新权限")
            .lc(library.getFullName())
            .add("权限", auth, null).end());
        libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void updateLibraryCatalogAuthorities(Long id, List<LibraryAuthorityAddModel> model) {
        LibraryCatalog catalog = libraryCatalogRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, id));
        List<LibraryCatalogAuthority> authorities = catalog.getAuthorityList();
        List<LibraryCatalogAuthority> ret = new ArrayList<>();
        for(LibraryAuthorityAddModel row : model) {
            LibraryCatalogAuthority target = null;
            for (LibraryCatalogAuthority item : authorities) {
                if (item.getUsername().equals(row.getUsername())) target = item;
            }
            if (target == null) {
                target = new LibraryCatalogAuthority();
                target.setCatalog(catalog);
                target.setUsername(row.getUsername());
            }
            target.setOpt(row.getOpt());
            ret.add(target);
        }
        catalog.getAuthorityList().clear();
        catalog.getAuthorityList().addAll(ret);

        String auth = catalog.getAuthorityList().stream().map(s -> s.getUsername() + "," + s.getOpt().name())
                .collect(Collectors.toList()).toString();
        logService.add(Log.build(LogModule.LIB, LogType.UPDATE).st("更新权限")
                .lc(catalog.getName())
                .add("权限", auth, null).end());
        libraryCatalogRepository.save(catalog);
    }

    @Override
    @Transactional
    public void updateLibraryFieldDefAuthorities(Long id, List<LibraryAuthorityAddModel> model) {
        LibraryFieldDef fieldDef = libraryFieldDefRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, id));
        List<LibraryFieldDefAuthority> authorities = fieldDef.getAuthorityList();
        List<LibraryFieldDefAuthority> ret = new ArrayList<>();
        for(LibraryAuthorityAddModel row : model) {
            LibraryFieldDefAuthority target = null;
            for (LibraryFieldDefAuthority item : authorities) {
                if (item.getUsername().equals(row.getUsername())) target = item;
            }
            if (target == null) {
                target = new LibraryFieldDefAuthority();
                target.setFieldDef(fieldDef);
                target.setUsername(row.getUsername());
            }
            target.setOpt(row.getOpt());
            ret.add(target);
        }
        fieldDef.getAuthorityList().clear();
        fieldDef.getAuthorityList().addAll(ret);

        String auth = fieldDef.getAuthorityList().stream().map(s -> s.getUsername() + "," + s.getOpt().name())
                .collect(Collectors.toList()).toString();
        logService.add(Log.build(LogModule.LIB, LogType.UPDATE).st("更新权限")
                .lc(fieldDef.getCatalog().getName() + "/" + fieldDef.getName())
                .add("权限", auth, null).end());
        libraryFieldDefRepository.save(fieldDef);
    }

    @Override
    public java.io.File previewFile(Long id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        File file = library.getFile();
        if (file == null || file.getDir()) throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, null);
        return previewService.preview(nasService.download(file.getUuid(), true), ContextHelper.getCurrentUsername());
    }

    @Override
    public java.io.File downloadFile(Long id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_NOT_FOUND, id));
        File file = library.getFile();
        if (file == null || file.getDir()) throw new AppException(HttpJsonStatus.FILE_NOT_FOUND, null);
        return fileService.download(file.getId(), true);
    }

    private boolean isEnumType (LibraryFieldDef fieldDef) {
        return LibraryFieldType.radio.equals(fieldDef.getType()) || LibraryFieldType.checkbox.equals(fieldDef.getType());
    }

    private Specification<Library> getLibrarySpec (LibraryQueryModel model) {
        Long catalogId = model.getCatalogId();
        String name = model.getName();
        LibraryCatalog catalog = libraryCatalogRepository.findById(catalogId)
                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_NOT_FOUND, catalogId));
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate.getExpressions().add(criteriaBuilder.equal(root.get("catalog"), catalog));
            if (!ContextHelper.getCurrentUser().hasSuperRole()) {
                Predicate p1 = criteriaBuilder.equal(root.get("visibility"), LibraryVisibility.all);

                Predicate p2_1 = criteriaBuilder.equal(root.get("visibility"), LibraryVisibility.assign);
                Predicate p2_2 = criteriaBuilder.equal(root.join("authorityList", JoinType.LEFT).get("username"), ContextHelper.getCurrentUsername());
                Predicate p2 = criteriaBuilder.and(p2_1, p2_2);

                predicate.getExpressions().add(criteriaBuilder.or(p1, p2));
            }

            if (!StringUtils.isEmpty(name)) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("name"), name));
            }

            if (model.getParams() != null && model.getParams().size() > 0) {
                Map<String, Object> params = model.getParams();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    Object value = entry.getValue();
                    Long key = Long.valueOf(entry.getKey());
                    boolean isEmpty = false;
                    if (value == null) isEmpty = true;
                    if (value instanceof String && StringUtils.isEmpty(value)) isEmpty = true;
                    if (value instanceof List && CollectionUtils.isEmpty((List)value)) isEmpty = true;
                    if (!isEmpty) {
                        LibraryFieldDef def = libraryFieldDefRepository.findById(key)
                                .orElseThrow(AppException.optionalThrow(HttpJsonStatus.LIBRARY_CATALOG_FIELD_NOT_FOUND, key));
                        boolean isText = LibraryFieldType.text.equals(def.getType())
                                || LibraryFieldType.multi_text.equals(def.getType())
                                || LibraryFieldType.radio.equals(def.getType());

                        boolean isBool = LibraryFieldType.bool.equals(def.getType());

                        boolean isMultiple = LibraryFieldType.checkbox.equals(def.getType());

                        boolean isRange = LibraryFieldType.date.equals(def.getType())
                                || LibraryFieldType.datetime.equals(def.getType());

                        Predicate p = criteriaBuilder.equal(root.join("fieldList").join("fieldDef").get("id"),key);

                        if (isText) {
                            Predicate p1 = criteriaBuilder.equal(root.join("fieldList").get("value"), "\"" + value + "\"");
                            predicate.getExpressions().add(criteriaBuilder.and(p, p1));
                        } else if (isMultiple) {
                            Predicate p1 = criteriaBuilder.like(root.join("fieldList").get("value"), "%" + value + "%");
                            predicate.getExpressions().add(criteriaBuilder.and(p, p1));
                        } else if (isRange) {
                            String v1 = String.valueOf(((List)value).get(0));
                            String v2 = String.valueOf(((List)value).get(1));
                            if (!StringUtils.isEmpty(v1) && !StringUtils.isEmpty(v2)) {
                                Predicate p1 = criteriaBuilder.between(root.join("fieldList").get("value"),
                                        "\"" + ((List)value).get(0) + "\"", "\"" + ((List)value).get(1) + "\"");
                                predicate.getExpressions().add(criteriaBuilder.and(p, p1));
                            }
                        }
                    }
                }
            }

            criteriaQuery.groupBy(root.get("id"));
            return predicate;
        };
    }
}

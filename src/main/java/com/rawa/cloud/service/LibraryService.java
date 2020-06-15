package com.rawa.cloud.service;

import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.Library;
import com.rawa.cloud.domain.LibraryCatalog;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.dept.DeptQueryModel;
import com.rawa.cloud.model.dept.DeptUpdateModel;
import com.rawa.cloud.model.library.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.List;

public interface LibraryService {

    List<LibraryCatalog> getLibCatalogs();

    LibraryCatalog getLibCatalog(Long id);

    Long addLibraryCatalog(LibraryCatalogAddModel model);

    Long addLibraryCatalogField(LibraryCatalogFieldAddModel model);

    Long addLibraryCatalogFieldDict(LibraryCatalogFieldDictAddModel model);

    void updateLibraryCatalog(Long id, LibraryCatalogUpdateModel model);

    void updateLibraryCatalogField(Long id, LibraryCatalogFieldUpdateModel model);

    void updateLibraryCatalogFieldDict(Long id, LibraryCatalogFieldDictUpdateModel model);

    void deleteLibraryCatalog(Long id);

    void deleteLibraryCatalogField(Long id);

    void deleteLibraryCatalogFieldDict(Long id);

    // Library

    Page<Library> getLibraries(LibraryQueryModel model);

    File exportLibraries(LibraryExportModel model);

    Long addLibrary(LibraryAddModel model);

    Long copyLibrary(Long id);

    void updateLibrary(Long id, LibraryUpdateModel model);

    void deleteLibrary(Long id);

    void updateLibraryFields(Long id, List<LibraryFieldAddModel> model);

    void addLibraryFile(Long id, Long fileId);

    void updateLibraryAuthorities(Long id, List<LibraryAuthorityAddModel> model);

    void updateLibraryCatalogAuthorities(Long id, List<LibraryAuthorityAddModel> model);

    void updateLibraryFieldDefAuthorities(Long id, List<LibraryAuthorityAddModel> model);


    java.io.File previewFile (Long id);

    java.io.File downloadFile (Long id);
}
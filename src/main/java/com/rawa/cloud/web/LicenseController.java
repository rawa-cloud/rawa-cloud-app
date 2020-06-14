package com.rawa.cloud.web;

import com.rawa.cloud.bean.Licence;
import com.rawa.cloud.helper.FileHelper;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.license.LicenseAddModel;
import com.rawa.cloud.service.LicenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(tags = "License相关接口")
@RestController
@RequestMapping("/license")
public class LicenseController {

    @Autowired
    LicenseService licenseService;

    @ApiOperation("获取License")
    @GetMapping("")
    public JsonResult<Licence> get () {
        return JsonResult.success(licenseService.getLicense());
    }

    @ApiOperation("写入License")
    @PostMapping("")
    public JsonResult add (@Valid @RequestBody LicenseAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        licenseService.writeLicense(model.getText());
        return JsonResult.success(null);
    }
}

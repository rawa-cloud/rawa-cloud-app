package com.rawa.cloud.web;

import com.rawa.cloud.domain.Property;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.helper.ValidationHelper;
import com.rawa.cloud.model.dept.DeptAddModel;
import com.rawa.cloud.model.property.PropertyAddModel;
import com.rawa.cloud.service.PropertyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "参数相关接口")
@RestController
@RequestMapping("/properties")
public class PropertyController {

    @Autowired
    PropertyService propertyService;

    @ApiOperation("查询")
    @GetMapping("")
    public JsonResult<List<Property>> query() {
        return JsonResult.success(propertyService.query());
    }

    @ApiOperation("添加")
    @PostMapping("")
    public JsonResult add(@Valid @RequestBody PropertyAddModel model, BindingResult result) {
        ValidationHelper.validate(result);
        propertyService.add(model.getName(), model.getValue(), true);
        return JsonResult.success(null);
    }
}

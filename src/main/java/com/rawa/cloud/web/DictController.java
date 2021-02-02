package com.rawa.cloud.web;

import com.rawa.cloud.domain.Dict;
import com.rawa.cloud.helper.JsonResult;
import com.rawa.cloud.service.DictService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "字典相关接口")
@RestController
@RequestMapping("/dicts")
public class DictController {

    @Autowired
    DictService dictService;

    @PostMapping("/initDict")
    public JsonResult initDict (@RequestBody List<Dict> dictionaries) {
        dictService.initData(dictionaries);
        return JsonResult.success(null);
    }

    @GetMapping("")
    public JsonResult<List<Dict>> loadAll () {
        return JsonResult.success(dictService.loadAll());
    }
}

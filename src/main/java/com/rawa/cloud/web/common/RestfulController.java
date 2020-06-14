package com.rawa.cloud.web.common;

import com.rawa.cloud.helper.JsonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public abstract class RestfulController<E, A, U, Q> {
    @ApiOperation("新增")
    @PostMapping("")
    public abstract JsonResult<Long> add (@Valid @RequestBody A model, BindingResult result);

    @ApiOperation("更新")
    @PutMapping("/{id}")
    public abstract JsonResult update (@PathVariable Long id, @Valid @RequestBody U model, BindingResult result);

    @ApiOperation("查询")
    @GetMapping("")
    public abstract JsonResult<List<E>> query (@Valid Q model, BindingResult result);

    @ApiOperation("获取")
    @GetMapping("/{id}")
    public abstract JsonResult<E> get (@PathVariable Long id);

    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public abstract JsonResult delete (@PathVariable Long id);
}

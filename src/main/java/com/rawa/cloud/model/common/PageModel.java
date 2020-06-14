package com.rawa.cloud.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public abstract class PageModel<E> extends BaseModel<E> {

    @ApiModelProperty(value = "分页页码", required = true)
    @NotNull
    private Integer page;

    @ApiModelProperty(value = "分页每页条数", required = true)
    @NotNull
    private int size;

    public Pageable toPage () {
        return new PageRequest(page, size);
    }

    public Pageable toPage (boolean asc, String property) {
        return new PageRequest(page, size, Sort.by(asc ? Sort.Direction.DESC : Sort.Direction.DESC, property));
    }
}

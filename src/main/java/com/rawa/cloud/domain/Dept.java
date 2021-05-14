package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.CascadeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.LinkedList;
import java.util.List;

@ApiModel
@Data
@Entity
public class Dept extends CascadeEntity<Dept> {

    @ApiModelProperty(value = "部门名称")
    private String name;

    private String code;

    private String parentCode;

    private String areaCode;

    private String thirdId;

    @Transient
    private Boolean synced;

    @JsonIgnore
    public boolean isDefaultDept () {
        return getParent() == null;
    }

    @Transient
    @JsonIgnore
    public List<Dept> getParents () {
        List<Dept> ret = new LinkedList<>();
        Dept current = this;
        while (current != null) {
            ret.add(current);
            current = current.getParent();
        }
        return ret;
    }

}

package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.Date;


@Data
@Entity
public class Recycle extends BaseEntity {

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private File file;

    @ApiModelProperty(value = "创建人")
    private String creationBy;

    @ApiModelProperty(value = "创建时间")
    private Date creationTime;

    @JsonIgnore
    private Boolean personal;

    @Transient
    public String getPath () {
        return this.file == null ? "" : this.file.getPath();
    }

    @Transient
    public Long getSize () {
        return this.file == null ? null : this.file.getSize();
    }


}


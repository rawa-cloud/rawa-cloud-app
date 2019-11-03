package com.rawa.cloud.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.File;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class Authority<E> extends BaseEntity{

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private E principle;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    private File file;

    @ApiModelProperty(value = "权限掩码")
    private Long umask;

    @JsonIgnore
    private Date expiryTime;

    @ApiModelProperty(value = "实体ID")
    public abstract Long getPrincipleId ();

    @ApiModelProperty(value = "实体是否用户")
    public abstract Boolean getIsUser ();

    @ApiModelProperty(value = "文件ID")
    public Long getFileId () {
        return this.file != null ? this.file.getId() : null;
    }

    @ApiModelProperty(value = "文件路径")
    public String getFilePath () {
        return this.file != null ? this.file.getPath() : null;
    }

    @ApiModelProperty(value = "有效天数")
    public Long getValidDays () {
        if (this.expiryTime == null) return null;
        LocalDate now = LocalDate.now();
        LocalDate to = this.expiryTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(now, to);
    }

    public void setValidDays (Long days) {
        if (days == null) {
            this.expiryTime = null;
            return;
        }
        LocalDate now = LocalDate.now();
        LocalDate to = now.plusDays(days);
        Date d = Date.from(to.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.expiryTime = d;
    }
}

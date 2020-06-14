package com.rawa.cloud.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.domain.Dept;
import com.rawa.cloud.domain.File;
import com.rawa.cloud.domain.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class Authority<E extends BaseEntity> extends BaseEntity{

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

    @Transient
    private boolean implicit;

    @ApiModelProperty(value = "文件ID")
    public Long getFileId () {
        return this.file != null ? this.file.getId() : null;
    }

    @ApiModelProperty(value = "文件名称")
    public String getFileName () {
        return this.file != null ? this.file.getName() : null;
    }

    @ApiModelProperty(value = "是否文件夹")
    public Boolean getDir () {
        return this.file != null ? this.file.getDir() : null;
    }

    @ApiModelProperty(value = "文件类型")
    public String getContentType () {
        return this.file != null ? this.file.getContentType() : null;
    }

    @ApiModelProperty(value = "有效天数")
    public Long getValidDays () {
        if (this.expiryTime == null) return null;
        LocalDate now = LocalDate.now();
        LocalDate to = this.expiryTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(now, to);
    }

    @ApiModelProperty(value = "实体ID")
    public Long getPrincipleId () {
        return principle != null ? principle.getId() : null;
    }

    @ApiModelProperty(value = "是否用户")
    public boolean isUser () {
        return principle instanceof User;
    }

    @ApiModelProperty(value = "实体名称")
    public String getPrincipleName () {
        if (principle instanceof Dept) return ((Dept) principle).getName();
        else if (principle instanceof User) return ((User) principle).getUsername();
        return "";
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

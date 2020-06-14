package com.rawa.cloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rawa.cloud.constant.LogModule;
import com.rawa.cloud.constant.LogType;
import com.rawa.cloud.domain.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@ApiModel
@Data
@Entity
public class Log extends BaseEntity {

    @Data
    private static class LogField {
        private LogField (String name, String newValue, String oldValue) {
            this.name = name;
            this.newValue = newValue;
            this.oldValue = oldValue;
        }
        private String name;

        private String oldValue;

        private String newValue;
    }

    public static Log build (LogModule module, LogType type) {
        Log log = new Log();
        log.setModule(module);
        log.setType(type);
        return log;
    }

    @JsonIgnore
    @Transient
    private List<LogField> logFields;

    @JsonIgnore
    @Transient
    private String smallType;

    @JsonIgnore
    @Transient
    private String location;

    @ApiModelProperty(value = "功能模块")
    private LogModule module;

    @ApiModelProperty(value = "操作类型")
    private LogType type;

    @ApiModelProperty(value = "操作备注")
    @Column(length = 255 * 16)
    private String remark;

    @ApiModelProperty(value = "操作人")
    private String operateBy;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Log st (String smallType) {
        this.smallType = smallType;
        return this;
    }

    public Log lc (Object location) {
        this.location = location.toString();
        return this;
    }

    public Log add (String name, String newValue, String oldValue) {
        if (this.logFields == null) this.logFields = new LinkedList<>();
        this.logFields.add(new LogField(name, newValue, oldValue));
        return this;
    }

    public Log end () {
        String text = String.format("[模块: %s, 类型: %s, 标识: %s] ", this.module.desc, this.type.desc, this.location);
        StringBuilder sb = new StringBuilder(text);
        if (this.smallType != null) {
            sb.append("子类型: ").append(this.smallType).append("; ");
        }
        if (this.logFields != null) {
            for(LogField field: this.logFields) {
                sb.append(field.getName()).append(": ");
                if (field.getOldValue() != null) {
                    sb.append("由<").append(field.getOldValue()).append(">更新为<").append(field.getNewValue()).append(">; ");
                } else {
                    sb.append("设置为<").append(field.getNewValue()).append(">; ");
                }
            }
        }
        int max = 255 * 15;
        String ret = sb.toString();
        this.remark = ret.length() > max ? ret.substring(0, max): ret;
        return this;
    }
}


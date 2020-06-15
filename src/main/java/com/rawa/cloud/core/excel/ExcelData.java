package com.rawa.cloud.core.excel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExcelData implements Serializable {
    private List<String> titles;

    /**
     * 数据
     */
    private List<List<Object>> rows;

    /**
     * 页签名称
     */
    private String name;
}

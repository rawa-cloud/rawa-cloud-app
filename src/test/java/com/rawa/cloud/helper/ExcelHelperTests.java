package com.rawa.cloud.helper;

import com.rawa.cloud.core.excel.ExcelData;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExcelHelperTests {

    @Test
    public void testGenerateExcel() {
        ExcelData data = new ExcelData();
        data.setTitles(Arrays.asList(new String[]{"姓名", "年龄", "地址"}));
        List<List<Object>> rows = new LinkedList<>();
        rows.add(Arrays.asList(new String[]{"张银", "28", "上海浦东张江"}));
        rows.add(Arrays.asList(new String[]{"Alison", "23", "New York USA"}));
        data.setRows(rows);
        ExcelHelper.generateExcel(data, new File("/Users/zhangyin/IdeaProjects/rawa-cloud-app/exports/1.xlsx"));
    }
}
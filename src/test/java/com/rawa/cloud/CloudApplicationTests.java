package com.rawa.cloud;

import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudApplicationTests {

    @Autowired
    private DocumentConverter documentConverter;

    @Test
    public void contextLoads() {
        System.out.println("hello world");
    }

    @Test
    public void testConvertOffice () throws OfficeException {
//        String base = "C:\\Users\\zhang\\IdeaProjects\\rawa-cloud-app\\office\\";
        String base = "/Users/zhangyin/IdeaProjects/rawa-cloud-app/office/";

        // 源文件 （office）
        File source = new File(base + "1.xlsx");
        // 源文件 （pdf）
        File target = new File(base + "1.pdf");
        // 转换文件
        if (!target.exists()) {
            documentConverter.convert(source).to(target).execute();
        }

        //
        // 源文件 （office）
        source = new File(base + "2.pptx");
        // 源文件 （pdf）
        target = new File(base + "2.pdf");
        // 转换文件
        if (!target.exists()) {
            documentConverter.convert(source).to(target).execute();
        }

        //
        // 源文件 （office）
        source = new File(base + "3.docx");
        // 源文件 （pdf）
        target = new File(base + "3.pdf");
        // 转换文件
        if (!target.exists()) {
            documentConverter.convert(source).to(target).execute();
        }
    }


}

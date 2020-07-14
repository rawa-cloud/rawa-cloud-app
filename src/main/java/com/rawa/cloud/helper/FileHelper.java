package com.rawa.cloud.helper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class FileHelper {

    public static String getSuffix (String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public static ResponseEntity<FileSystemResource> download(java.io.File file, HttpServletResponse response) {
        String filename = file.getName();
        filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }

    public static ResponseEntity<FileSystemResource> downloadWithCache(java.io.File file, HttpServletResponse response) {
        String filename = file.getName();
        filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "max-age=30,only-if-cached");
        headers.add("Content-Disposition", "attachment; filename=" + filename);
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }

    public static boolean delete (File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                delete(f);
            }
            return file.delete();
        }
    }

    public static String formatSize (Long value) {
        if (value == null) return "未设置";
        String mode = "K";
        int i = 0;
        if (value / Math.pow(1024, 3) >= 1) { mode = "G"; i = 2; }
        else if (value / Math.pow(1024, 2) < 1) { mode = "K"; i = 0; }
        else { mode = "M"; i = 1; }
        double ratio = Math.pow(1024, i + 1);
        return  new DecimalFormat("#.00").format(value / ratio) + mode;
    }
}

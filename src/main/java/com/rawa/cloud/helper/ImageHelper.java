package com.rawa.cloud.helper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class ImageHelper {
    static final List<String> IMAGE_FILES = Arrays.asList("gif", "ico", "jpg", "jpeg", "png");

    public static File scale (File source, File target, Integer height, Integer width) {
        if (height == null && width == null) return source;
        try {
            Image srcImg = ImageIO.read(source);
            int w = srcImg.getWidth(null);
            int h = srcImg.getHeight(null);
            if (height == null) {
                height = (int)(width * h / w);
            }
            if (width == null) {
                width = (int)(height * w / h);
            }
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buffImg.createGraphics();
            g.drawImage(srcImg, 0, 0, width, height, null);
            g.dispose();
            FileOutputStream outputStream = new FileOutputStream(target);
            ImageIO.write(buffImg, "jpg", outputStream);
            outputStream.flush();
            outputStream.close();
            return target;
        } catch (Exception e) {
            System.out.println(e);
            return source;
        }
    }

    public static boolean isImage(File file) {
        String suffix = FileHelper.getSuffix(file.getName());
        return IMAGE_FILES.contains(suffix.toLowerCase());
    }
}

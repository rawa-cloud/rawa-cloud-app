package com.rawa.cloud.helper;

import com.rawa.cloud.constant.HorizontalPosition;
import com.rawa.cloud.constant.VerticalPosition;
import com.rawa.cloud.domain.Watermark;
import javafx.geometry.VPos;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class WaterMarkHelper {
//    private static float spaceRatio = 0.1f;
    private static int minGutter = 36;

    public static File addWatermark(Watermark watermark, File source, File target, File logo, String watermarkContent) {
        try {
            Image srcImg = ImageIO.read(source);
            //获取图片的宽和高
            int width = srcImg.getWidth(null);
            int height = srcImg.getHeight(null);

            //画水印需要一个画板    创建一个画板
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //创建一个2D的图像
            Graphics2D g = buffImg.createGraphics();
            //画出来
            g.drawImage(srcImg, 0, 0, width, height, null);

            float widthRadio = watermark.getWidthRadio() == null ? 0.6f : watermark.getWidthRadio();
            float logoWidthRatio = watermark.getLogoWidthRadio() == null ? 0.3f : watermark.getLogoWidthRadio();
            VerticalPosition verticalPosition = watermark.getVerticalPosition() == null ? VerticalPosition.center : watermark.getVerticalPosition();
            HorizontalPosition horizontalPosition = watermark.getHorizontalPosition() == null ? HorizontalPosition.center : watermark.getHorizontalPosition();
            float opacity = watermark.getOpacity() == null ? 0.4f : watermark.getOpacity();

            int watermarkWidth = (int)(width * widthRadio);
            int logoWidth = (int)(watermarkWidth * logoWidthRatio);
            int textGutter = 12;

            String[] text = watermarkContent.split("\n");
            int textWidth = watermarkWidth - logoWidth - textGutter;
            int fontSize = getFontSize(textWidth, text);
            int lineHeight = (int)(fontSize * 1.5);
            int textHeight = lineHeight * text.length;

            int spaceWidth = 12;
            int spaceHeight = 12;

            //设置水印的颜色
            Color color = new Color(0, 0, 0, 255);
            g.setColor(color);

//            g.rotate(Math.toRadians(-45),
//                    (double) buffImg.getWidth() / 2, (double) buffImg
//                            .getHeight() / 2);
            int x = 0;
            int y = 0;
            if (logo != null) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,opacity));
                Image logoImage = ImageIO.read(logo);
                int w = logoImage.getWidth(null);
                int h = logoImage.getHeight(null);
//                logoWidth = Math.min(maxWidth, w);
                int logoHeight = h * logoWidth / w;
                if (horizontalPosition.equals(HorizontalPosition.left)) {
                    x = spaceWidth;
                } else if (horizontalPosition.equals(HorizontalPosition.center)) {
                    x = Math.max((int)((width - watermarkWidth) / 2), 0);
                } else {
                    x = Math.max(width - spaceWidth - watermarkWidth, 0);
                }

                if (verticalPosition.equals(VerticalPosition.top)) {
                    y = spaceHeight;
                } else if (horizontalPosition.equals(HorizontalPosition.center)) {
                    y = (height - logoHeight) / 2;
                } else {
                    y = height - spaceHeight - Math.max(logoHeight, textHeight);
                }
                g.drawImage(logoImage, x, y, logoWidth, logoHeight, null);
            }

            //设置水印的字体

            //设置水印坐标
            if (!StringUtils.isEmpty(watermarkContent)) {
                Font font = new Font("微软雅黑",Font.PLAIN, fontSize);
                g.setFont(font);

                x = x + logoWidth + textGutter;
                if (verticalPosition.equals(VerticalPosition.center)) {
                    y = (height - textHeight) / 2 + fontSize;
                } else {
                    y = y + fontSize;
                }
                for (String t: text) {
                    g.drawString(t, x, y);
                    y += lineHeight;
                }
            }

            //根据获取的坐标 在相应的位置画出水印


            //释放画板的资源
            g.dispose();

            //输出新的图片
            FileOutputStream outputStream = new FileOutputStream(target);

            //创建新的图片
            ImageIO.write(buffImg, "jpg", outputStream);

            //刷新流
            outputStream.flush();
            //关闭流
            outputStream.close();
            return target;
        } catch (Exception e) {
            return source;
        }
    }

    private static int getFontSize (int total, String[] text) {
        int len = 0;
        int min = 12;
        int max = 256;
        for (String t: text) {
            if (len <= 0) {
                len = t.length();
            } else if (t.length() < len) {
                len = t.length();
            }
        }

        int s = (int)(total / len / 1.25);
        s = Math.max(min, s);
        s = Math.min(s, max);
        return s;
    }
}

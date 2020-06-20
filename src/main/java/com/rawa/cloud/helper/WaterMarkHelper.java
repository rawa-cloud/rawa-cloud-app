package com.rawa.cloud.helper;

import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class WaterMarkHelper {
    private static float spaceRatio = 0.1f;
    private static float logoRadio = 0.3f;

    public static File addWatermark(File source, File target, File logo, String watermarkContent) {
        try {
            Image srcImg = ImageIO.read(source);
            //获取图片的宽和高
            int srcImgwidth = srcImg.getWidth(null);
            int srcImgheight = srcImg.getHeight(null);

            //画水印需要一个画板    创建一个画板
            BufferedImage buffImg = new BufferedImage(srcImgwidth, srcImgheight, BufferedImage.TYPE_INT_RGB);
            //创建一个2D的图像
            Graphics2D g = buffImg.createGraphics();
            //画出来
            g.drawImage(srcImg, 0, 0, srcImgwidth, srcImgheight, null);

            //设置水印的颜色
            Color color = new Color(0, 0, 0, 255);
            g.setColor(color);

//            g.rotate(Math.toRadians(-45),
//                    (double) buffImg.getWidth() / 2, (double) buffImg
//                            .getHeight() / 2);
            int logoWidth = 0;
            if (logo != null) {
                int maxWidth = (int)(srcImgwidth * logoRadio);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.4f));
                Image logoImage = ImageIO.read(logo);
                int w = logoImage.getWidth(null);
                int h = logoImage.getHeight(null);
                logoWidth = Math.min(maxWidth, w);
                int logoHeight = h * logoWidth / w;
                int x = (int)(srcImgwidth * spaceRatio);
                int y = (srcImgheight - logoHeight) / 2;
                g.drawImage(logoImage, x, y, logoWidth, logoHeight, null);
            }

            //设置水印的字体

            //设置水印坐标
            if (!StringUtils.isEmpty(watermarkContent)) {
                String[] text = watermarkContent.split("\n");
                int x = logoWidth + (int)(srcImgwidth * spaceRatio) + 64;
                int textWidth = srcImgwidth - x - (int)(srcImgwidth * spaceRatio);
                int fontSize = getFontSize(textWidth, text);
                Font font = new Font("Serif",Font.PLAIN, fontSize);
                g.setFont(font);
                int lineHeight = (int)(fontSize * 1.5);
                int h = lineHeight * text.length;
                int y = (srcImgheight - h) / 2;
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
        int min = 16;
        int max = 64;
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

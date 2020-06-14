package com.rawa.cloud.helper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class WaterMarkHelper {
    public static File addWatermark(File source, File target, String watermarkContent) {
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

            g.rotate(Math.toRadians(-45),
                    (double) buffImg.getWidth() / 2, (double) buffImg
                            .getHeight() / 2);

            //设置水印的颜色
            Color color = new Color(160, 160, 160, 140);
            g.setColor(color);

            //设置水印的字体
            Font font = new Font("微软雅黑",Font.PLAIN,24);
            g.setFont(font);

            //设置水印坐标
            int maxY = Math.max(srcImgheight, srcImgwidth) * 3;
            int lineHeight = 128;
            int y = - maxY;
            while ( y < maxY) {
                y += lineHeight;
                g.drawString(getRepeatContent(watermarkContent, g, maxY), -maxY, y);
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

    private static int getWaterMarkLength(String watermarkContent,Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(watermarkContent.toCharArray(), 0, watermarkContent.length());
    }

    private static String getRepeatContent (String watermarkContent, Graphics2D g, int maxLen) {
        int n = maxLen * 3 / getWaterMarkLength(watermarkContent, g) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(watermarkContent).append("  ");
        }
        return sb.toString();
    }
}

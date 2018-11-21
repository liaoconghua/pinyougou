package com.pinyougou.common.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成工具
 */
public class VerifyCodeUtil {
    private static StringBuffer codesave = null;
    private static Random rand = new Random();

    /**
     * 获取验证码的图片
     *
     * @param width  默认 120
     * @param height 默认 30
     * @return BufferedImage
     */
    public static BufferedImage paintImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) img.getGraphics();

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(1, 1, width - 2, height - 2);

        String code = "23456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
        Random rd = new Random();
        codesave = new StringBuffer();
        int y = height - 4;
        int x = width / 6;
        graphics.setFont(new Font("楷体", Font.BOLD, y));

        //设置验证码内容
        for (int i = 0; i < 4; i++) {
            graphics.setColor(new Color(randNumber(0, 255), randNumber(0, 255), randNumber(0, 255)));
            double theta = Math.toRadians(randNumber(-50, 50));
            graphics.rotate(theta, x * (i + 1), y);
            //取出随机索引
            int index = rd.nextInt(code.length());
            String character = code.substring(index, index + 1);
            codesave.append(character);

            graphics.drawString(character, x * (i + 1), y);

            graphics.rotate(0 - theta, x * (i + 1), y);
        }
        //设置干扰线
        int lineNum = randNumber(3, 7);//干扰线的条数
        for (int i = 0; i < lineNum; i++) {
            graphics.setColor(new Color(randNumber(0, 255), randNumber(0, 255), randNumber(0, 255)));
            graphics.drawLine(randNumber(0, width), randNumber(0, height), randNumber(0, width), randNumber(0, height));
        }
        return img;
    }

    /**
     * 获取生成的验证码字符串
     *
     * @return String
     */
    public static String getCode() {
        if (codesave == null) {
            return "";
        }
        return codesave.toString();
    }

    /**
     * 产生一个区间里面的随机数
     *
     * @param left  区间左值
     * @param right 区间右值
     * @return 区间里面的一个随机数
     */
    private static int randNumber(int left, int right) {
        return rand.nextInt(right - left) + left;
    }
}

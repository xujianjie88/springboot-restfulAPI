package com.xjj.utils;

/**
 * Created by jie on 2018/9/11.
 */

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 图片验证码生成类
 *
 */
public class ImgVerifyCodeUtils extends HttpServlet {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *  验证码图片的宽度
     */
    private int width = 70;

    /**
     * 验证码图片的高度
     */
    private int height = 30;

    /**
     * 验证码字符个数
     */
    private int codeCount = 5;

    /**
     * 生成随机数的水平距离
     */
    private int xx = 0;

    /**
     * 生成随机数的数字高度
     */
    private int fontHeight;

    /**
     * 生成随机数的垂直距离
     */
    private int codeY;

    /**
     * 验证码序列
     */
    private String[] codeSequence = {"1" , "2" , "3" , "4" , "5" ,"6","7","8","9","A","a","B","b","c","C"
            ,"D","d","E","e","F","f","G","g","z","X","Q","v"};

    public ImgVerifyCodeUtils(int width, int height, int codeCount, int xx, int fontHeight, int codeY,
                              String[] codeSequence) {
        super();
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.xx = xx;
        this.fontHeight = fontHeight;
        this.codeY = codeY;
        this.codeSequence = codeSequence;
    }


    public ImgVerifyCodeUtils() {
        super();
    }


    public int getWidth() {
        return width;
    }


    public void setWidth(int width) {
        this.width = width;
    }


    public int getHeight() {
        return height;
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public int getCodeCount() {
        return codeCount;
    }


    public void setCodeCount(int codeCount) {
        this.codeCount = codeCount;
    }


    public int getXx() {
        return xx;
    }


    public void setXx(int xx) {
        this.xx = xx;
    }


    public int getFontHeight() {
        return fontHeight;
    }


    public void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
    }


    public int getCodeY() {
        return codeY;
    }


    public void setCodeY(int codeY) {
        this.codeY = codeY;
    }


    public String[] getCodeSequence() {
        return codeSequence;
    }


    public void setCodeSequence(String[] codeSequence) {
        this.codeSequence = codeSequence;
    }


    public static long getSerialversionuid() {
        return serialVersionUID;
    }


    /**
     * 初始化验证图片属性
     */
    public void init() throws ServletException {
//高度
        String strWidth = width + "";
//宽度
        String strHeight = height + "";
//字符个数
        String strCodeCount = codeCount + "";

//此处这么写，代表可以随时更改告诉，在程序运行时改变
        try{
            if (strWidth != null && strWidth.length() != 0) {
                width = Integer.parseInt(strWidth);
            }
            if (strHeight != null && strHeight.length() != 0) {
                height = Integer.parseInt(strHeight);
            }
            if (strCodeCount != null && strCodeCount.length() != 0) {
                codeCount = Integer.parseInt(strCodeCount);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
     //生成随机数的水平距离
        xx = width/(codeCount + 2);
     //生成随机数的数字高度
        fontHeight = height - 12;
    //生成随机数的垂直距离
        codeY = height - 8;

    }

    public void createImageCode(HttpServletRequest req , HttpServletResponse resp) throws Exception{
        init();
//自定义图像buffer
        BufferedImage buffImg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D gd = buffImg.createGraphics();

        Random random = new Random();
//将图片填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);
//创建字体，字体的大小根据图片的高度来定
        Font font = new Font("Fixedsys",Font.PLAIN,fontHeight);
//设置字体
        gd.setFont(font);
//画边框
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);
//随机产生4条干扰线，使图像中的认证码不易被其他程序探测到。
        gd.setColor(Color.BLACK);
        for(int i = 0 ; i < 4; i ++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }
//randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0;
        int green = 0;
        int blue = 0;

//随机产生codeCount数字的验证码。
        for(int i = 0 ; i < codeCount ; i ++){
//得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(27)]);
//产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(125);
            green = random.nextInt(255);
            blue = random.nextInt(200);

//用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red,green,blue));
            gd.drawString(strRand, (i + 1) * xx, codeY);

//将产生的四个随机数组合在一起
            randomCode.append(strRand);
        }
//将四位数字的验证码保存到Session中。
        HttpSession session = req.getSession();
        session.setAttribute("validataCode", randomCode.toString());

//禁止图像缓存
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        resp.setContentType("image/jpeg");

//将图像输出到Servlet输出流中
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }
}
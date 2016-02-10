package com.moblong.amuse;

import javax.servlet.http.HttpServlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;  
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;  
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
  
@SuppressWarnings("serial")
@WebServlet(displayName="CAPTCHA", name ="CAPTCHA", urlPatterns = "/CAPTCHA.activity")
public class CAPTCHAActivity extends HttpServlet {
	
	/**
	 * ��֤��ͼƬ�Ŀ�ȡ�
	 */
	private int width = 160;
	/**
	 * ��֤��ͼƬ�ĸ߶ȡ�
	 */
	private int height = 80;
	
	private int FONT_SIZE = 32;
	
	private Random random = new Random();
	
	private String captcha;
	
    public CAPTCHAActivity() {  
        super();  
    }
  
    private Color color(int fc, int bc) {
		if (fc > 255)
			fc = 200;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
    
	private void drawBackground(Graphics2D g, int nums) {
		g.setColor(color(200 , 250));
        g.fillRect(0, 0, width , height);
		g.setColor(this.color(160, 200));
		for (int i = 0; i < nums; i++) {
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(12);
			int y2 = random.nextInt(12);
			g.drawLine(x1, y1, x2, y2);
		}
	}
	
	private String drawCAPTCHA(final Graphics2D g, final int length) {
		StringBuffer sbuf = new StringBuffer();
		char temp;
		int itmp = 0;
		int offx = FONT_SIZE;
		for (int i = 0; i < length; i++) {
			switch (random.nextInt(5)) {
			case 1: // ����A��Z����ĸ
				itmp = random.nextInt(26) + 65;
				temp = (char)itmp;
				break;
			case 2:
				itmp = random.nextInt(26) + 97;
				temp = (char)itmp;
			default:
				itmp = random.nextInt(10) + 48;
				temp = (char)itmp;
				break;
			}
			Color color = new Color(20 + random.nextInt(20), 20 + random.nextInt(20), 20 + random.nextInt(20));
			g.setColor(color);
			// ��������תһ���ĽǶ�
			AffineTransform trans = new AffineTransform();
			trans.rotate(random.nextInt(45) * 3.14 / 180, 15 * i + 8, 7);
			// ��������
			float scaleSize = random.nextFloat() + 0.8f;
			if (scaleSize > 1f)
				scaleSize = 1f;
			trans.scale(scaleSize, scaleSize);
			g.setTransform(trans);
			g.setFont(new Font("����", Font.BOLD, FONT_SIZE));
			g.setColor(Color.BLACK);
			g.drawString(String.valueOf(temp), offx, 32);
			offx += FONT_SIZE;
			sbuf.append(temp);
		}
		g.dispose();
		return sbuf.toString();
	}
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "No-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/jpeg");
        BufferedImage image =new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = image.createGraphics();
        drawBackground(g, 160);
        captcha = drawCAPTCHA(g, 4);
        HttpSession session = req.getSession();
        session.setAttribute("captcha", captcha);
        g.dispose();
        ImageIO.write(image, "JPEG", resp.getOutputStream());
    }
}  

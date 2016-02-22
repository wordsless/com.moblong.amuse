package com.moblong.amuse;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

@SuppressWarnings("serial")
@WebServlet(displayName="SaveUploadedPicture", name ="SaveUploadedPicture", urlPatterns = "/SavePicture")
public class SavePictureActivity extends BasicServlet{
	
	/*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    private void zoomImage(BufferedImage bufImg, File dest, double rate) throws Exception {
        int w = (int) (bufImg.getWidth() * rate);
        int h = (int) (bufImg.getHeight() * rate);
        Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);//设置缩放目标图片模板
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        Itemp = ato.filter(bufImg, null);
        try {
            ImageIO.write((BufferedImage) Itemp, "JPEG", dest); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	@Override
	public void init() throws ServletException {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		File cache = context.getBean("cache", File.class);
		//生成图片名称
		final String pictureName = UUID.randomUUID().toString().replace("-", "");
		
		InputStream    input = null;
		PrintWriter    print = null;
		ImageInputStream iis = null;
		try {
			//保存原图
			input = request.getInputStream();
			iis   = ImageIO.createImageInputStream(input);
			File picture = new File(cache, pictureName);
			if(!picture.exists()) {
				picture.createNewFile();
				picture.setReadable(true);
				picture.setWritable(true);
			}
			final BufferedImage image = ImageIO.read(iis);
			OutputStream output = new FileOutputStream(picture);
			ImageIO.write(image, "JPEG", output);
			output.close();
			output = null;
			input.close();
			input = null;
			
			//保存缩小图
			File dest = new File(cache, pictureName+"_small");
			if(!dest.exists()) {
				dest.createNewFile();
				dest.setReadable(true);
				dest.setWritable(true);
			}
			zoomImage(image, dest, 0.5d);
			
			print = response.getWriter();
			print.write(pictureName);
			print.flush();
			print.close();
			print = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				input = null;
			}
			
			if(print != null) {
				print.close();
				print = null;
			}
		}
	}

}

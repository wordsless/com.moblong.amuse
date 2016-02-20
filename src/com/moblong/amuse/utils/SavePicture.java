package com.moblong.amuse.utils;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public final class SavePicture {

	private File cache;
	
	public SavePicture(final String absPath) {
		cache = new File(absPath);
	}
	
	public void save(BufferedImage image, File dest, double rate) throws Exception {
        int w = (int) (image.getWidth() * rate);
        int h = (int) (image.getHeight() * rate);
        Image Itemp = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);//设置缩放目标图片模板
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        Itemp = ato.filter(image, null);
        try {
            ImageIO.write((BufferedImage) Itemp, "JPEG", dest); //写入缩减后的图片
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	public void save(final String pictureName, final InputStream input) throws IOException {
		File dest = new File(cache, pictureName);
		ImageInputStream iis = null;
		//保存原图
		iis = ImageIO.createImageInputStream(input);
		if(!dest.exists()) {
			dest.createNewFile();
			dest.setReadable(true);
			dest.setWritable(true);
		}
		final BufferedImage image = ImageIO.read(iis);
		OutputStream output = new FileOutputStream(dest);
		ImageIO.write(image, "JPEG", output);
		output.close();
		output = null;
	}
}

package com.moblong.amuse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

@SuppressWarnings("serial")
@WebServlet(displayName="SaveUploadedPicture", name ="SaveUploadedPicture", urlPatterns = "/SavePicture")
public class SavePictureActivity extends BaseHttpServlet{
	
	@Override
	public void init() throws ServletException {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		String CACHE = context.getBean("cache", String.class);
		String pictureName = UUID.randomUUID().toString().replace("-", "");
		InputStream input = null;
		PrintWriter print = null;
		try {
			input = request.getInputStream();
			byte[] data = read(input);
			File picture = new File(CACHE, pictureName);
			if(!picture.exists()) {
				picture.createNewFile();
				picture.setReadable(true);
				picture.setWritable(true);
			}
			OutputStream output = new FileOutputStream(picture);
			output.write(data);
			output.flush();
			output.close();
			
			input.close();
			input = null;
			
			print = response.getWriter();
			print.write(pictureName);
			print.flush();
			print.close();
			print = null;
		} catch (IOException e) {
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

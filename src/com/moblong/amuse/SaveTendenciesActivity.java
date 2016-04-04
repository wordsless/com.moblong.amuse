package com.moblong.amuse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class SaveTendenciesActivity extends HttpServlet {

	private static final long serialVersionUID = -4769267591347798005L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

		String aid = req.getParameter("aid");
		String data = req.getParameter("tendencies");
		File dir = context.getBean("tendencies", File.class);
		File tendency = new File(dir, aid);
		OutputStream output = new FileOutputStream(tendency);
		output.write(data.getBytes("UTF-8"));
		output.flush();
		output.close();
		output = null;
	}

}

package com.moblong.amuse;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.flipped.model.Account;

public final class SignInActivity extends HttpServlet {
	
	private final Gson gson = new GsonBuilder()
			  				  .setDateFormat("yyyy-MM-dd HH:mm:ss")
			  				  .create();
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String phone = req.getParameter("phone");
		String pwd   = req.getParameter("pwd");
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
		Account account = accountDTO.signIn(context, phone, pwd);
		
		PrintWriter writer = resp.getWriter();
		if(account != null) {
			writer.print(gson.toJson(account).getBytes("utf8"));
			writer.flush();
		} else {
			writer.print("NONE".getBytes("utf8"));
			writer.flush();
		}
		writer.close();
		writer = null;
	}

}

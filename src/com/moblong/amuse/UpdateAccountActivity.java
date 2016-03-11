package com.moblong.amuse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.flipped.model.Account;

@SuppressWarnings("serial")
@WebServlet(displayName="UpdateAccount", name ="UpdateAccount", urlPatterns = "/UpdateAccount")
public final class UpdateAccountActivity extends HttpServlet {
	
	private final Gson gson = new GsonBuilder()
			  				  .setDateFormat("yyyy-MM-dd HH:mm:ss")
			  				  .create();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		OutputStream writer = null;
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		try {
			writer = resp.getOutputStream();
			Account       account = gson.fromJson(req.getParameter("account"), Account.class);
			AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
			accountDTO.update(context, account, req.getParameter("password"));
			
			writer.write(gson.toJson(account).getBytes("UTF-8"));
			writer.flush();
			writer.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.flush();
					writer.close();
					writer = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

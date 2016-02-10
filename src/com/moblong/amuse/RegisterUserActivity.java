package com.moblong.amuse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.amuse.dto.UserDTO;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.User;

@SuppressWarnings("serial")
@WebServlet(displayName="register user", name ="SubmitUserDetails", urlPatterns = "/SubmitUserDetails")
public final class RegisterUserActivity  extends BaseHttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		final Gson gson = new GsonBuilder()  
							  .setDateFormat("yyyy-MM-dd HH:mm:ss")
							  .create();
		PrintWriter writer = null;
		try {
			String aid  = req.getParameter("aid");
			String data = req.getParameter("data");
			User			 user = gson.fromJson(data, User.class);
			AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
			UserDTO		  userDTO = context.getBean("UserDTO", UserDTO.class);
			String			uid = accountDTO.lookforUserId(context, aid);
			user.setUid(uid);
			userDTO.save(context, user);
			writer = resp.getWriter();
			writer.write("OK");
			writer.close();
			writer = null;
		} catch (JsonSyntaxException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if(writer != null) {
				writer.close();
				writer = null;
			}
		}
	}
}

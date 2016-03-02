package com.moblong.amuse;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.flipped.model.Account;

@WebServlet(displayName="RequestAccount", name ="RequestAccount", urlPatterns = "/RequestAccount")
public class RequestAccount extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		Gson gson = new GsonBuilder()
				  .setDateFormat("yyyy-MM-dd HH:mm:ss")
				  .create();
		String aid = req.getParameter("aid");
		AccountDTO dto = context.getBean("AccountDTO", AccountDTO.class);
		Account account = dto.reload(context, aid);
		resp.setCharacterEncoding("UTF-8");
		OutputStream output = resp.getOutputStream();
		output.write(gson.toJson(account).getBytes("utf8"));
		output.flush();
		output = null;
	}

}

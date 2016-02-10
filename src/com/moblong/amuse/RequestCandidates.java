package com.moblong.amuse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.activation.DataSource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.flipped.model.Account;

@WebServlet(displayName="RequestCandidates", name ="RequestCandidates", urlPatterns = "/RequestCandidates")
public final class RequestCandidates extends BaseHttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		final Gson gson = new GsonBuilder()
				  .setDateFormat("yyyy-MM-dd HH:mm:ss")
				  .create();
		String aid = req.getParameter("aid");
		PrintWriter writer = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json; charset=UTF-8");
			writer = resp.getWriter();
			if(aid != null) {
				AccountDTO dto = context.getBean("AccountDTO", AccountDTO.class);
				List<Account> accounts = dto.candidate(context, aid, 0);
				writer.write(gson.toJson(accounts));
				writer.close();
				writer = null;
			} else {
				writer.write("NONE");
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}

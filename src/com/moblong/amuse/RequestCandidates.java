package com.moblong.amuse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.flipped.model.Account;

@WebServlet(displayName="RequestCandidates", name ="RequestCandidates", urlPatterns = "/RequestCandidates")
public final class RequestCandidates extends BasicServlet {

	private static final long serialVersionUID = 2430251822461486137L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		final Gson gson = new GsonBuilder()
				  .setDateFormat("yyyy-MM-dd HH:mm:ss")
				  .create();
		String aid  = req.getParameter("aid");
		double latitude = Double.parseDouble(req.getParameter("latitude")), longitude = Double.parseDouble(req.getParameter("longitude"));
		OutputStream writer = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json; charset=UTF-8");
			writer = resp.getOutputStream();
			List<Account> candidates = new ArrayList<Account>(0);
			if(aid != null) {
				SmartAssister assister = context.getBean("smartAssister", SmartAssister.class);
				candidates = assister.nearby(context, aid, latitude, longitude, 1000);
			}
			writer.write(gson.toJson(candidates).getBytes("UTF-8"));
			writer.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

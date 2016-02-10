package com.moblong.amuse;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.UserDTO;
import com.moblong.flipped.model.User;

@SuppressWarnings("serial")
@WebServlet(displayName="RequestIndicator", name ="RequestIndicator", urlPatterns = "/RequestIndicator")
public final class RequestIndicator extends BaseHttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		final Gson gson = new GsonBuilder()
						  .setDateFormat("yyyy-MM-dd HH:mm:ss")
						  .create();
		final String aid = req.getParameter("aid");
		final UserDTO userDTO = context.getBean("UserDTO", UserDTO.class);
		final String uid = userDTO.lookfor(context, aid);
		final User user = userDTO.reload(context, uid);
		
		PrintWriter writer = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json; charset=UTF-8");
			writer = resp.getWriter();
			writer.write(gson.toJson(user.getIndicators()));
			writer.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				writer.close();
				writer = null;	
			}
		}
	}

}

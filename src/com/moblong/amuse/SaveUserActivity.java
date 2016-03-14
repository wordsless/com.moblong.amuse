package com.moblong.amuse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.amuse.dto.UserDTO;
import com.moblong.flipped.model.DetailsItem;

@SuppressWarnings("serial")
@WebServlet(displayName="register user", name ="SubmitUserDetails", urlPatterns = "/SubmitUserDetails")
public final class SaveUserActivity  extends BasicServlet {
	
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
			List<DetailsItem<?>> user = gson.fromJson(data, new TypeToken<List<DetailsItem<?>>>(){}.getType());
			AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
			UserDTO		  userDTO = context.getBean("UserDTO", UserDTO.class);
			String			uid = accountDTO.lookforUserId(context, aid);
			userDTO.save(context, uid, user);
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

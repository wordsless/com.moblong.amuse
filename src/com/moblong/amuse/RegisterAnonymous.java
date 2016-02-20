package com.moblong.amuse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.AccountDTO;
import com.moblong.amuse.dto.DeviceDTO;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Device;

@SuppressWarnings("serial")
@WebServlet(displayName="register", name ="register", urlPatterns = "/RegisterAnonymous")
public final class RegisterAnonymous extends BasicServlet {

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		final Gson    gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		final String  aid  = UUID.randomUUID().toString().replace("-", "");
		
		Account account = gson.fromJson(req.getParameter("account"), Account.class);
		account.setId(aid);
		account.setRegistered(new Date(System.currentTimeMillis()));
		account.setLast(new Date(System.currentTimeMillis()));
		account.setSignature("TA什么都没有留下！");
		
		AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
		accountDTO.save(context, req.getParameter("password"), UUID.randomUUID().toString().replace("-", ""), account);
		
		Device  device  = gson.fromJson(req.getParameter("device"), Device.class);
		DeviceDTO deviceDTO = context.getBean("DeviceDTO", DeviceDTO.class);
		deviceDTO.save(context, aid, device);
		
		PrintWriter writer = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			writer = resp.getWriter();
			writer.write(gson.toJson(account));
			writer.flush();
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

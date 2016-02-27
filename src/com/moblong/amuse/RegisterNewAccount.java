package com.moblong.amuse;

import java.io.IOException;
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
import com.moblong.amuse.dto.DeviceDTO;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Device;

@SuppressWarnings("serial")
@WebServlet(displayName="Register New Account", name ="RegisterNewAccount", urlPatterns = "/RegisterNewAccount")
public final class RegisterNewAccount extends HttpServlet {
	
	private final Gson gson = new GsonBuilder()
			  				  .setDateFormat("yyyy-MM-dd HH:mm:ss")
			  				  .create();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		PrintWriter writer = null;
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		try {
			writer = resp.getWriter();
			Account       account = gson.fromJson(req.getParameter("account"), Account.class);
			AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
			accountDTO.update(context, account, req.getParameter("password"));
			DeviceDTO deviceDTO = context.getBean("DeviceDTO", DeviceDTO.class);
			Device device = deviceDTO.reload(context, account.getId());
			deviceDTO.setPhone(context, device.getDeviceID(), account.getTelephone());
			
			writer.print("OK");
			writer.flush();
			writer.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				writer.flush();
				writer.close();
				writer = null;
			}
		}
	}

}

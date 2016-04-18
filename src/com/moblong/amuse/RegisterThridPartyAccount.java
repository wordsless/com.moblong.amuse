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
import com.moblong.amuse.dto.ContactDTO;
import com.moblong.amuse.dto.DeviceDTO;
import com.moblong.flipped.model.Contact;
import com.moblong.flipped.model.Device;

public final class RegisterThridPartyAccount extends HttpServlet {
	
	private final Gson gson = new GsonBuilder()
			  .setDateFormat("yyyy-MM-dd HH:mm:ss")
			  .create();
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		PrintWriter writer = resp.getWriter();
		
		Contact       account = gson.fromJson(req.getParameter("account"), Contact.class);
		ContactDTO accountDTO = context.getBean("AccountDTO", ContactDTO.class);
		accountDTO.update(context, account, null);
		
		DeviceDTO deviceDTO = context.getBean("DeviceDTO", DeviceDTO.class);
		Device device = deviceDTO.reload(context, account.getId());
		deviceDTO.setPhone(context, device.getDeviceID(), account.getTelephone());
		
		writer.print("OK");
		writer.flush();
		writer.close();
		writer = null;
	}

}

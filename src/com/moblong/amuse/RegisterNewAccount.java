package com.moblong.amuse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.moblong.amuse.dto.AccountDTO;
import com.moblong.amuse.dto.DeviceDTO;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.Device;

@SuppressWarnings("serial")
@WebServlet(displayName="Register New Account", name ="RegisterNewAccount", urlPatterns = "/RegisterNewAccount")
public final class RegisterNewAccount extends BasicServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		byte[] data;
		PrintWriter writer = null;
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		try {
			data   = read(req.getInputStream());
			writer = resp.getWriter();
			Account       account = gson.fromJson(new String(data, "UTF-8"), Account.class);
			AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
			accountDTO.update(context, account);
			
			DeviceDTO deviceDTO = context.getBean("DeviceDTO", DeviceDTO.class);
			Device device = deviceDTO.reload(context, account.getId());
			deviceDTO.setPhone(context, device.getDeviceID(), account.getTelephone());
			
			writer.write("OK");
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

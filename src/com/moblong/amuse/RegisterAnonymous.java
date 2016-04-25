package com.moblong.amuse;

import java.io.IOException;
import java.io.OutputStream;
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
import com.moblong.amuse.dto.ContactDTO;
import com.moblong.amuse.dto.DetailsDTO;
import com.moblong.amuse.dto.DeviceDTO;
import com.moblong.flipped.model.Contact;
import com.moblong.flipped.model.Device;

@SuppressWarnings("serial")
@WebServlet(displayName="RegisterAnonymous", name ="RegisterAnonymous", urlPatterns = "/RegisterAnonymous")
public final class RegisterAnonymous extends BasicServlet {

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		final Gson   gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		//final String aid  = UUID.randomUUID().toString().replace("-", "");
		
		Contact anonymous = gson.fromJson(req.getParameter("account"), Contact.class);
		anonymous.setRegistered(new Date(System.currentTimeMillis()));
		anonymous.setLatest(new Date(System.currentTimeMillis()));
		anonymous.setSignature("TA什么都没有留下！");
		anonymous.setUid(UUID.randomUUID().toString().replace("-", ""));
		
		ContactDTO contactDTO = context.getBean("ContactDTO", ContactDTO.class);
		contactDTO.save(context, anonymous.getUid(), req.getParameter("password"), anonymous);
		
		DetailsDTO detailsDTO = context.getBean("DetailsDTO", DetailsDTO.class);
		detailsDTO.init(context, anonymous.getUid());
		
		Device  device = gson.fromJson(req.getParameter("device"), Device.class);
		DeviceDTO deviceDTO = context.getBean("DeviceDTO", DeviceDTO.class);
		deviceDTO.save(context, anonymous.getId(), device);
		
		OutputStream writer = null;
		try {
			writer = resp.getOutputStream();
			writer.write(gson.toJson(anonymous).getBytes("UTF-8"));
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

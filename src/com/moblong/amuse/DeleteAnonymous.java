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
@WebServlet(displayName="DeleteAnonymous", name ="DeleteAnonymous", urlPatterns = "/DeleteAnonymous")
public final class DeleteAnonymous extends BasicServlet {

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		final String aid  = UUID.randomUUID().toString().replace("-", "");
		
		AccountDTO accountDTO = context.getBean("AccountDTO", AccountDTO.class);
		accountDTO.delete(context, aid);
		
		PrintWriter writer = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			writer = resp.getWriter();
			writer.write(Boolean.toString(Boolean.TRUE));
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

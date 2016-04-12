package com.moblong.amuse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moblong.amuse.dto.DetailsDTO;
import com.moblong.flipped.model.Contact;
import com.moblong.flipped.model.VerifiableItem;

@SuppressWarnings("serial")
@WebServlet(displayName="ReloadUser", name ="ReloadUser", urlPatterns = "/ReloadUser")
public final class RequestDetails extends BasicServlet {

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		final Gson   gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		
		Contact account = gson.fromJson(req.getParameter("account"), Contact.class);
		
		DetailsDTO detailsDTO = context.getBean("DetailsDTO", DetailsDTO.class);
		List<VerifiableItem> details = detailsDTO.reload(context, account.getUid());
		
		OutputStream output = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			output = resp.getOutputStream();
			output.write(gson.toJson(details).getBytes("UTF-8"));
			output.flush();
			output.close();
			output = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(output != null) {
				output.close();
				output = null;
			}
		}
	}
}

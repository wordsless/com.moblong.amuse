package com.moblong.amuse;

import java.io.File;
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
import com.google.gson.reflect.TypeToken;
import com.moblong.amuse.dto.UserDTO;
import com.moblong.amuse.utils.SecurityReaderAndWirter;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.DetailsItem;
import com.moblong.flipped.model.IOperator;

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
		File dir = context.getBean("tendencies", File.class);
		SecurityReaderAndWirter reader = new SecurityReaderAndWirter();
		String data = reader.read(dir, aid);
		List<DetailsItem<?>> tendencies = gson.fromJson(data, new TypeToken<List<DetailsItem<?>>>(){}.getType());
		UserDTO userDTO = context.getBean("UserDTO", UserDTO.class);
		double latitude = Double.parseDouble(req.getParameter("latitude")), longitude = Double.parseDouble(req.getParameter("longitude"));
		OutputStream writer = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json; charset=UTF-8");
			writer = resp.getOutputStream();
			SmartAssister assister = context.getBean("smartAssister", SmartAssister.class);
			List<Account> temp = assister.nearby(context, aid, latitude, longitude, 1000);
			List<Account> candidates = new ArrayList<Account>(temp.size());
			for(Account account : temp) {
				List<DetailsItem<?>> user = userDTO.reload(context, account.getUid());
				int c = 0;
				for(DetailsItem<?> detail : user) {
					if(detail.getContent() instanceof Integer[]) {
						@SuppressWarnings("unchecked")
						DetailsItem<Integer[]> item = (DetailsItem<Integer[]>) tendencies.get(c);
						Integer[] scope = item.getContent();
						IOperator<Integer> operator = (IOperator<Integer>) item.getOperator();
						if(!operator.operate(scope, (Integer) detail.getContent()))
							break;
					} else if(detail.getContent() instanceof Boolean) {
						@SuppressWarnings("unchecked")
						DetailsItem<Boolean> item = (DetailsItem<Boolean>) tendencies.get(c);
						Boolean content = (Boolean) detail.getContent();
						IOperator<Boolean> operator = (IOperator<Boolean>) item.getOperator();
						if(!operator.operate(new Boolean[]{content}, (Boolean) detail.getContent()))
							break;
					} else if(detail.getContent() instanceof String) {
						@SuppressWarnings("unchecked")
						DetailsItem<String[]> item = (DetailsItem<String[]>) tendencies.get(c);
						String[] scope = item.getContent();
						IOperator<String> operator = (IOperator<String>) item.getOperator();
						if(!operator.operate(scope, (String) detail.getContent()))
							break;
					}
					++c;	
				}
				
				if(c == user.size()) {
					candidates.add(account);
				}
			}
			
			writer.write(gson.toJson(candidates).getBytes("UTF-8"));
			writer.close();
			writer = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer = null;
			}
		}
	}
}

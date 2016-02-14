package com.moblong.amuse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.moblong.flipped.model.Message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@SuppressWarnings("serial")
@WebServlet(displayName="RequestInvitation", name ="RequestInvitation", urlPatterns = "/RequestInvitation")
public final class RequestInvitation extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String msg = req.getParameter("msg");
		Gson gson = new GsonBuilder()
				    .setDateFormat("yyyy-MM-dd HH:mm:ss")
				    .create();
		Message message = null;
		if(msg != null)
			message = gson.fromJson(msg, Message.class);
		
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		Connection con;
	    Channel channel;
	    RabbitConnectionPool pool = context.getBean("RabbitConnectionPool", RabbitConnectionPool.class);
		con = pool.getConnection();
		channel = con.createChannel();
	    channel.queueDeclare(message.getTarget(), false, false, false, null);
	    channel.basicPublish("", message.getTarget(), null, msg.getBytes("UTF-8"));
	    try {
			channel.close();
			channel = null;
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

}

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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@SuppressWarnings("serial")
@WebServlet(displayName="RequestSayHello", name ="RequestSayHello", urlPatterns = "/RequestSayHello")
public final class RequestSayHello extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String aid = req.getParameter("aid");
		String msg = req.getParameter("msg");
		
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		Connection con;
	    Channel channel;
	    RabbitConnectionPool pool = context.getBean("RabbitConnectionPool", RabbitConnectionPool.class);
		con = pool.getConnection();
		channel = con.createChannel();
	    channel.queueDeclare(aid, false, false, false, null);
	    channel.basicPublish("", aid, null, msg.getBytes("UTF-8"));
	    /*try {
			channel.close();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	    channel = null;*/
	}

}

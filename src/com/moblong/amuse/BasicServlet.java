package com.moblong.amuse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BasicServlet extends HttpServlet {
	
	protected final Gson gson = new GsonBuilder()
			  				    .setDateFormat("yyyy-MM-dd HH:mm:ss")
			  				    .create();
	
	public byte[] read(InputStream inStream) throws IOException {
        ByteArrayOutputStream swap = new ByteArrayOutputStream();  
        byte[] buff = new byte[1024];  
        int c = 0;  
        while ((c = inStream.read(buff, 0, 100)) > 0) {  
            swap.write(buff, 0, c);  
        }  
        byte[] in2b = swap.toByteArray();
        swap.close();
        swap = null;
        return in2b;  
    }
}

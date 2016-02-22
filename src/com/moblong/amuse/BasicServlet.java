package com.moblong.amuse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BasicServlet extends HttpServlet {

	private static final long serialVersionUID = 4109972672240022385L;
	
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

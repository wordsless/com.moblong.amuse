package com.moblong.amuse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.moblong.amuse.dto.MaterialsDTO;

@SuppressWarnings("serial")
@WebServlet(displayName="SaveMaterials", name ="SaveMaterials", urlPatterns = "/SaveMaterials")
public final class SaveMaterialsActivity extends BasicServlet {

	private Map<String, String> parse(final String text) {
		Map<String, String> params = new HashMap<String, String>();
		char[] cbuf = text.toCharArray();
		StringBuilder sbuf = new StringBuilder();
		String key = null, value = null;
		for(char c : cbuf) {
			switch(c) {
			case '=':
				key = sbuf.toString();
				sbuf.delete(0, key.length() - 1);
				break;
			case '&':
				value = sbuf.toString();
				sbuf.delete(0, value.length() - 1);
				params.put(key, value);
				break;
			default:
				sbuf.append(c);
			}
		}
		return params;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		//DiskFileItemFactory factory = context.getBean("fileItemFactory", DiskFileItemFactory.class);
		File cache = context.getBean("cache", File.class);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		MaterialsDTO materialsDTO = context.getBean("MaterialsDTO", MaterialsDTO.class);
		if(ServletFileUpload.isMultipartContent(req)) {
			ServletFileUpload uploader = new ServletFileUpload(factory);
			try {
				List<FileItem> items = uploader.parseRequest(req);
				for(int index = 0; index < items.size(); index++) {
					String uid = UUID.randomUUID().toString().replace("-", "");
					FileItem picture = items.get(index);
					FileItem 	 aid = items.get(++index);
					FileItem	type = items.get(++index);
					FileItem	desc = items.get(++index);
					
					File bin = new File(cache, uid);
					if(bin.exists())
						bin.delete();
					bin.createNewFile();
					picture.write(bin);
					materialsDTO.save(context, uid, aid.getString("utf-8"), type.getString("utf-8"), desc.getString("utf-8"));
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		PrintWriter writer = resp.getWriter();
		writer.write("OK");
		writer.close();
		writer = null;
	}

}

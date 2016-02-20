package com.moblong.amuse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.moblong.amuse.dto.MaterialDTO;
import com.moblong.amuse.utils.SavePicture;
import com.moblong.amuse.utils.SecurityReaderAndWirter;
import com.moblong.flipped.model.Multiparty;

@SuppressWarnings("serial")
@WebServlet(displayName="SaveMaterials", name ="SaveMaterials", urlPatterns = "/SaveMaterials")
public final class SaveMaterialsActivity extends BasicServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Gson gson = new GsonBuilder()
				    .setDateFormat("yyyy-MM-dd HH:mm:ss")
				    .create();
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		MaterialDTO materialDTO = context.getBean("MaterialDTO", MaterialDTO.class);
		SecurityReaderAndWirter reader = new SecurityReaderAndWirter();
		SavePicture saver = new SavePicture("/webdav/static/image");
		String aid = req.getParameter("aid");
		List<Multiparty> multiparties = gson.fromJson(reader.read(req.getInputStream()), new TypeToken<List<Multiparty>>(){}.getType());
		for(Multiparty multiparty : multiparties) {
			String pid = UUID.randomUUID().toString().replace("-", "");
			byte[] data = Base64.decodeBase64(multiparty.getValue());
			InputStream input = new ByteArrayInputStream(data);
			saver.save(pid, input);
			input.close();
			input = null;
			String typed = multiparty.getParam("typed");
			materialDTO.save(context, aid, typed, pid);
		}
	}

}

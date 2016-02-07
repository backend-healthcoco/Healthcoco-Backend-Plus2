package com.dpdocter.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.webservices.PathProxy;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import common.util.web.Response;

@Component
@Path(PathProxy.BASE_URL)
public class FileUploadDemo {

    @Value(value = "${IMAGE_RESOURCE}")
    private String IMAGE_RESOURCE;

    @Path("fileuploaddemo")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response<String> uploadFile(@FormDataParam("file") List<FormDataBodyPart> files, @FormDataParam("name") String name) {

	for (FormDataBodyPart file : files) {
	    FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
	    String uploadedFileLocation = IMAGE_RESOURCE + fileDetail.getFileName();
	    writeToFile(file.getEntityAs(InputStream.class), uploadedFileLocation);
	}

	Response<String> response = new Response<String>();
	response.setData("200");

	return response;

    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

	try {
	    OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
	    int read = 0;
	    byte[] bytes = new byte[1024];

	    out = new FileOutputStream(new File(uploadedFileLocation));
	    while ((read = uploadedInputStream.read(bytes)) != -1) {
		out.write(bytes, 0, read);
	    }
	    out.flush();
	    out.close();
	} catch (IOException e) {

	    e.printStackTrace();
	}

    }
}

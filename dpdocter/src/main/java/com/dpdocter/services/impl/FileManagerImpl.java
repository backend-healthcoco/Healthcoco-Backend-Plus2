package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.services.FileManager;

@Service
public class FileManagerImpl implements FileManager {

    @Value(value = "${IMAGE_RESOURCE}")
    private String imageResource;

    // @Value(value = "${IMAGE_URL}")
    // private String imageUrl;

    @Context
    private UriInfo uriInfo;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    private void createDirIfNotExist(String dirPath) {
	File dir = new File(dirPath);
	if (!dir.exists()) {
	    dir.mkdirs();
	}
    }

    @Override
    public String saveImageAndReturnImageUrl(FileDetails fileDetails, String path) throws Exception {
	String fileName = fileDetails.getFileName() + "." + fileDetails.getFileExtension();
	createDirIfNotExist(imageResource + File.separator + path);
	String filePath = imageResource + File.separator + path + File.separator + fileName;
	IOUtils.write(Base64.decodeBase64(fileDetails.getFileEncoded()), new FileOutputStream(filePath));
	/*String imageUrl = this.imageUrl + "/" + path + "/" + fileName;*/
	String imageUrl = path + "/" + fileName;
	return imageUrl;
    }

    @Override
    public String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path) {
	String thumbnailUrl = "";
	BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);
	try {
	    img.createGraphics()
		    .drawImage(
			    ImageIO.read(
				    new File(imageResource + File.separator + path + File.separator + fileDetails.getFileName() + "."
					    + fileDetails.getFileExtension())).getScaledInstance(120, 120, Image.SCALE_SMOOTH), 0, 0, null);

	    String fileName = fileDetails.getFileName() + "_thumb." + fileDetails.getFileExtension();
	    createDirIfNotExist(imageResource + File.separator + path);
	    String filePath = imageResource + File.separator + path + File.separator + fileName;
	    ImageIO.write(img, fileDetails.getFileExtension(), new File(filePath));

	    thumbnailUrl = path + "/" + fileName;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return thumbnailUrl;
    }
}

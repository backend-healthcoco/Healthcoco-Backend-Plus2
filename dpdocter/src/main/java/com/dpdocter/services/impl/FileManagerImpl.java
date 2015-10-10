package com.dpdocter.services.impl;

import java.io.File;
import java.io.FileOutputStream;

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

    @Value(value = "${IMAGE_URL}")
    private String imageUrl;

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
}

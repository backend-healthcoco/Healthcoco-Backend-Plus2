package com.dpdocter.services.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.services.FileManager;
@Service
public class FileManagerImpl implements FileManager {

	@Value(value = "${IMAGE_RESOURCE}")
	private String imageResource;

	@Value(value = "${IMAGE_URL}")
	private String imageUrl;

	@Override
	public String saveImageAndReturnImageUrl(String path, MultipartFile image)
			throws Exception {
		createDirIfNotExist(imageResource + File.separator + path);
		File distinationFile = new File(imageResource + File.separator + path
				+ File.separator + image.getOriginalFilename());
		image.transferTo(distinationFile);
		String imageUrl = this.imageUrl + "/" + path + "/" + image.getOriginalFilename();
		return imageUrl;
	}

	private void createDirIfNotExist(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

}

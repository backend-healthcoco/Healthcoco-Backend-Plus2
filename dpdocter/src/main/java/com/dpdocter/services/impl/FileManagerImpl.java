package com.dpdocter.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.services.FileManager;
@Service
public class FileManagerImpl implements FileManager {

	@Value(value = "${IMAGE_RESOURCE}")
	private String imageResource;

	@Value(value = "${IMAGE_URL}")
	private String imageUrl;

	@Override
	public String saveImageAndReturnImageUrl(String path, InputStream image,String fileName)
			throws Exception {
		createDirIfNotExist(imageResource + File.separator + path);
		String filePath = imageResource + File.separator + path + File.separator + fileName;
		/*File distinationFile = new File(imageResource + File.separator + path
				+ File.separator + fileName);
		image.transferTo(distinationFile);*/
		saveFile(image, filePath);
		String imageUrl = this.imageUrl + "/" + path + "/" + fileName;
		return imageUrl;
	}

	private void createDirIfNotExist(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	// save uploaded file to a defined location on the server
		private void saveFile(InputStream uploadedInputStream,
				String serverLocation)throws IOException {

				OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
				int read = 0;
				byte[] bytes = new byte[1024];

				outpuStream = new FileOutputStream(new File(serverLocation));
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					outpuStream.write(bytes, 0, read);
				}
				outpuStream.flush();
				outpuStream.close();
			

		}

}

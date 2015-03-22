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

	/*
	 * @Override public String saveImageAndReturnImageUrl(String path,
	 * MultipartFormDataInput multipartFormDataInput) throws Exception {
	 * Map<String, List<InputPart>> uploadForm =
	 * multipartFormDataInput.getFormDataMap();
	 * 
	 * //Get file name String fileName =
	 * uploadForm.get("fileName").get(0).getBodyAsString();
	 * createDirIfNotExist(imageResource + File.separator + path); String
	 * filePath = imageResource + File.separator + path + File.separator +
	 * fileName; File distinationFile = new File(imageResource + File.separator
	 * + path + File.separator + fileName); image.transferTo(distinationFile);
	 * List<InputPart> inputParts = uploadForm.get("file"); for (InputPart
	 * inputPart : inputParts) {
	 * 
	 * //Use this header for extra processing if required
	 * 
	 * @SuppressWarnings("unused") MultivaluedMap<String, String> header =
	 * inputPart.getHeaders();
	 * 
	 * // convert the uploaded file to inputstream InputStream inputStream =
	 * inputPart.getBody(InputStream.class, null);
	 * 
	 * byte[] bytes = IOUtils.toByteArray(inputStream); // constructs upload
	 * file path writeFile(bytes, filePath);
	 * 
	 * } //saveFile(image, filePath); String imageUrl = this.imageUrl + "/" +
	 * path + "/" + fileName; return imageUrl; }
	 */

	private void createDirIfNotExist(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/*
	 * private void saveFile(InputStream uploadedInputStream, String
	 * serverLocation)throws IOException {
	 * 
	 * OutputStream outpuStream = new FileOutputStream(new
	 * File(serverLocation)); int read = 0; byte[] bytes = new byte[1024];
	 * 
	 * outpuStream = new FileOutputStream(new File(serverLocation)); while
	 * ((read = uploadedInputStream.read(bytes)) != -1) {
	 * outpuStream.write(bytes, 0, read); } outpuStream.flush();
	 * outpuStream.close();
	 * 
	 * 
	 * }
	 */
	/*
	 * // save uploaded file to a defined location on the server private void
	 * writeFile(byte[] content, String filename) throws IOException { File file
	 * = new File(filename); if (!file.exists()) { file.createNewFile(); }
	 * FileOutputStream fop = new FileOutputStream(file); fop.write(content);
	 * fop.flush(); fop.close(); }
	 */

	@Override
	public String saveImageAndReturnImageUrl(FileDetails fileDetails,
			String path) throws Exception {
		String fileName = fileDetails.getFileName()
				+ fileDetails.getFileExtension();
		createDirIfNotExist(imageResource + File.separator + path);
		String filePath = imageResource + File.separator + path
				+ File.separator + fileName;
		// File distinationFile = new File(imageResource + File.separator + path
		// + File.separator + fileName);
		IOUtils.write(Base64.decodeBase64(fileDetails.getFileEncoded()), new FileOutputStream(filePath));
		String imageUrl = this.imageUrl + "/" + path + "/" + fileName;
		return imageUrl;
	}

}

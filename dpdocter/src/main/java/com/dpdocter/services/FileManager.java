package com.dpdocter.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.response.ImageURLResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface FileManager {
	ImageURLResponse saveImageAndReturnImageUrl(FileDetails fileDetails, String path, Boolean createThumbnail)
			throws Exception;

	String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path);

	Double saveRecord(FormDataBodyPart file, String recordPath, Double allowedSize, Boolean checkSize);

	ImageURLResponse saveImage(FormDataBodyPart file, String recordPath, Boolean createThumbnail);

	public String saveThumbnailUrl(FormDataBodyPart file, String path);

	public Double saveRecordBase64(FileDetails fileDetail, String recordPath);
	
	public List<String> convertPdfToImage(FileDetails fileDetails, String path, Boolean createThumbnail) throws Exception;

	public String saveThumbnailAndReturnThumbNailUrl(String fileName, String path);

	public ImageURLResponse saveImageAndReturnImageUrl(ByteArrayOutputStream outstream, String fileName, String path,
			Boolean createThumbnail) throws Exception;

}

package com.dpdocter.services;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.response.ImageURLResponse;

public interface FileManager {
	ImageURLResponse saveImageAndReturnImageUrl(FileDetails fileDetails, String path, Boolean createThumbnail)
			throws Exception;

	String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path);

	Double saveRecord(MultipartFile file, String recordPath, Double allowedSize, Boolean checkSize);

	ImageURLResponse saveImage(MultipartFile file, String recordPath, Boolean createThumbnail);

	public String saveThumbnailUrl(MultipartFile file, String path);

	public Double saveRecordBase64(FileDetails fileDetail, String recordPath);
	
	public List<String> convertPdfToImage(MultipartFile file, String path, Boolean createThumbnail) throws Exception;

	public String saveThumbnailAndReturnThumbNailUrl(String fileName, String path);

	public ImageURLResponse saveImageAndReturnImageUrl(ByteArrayOutputStream outstream, String fileName, String path,
			Boolean createThumbnail) throws Exception;

}

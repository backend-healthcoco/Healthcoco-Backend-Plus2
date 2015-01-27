package com.dpdocter.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileManager {
	String saveImageAndReturnImageUrl(String path,MultipartFile image)throws Exception;
	
}

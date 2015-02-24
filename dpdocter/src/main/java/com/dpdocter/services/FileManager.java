package com.dpdocter.services;

import java.io.InputStream;

public interface FileManager {
	String saveImageAndReturnImageUrl(String path, InputStream image,String fileName)throws Exception;
	
}

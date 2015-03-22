package com.dpdocter.services;

import com.dpdocter.beans.FileDetails;


public interface FileManager {
	String saveImageAndReturnImageUrl(FileDetails fileDetails,String path)throws Exception;
	
}

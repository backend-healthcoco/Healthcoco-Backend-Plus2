package com.dpdocter.services;

import com.dpdocter.beans.FileDetails;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface FileManager {
    String saveImageAndReturnImageUrl(FileDetails fileDetails, String path) throws Exception;

    String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path);

	void saveRecord(FormDataBodyPart file, String recordPath);

}

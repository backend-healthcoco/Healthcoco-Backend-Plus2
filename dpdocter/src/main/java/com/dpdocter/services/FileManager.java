package com.dpdocter.services;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.response.ImageURLResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface FileManager {
    ImageURLResponse saveImageAndReturnImageUrl(FileDetails fileDetails, String path, Boolean createThumbnail) throws Exception;

    String saveThumbnailAndReturnThumbNailUrl(FileDetails fileDetails, String path);

    Double saveRecord(FormDataBodyPart file, String recordPath, Double allowedSize, Boolean checkSize);

}

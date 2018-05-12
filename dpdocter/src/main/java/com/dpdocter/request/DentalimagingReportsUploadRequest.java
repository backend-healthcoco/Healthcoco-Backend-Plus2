package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class DentalimagingReportsUploadRequest {

	private FileDetails fileDetails;
	private DentalImagingReportsAddRequest request;

	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	public DentalImagingReportsAddRequest getRequest() {
		return request;
	}

	public void setRequest(DentalImagingReportsAddRequest request) {
		this.request = request;
	}

}

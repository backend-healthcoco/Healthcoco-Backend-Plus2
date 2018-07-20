package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class DoctorRecordUploadRequest {

	private FileDetails fileDetails;

	private DoctorLabReportsAddRequest labReportsAddRequest;

	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	public DoctorLabReportsAddRequest getLabReportsAddRequest() {
		return labReportsAddRequest;
	}

	public void setLabReportsAddRequest(DoctorLabReportsAddRequest labReportsAddRequest) {
		this.labReportsAddRequest = labReportsAddRequest;
	}

	@Override
	public String toString() {
		return "DoctorRecordUploadRequest [fileDetails=" + fileDetails + ", labReportsAddRequest="
				+ labReportsAddRequest + "]";
	}
}
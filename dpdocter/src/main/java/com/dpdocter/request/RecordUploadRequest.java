package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class RecordUploadRequest {
	private FileDetails fileDetails;
	private LabReportsAddRequest labReportsAddRequest;

	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	public LabReportsAddRequest getLabReportsAddRequest() {
		return labReportsAddRequest;
	}

	public void setLabReportsAddRequest(LabReportsAddRequest labReportsAddRequest) {
		this.labReportsAddRequest = labReportsAddRequest;
	}

	@Override
	public String toString() {
		return "RecordUploadRequest [fileDetails=" + fileDetails + ", labReportsAddRequest=" + labReportsAddRequest
				+ "]";
	}

}

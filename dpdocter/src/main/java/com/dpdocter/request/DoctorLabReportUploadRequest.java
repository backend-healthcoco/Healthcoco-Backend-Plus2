package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class DoctorLabReportUploadRequest {

	private FileDetails fileDetails;
	private String recordsType;
	private String patientId;

	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	public String getRecordsType() {
		return recordsType;
	}

	public void setRecordsType(String recordsType) {
		this.recordsType = recordsType;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

}

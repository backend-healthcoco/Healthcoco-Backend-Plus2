package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.ExportRequestData;

public class ExportContactsRequest {
	private String doctorId;

	private String emailAddress;

	private List<ExportRequestData> dataType;

	private String specialComments;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public List<ExportRequestData> getDataType() {
		return dataType;
	}

	public void setDataType(List<ExportRequestData> dataType) {
		this.dataType = dataType;
	}

	public String getSpecialComments() {
		return specialComments;
	}

	public void setSpecialComments(String specialComments) {
		this.specialComments = specialComments;
	}

	@Override
	public String toString() {
		return "ExportContactsRequest [doctorId=" + doctorId + ", emailAddress=" + emailAddress + ", dataType=" + dataType + ", specialComments="
				+ specialComments + "]";
	}

}

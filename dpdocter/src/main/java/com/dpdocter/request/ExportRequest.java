package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.ComponentType;

public class ExportRequest {
	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String emailAddress;

	private List<ComponentType> dataType;

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

	public List<ComponentType> getDataType() {
		return dataType;
	}

	public void setDataType(List<ComponentType> dataType) {
		this.dataType = dataType;
	}

	public String getSpecialComments() {
		return specialComments;
	}

	public void setSpecialComments(String specialComments) {
		this.specialComments = specialComments;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "ExportRequest [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", emailAddress=" + emailAddress + ", dataType=" + dataType + ", specialComments=" + specialComments
				+ "]";
	}
}

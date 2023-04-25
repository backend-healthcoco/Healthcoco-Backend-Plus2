package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DoctorRegistrationDetail;

public class DoctorRegistrationAddEditRequest {
	private String doctorId;

	private List<DoctorRegistrationDetail> registrationDetails;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<DoctorRegistrationDetail> getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(List<DoctorRegistrationDetail> registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	@Override
	public String toString() {
		return "DoctorRegistrationAddEditRequest [doctorId=" + doctorId + ", registrationDetails=" + registrationDetails
				+ "]";
	}

}

package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Education;

public class DoctorEducationAddEditRequest {
	private String doctorId;

	private List<Education> education;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}

	@Override
	public String toString() {
		return "DoctorEducationAddEditRequest [doctorId=" + doctorId + "]";
	}

}

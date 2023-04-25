package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DoctorExperienceDetail;

public class DoctorExperienceDetailAddEditRequest {
	private String doctorId;

	private List<DoctorExperienceDetail> experienceDetails;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<DoctorExperienceDetail> getExperienceDetails() {
		return experienceDetails;
	}

	public void setExperienceDetails(List<DoctorExperienceDetail> experienceDetails) {
		this.experienceDetails = experienceDetails;
	}

	@Override
	public String toString() {
		return "DoctorExperienceAddEditRequest [doctorId=" + doctorId + ", experienceDetails=" + experienceDetails
				+ "]";
	}

}

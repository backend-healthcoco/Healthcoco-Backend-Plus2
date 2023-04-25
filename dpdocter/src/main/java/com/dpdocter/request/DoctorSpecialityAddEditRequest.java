package com.dpdocter.request;

import java.util.List;

public class DoctorSpecialityAddEditRequest {
	private String doctorId;

	private List<String> speciality;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<String> getSpeciality() {
		return speciality;
	}

	public void setSpeciality(List<String> speciality) {
		this.speciality = speciality;
	}

	@Override
	public String toString() {
		return "DoctorSpecialityAddEditRequest [doctorId=" + doctorId + ", speciality=" + speciality + "]";
	}

}

package com.dpdocter.request;

import java.util.ArrayList;
import java.util.List;

public class DoctorProfessionalAddEditRequest {
	private String doctorId;

	private List<String> membership = new ArrayList<>();

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<String> getMembership() {
		return membership;
	}

	public void setMembership(List<String> membership) {
		this.membership = membership;
	}

	@Override
	public String toString() {
		return "DoctorProfessionalAddEditRequest [doctorId=" + doctorId + ", membership=" + membership + "]";
	}

}

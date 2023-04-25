package com.dpdocter.request;

import java.util.List;

public class DoctorServicesAddEditRequest {

	private String doctorId;

	private List<String> services;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "DoctorSpecialityAddEditRequest [doctorId=" + doctorId + ", services=" + services + "]";
	}
}
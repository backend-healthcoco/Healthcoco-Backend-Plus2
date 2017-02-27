package com.dpdocter.request;

public class RegularCheckUpAddEditRequest {

	private String doctorId;

	private String regularCheckUpMonths;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(String regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	@Override
	public String toString() {
		return "RegularCheckUpAddEditRequest [doctorId=" + doctorId + ", regularCheckUpMonths=" + regularCheckUpMonths
				+ "]";
	}

}

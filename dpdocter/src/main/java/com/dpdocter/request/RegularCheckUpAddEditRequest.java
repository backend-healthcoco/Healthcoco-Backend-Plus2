package com.dpdocter.request;

public class RegularCheckUpAddEditRequest {

	private String doctorId;

	private Integer regularCheckUpMonths;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(Integer regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	@Override
	public String toString() {
		return "RegularCheckUpAddEditRequest [doctorId=" + doctorId + ", regularCheckUpMonths=" + regularCheckUpMonths
				+ "]";
	}

}

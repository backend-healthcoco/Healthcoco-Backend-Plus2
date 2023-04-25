package com.dpdocter.response;

public class DoctorPrescriptionAnalyticResponse {

	private Integer totalPrescription = 0;

	private Integer totalPrescriptionCreated = 0;

	public Integer getTotalPrescription() {
		return totalPrescription;
	}

	public void setTotalPrescription(Integer totalPrescription) {
		this.totalPrescription = totalPrescription;
	}

	public Integer getTotalPrescriptionCreated() {
		return totalPrescriptionCreated;
	}

	public void setTotalPrescriptionCreated(Integer totalPrescriptionCreated) {
		this.totalPrescriptionCreated = totalPrescriptionCreated;
	}
}
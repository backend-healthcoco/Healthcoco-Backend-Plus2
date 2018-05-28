package com.dpdocter.beans;

public class DentalWorksAmount {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String dentalLabLocationId;
	private String dentalLabHospitalId;
	private Double remainingAmount = 0.0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public Double getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(Double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	@Override
	public String toString() {
		return "DentalWorksAmount [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", dentalLabLocationId=" + dentalLabLocationId + ", dentalLabHospitalId="
				+ dentalLabHospitalId + ", remainingAmount=" + remainingAmount + "]";
	}

}

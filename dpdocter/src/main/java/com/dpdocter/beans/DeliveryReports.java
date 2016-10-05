package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.GenderType;

public class DeliveryReports extends GenericCollection {

	private String id;
	private Long deliveryDate;
	private String patientId;
	private Patient patient;
	private String mobileNumber;
	private String babyGender;
	private String deliveryType;
	private String formNo;
	private String remarks;
	private String doctorId;
	private String doctorName;
	private String locationId;
	private String locationName;
	private String hospitalId;
	private String hospitalName;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Long deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getBabyGender() {
		return babyGender;
	}

	public void setBabyGender(String babyGender) {
		this.babyGender = babyGender;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getFormNo() {
		return formNo;
	}

	public void setFormNo(String formNo) {
		this.formNo = formNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public String toString() {
		return "DeliveryReports [id=" + id + ", deliveryDate=" + deliveryDate + ", patientId=" + patientId
				+ ", patient=" + patient + ", mobileNumber=" + mobileNumber + ", babyGender=" + babyGender
				+ ", deliveryType=" + deliveryType + ", formNo=" + formNo + ", remarks=" + remarks + ", doctorId="
				+ doctorId + ", doctorName=" + doctorName + ", locationId=" + locationId + ", locationName="
				+ locationName + ", hospitalId=" + hospitalId + ", hospitalName=" + hospitalName + "]";
	}

}

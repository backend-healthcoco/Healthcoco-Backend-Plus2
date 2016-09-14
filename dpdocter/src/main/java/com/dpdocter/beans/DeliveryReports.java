package com.dpdocter.beans;

import com.dpdocter.enums.GenderType;

public class DeliveryReports {
	
	private String id;
	private String serialNo;
	private Long deliveryDate;
	private String patientId;
	private String mobileNumber;
	private GenderType babyGender;
	private String deliveryType;
	private String formNo;
	private String remarks;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
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
	public GenderType getBabyGender() {
		return babyGender;
	}
	public void setBabyGender(GenderType babyGender) {
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
	
	
	
	
	

}

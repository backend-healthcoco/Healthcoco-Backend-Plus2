package com.dpdocter.response;

import com.dpdocter.beans.Patient;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;

public class DeliveryReportsLookupResponse {

	private String id;
	private Long deliveryDate;
	private Integer deliveryTime;
	private String patientId;
	private Patient patient;
	private String mobileNumber;
	private String babyGender;
	private String deliveryType;
	private String formNo;
	private String remarks;
	private String doctorId;
	private UserCollection doctor;
	private String locationId;
	private LocationCollection location;
	private String hospitalId;
	private HospitalCollection hospital;
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
	public Integer getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(Integer deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
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
	public UserCollection getDoctor() {
		return doctor;
	}
	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public LocationCollection getLocation() {
		return location;
	}
	public void setLocation(LocationCollection location) {
		this.location = location;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public HospitalCollection getHospital() {
		return hospital;
	}
	public void setHospital(HospitalCollection hospital) {
		this.hospital = hospital;
	}
	@Override
	public String toString() {
		return "DeliveryReportsLookupResponse [id=" + id + ", deliveryDate=" + deliveryDate + ", deliveryTime="
				+ deliveryTime + ", patientId=" + patientId + ", patient=" + patient + ", mobileNumber=" + mobileNumber
				+ ", babyGender=" + babyGender + ", deliveryType=" + deliveryType + ", formNo=" + formNo + ", remarks="
				+ remarks + ", doctorId=" + doctorId + ", doctor=" + doctor + ", locationId=" + locationId
				+ ", location=" + location + ", hospitalId=" + hospitalId + ", hospital=" + hospital + "]";
	}
}

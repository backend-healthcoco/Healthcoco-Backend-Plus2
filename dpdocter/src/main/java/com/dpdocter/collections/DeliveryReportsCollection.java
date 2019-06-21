package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "delivery_report_cl")
public class DeliveryReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String uniqueDRId;
	@Field
	private String serialNo;
	@Field
	private Long deliveryDate;
	@Field
	private Integer deliveryTime;
	@Field
	private ObjectId patientId;
	@Field
	private String mobileNumber;
	@Field
	private String babyGender;
	@Field
	private String deliveryType;
	@Field
	private String formNo;
	@Field
	private String remarks;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Integer getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Integer deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getUniqueDRId() {
		return uniqueDRId;
	}

	public void setUniqueDRId(String uniqueDRId) {
		this.uniqueDRId = uniqueDRId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DeliveryReportsCollection [id=" + id + ", uniqueDRId=" + uniqueDRId + ", serialNo=" + serialNo
				+ ", deliveryDate=" + deliveryDate + ", deliveryTime=" + deliveryTime + ", patientId=" + patientId
				+ ", mobileNumber=" + mobileNumber + ", babyGender=" + babyGender + ", deliveryType=" + deliveryType
				+ ", formNo=" + formNo + ", remarks=" + remarks + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}

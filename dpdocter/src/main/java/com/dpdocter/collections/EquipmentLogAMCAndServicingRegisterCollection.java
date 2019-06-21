package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "equipment_log_amc_and_servicing_register")
public class EquipmentLogAMCAndServicingRegisterCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Long date;
	@Field
	private String nameOfEquipment;
	@Field
	private String idNum;
	@Field
	private Long dateOfPurchase;
	@Field
	private Integer warrantyPeriod;
	@Field
	private Integer amcPeriod;
	@Field
	private Long servicingDate;
	@Field
	private Long nextServicingDate;
	@Field
	private String remark;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getNameOfEquipment() {
		return nameOfEquipment;
	}

	public void setNameOfEquipment(String nameOfEquipment) {
		this.nameOfEquipment = nameOfEquipment;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public Long getDateOfPurchase() {
		return dateOfPurchase;
	}

	public void setDateOfPurchase(Long dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}

	public Integer getWarrantyPeriod() {
		return warrantyPeriod;
	}

	public void setWarrantyPeriod(Integer warrantyPeriod) {
		this.warrantyPeriod = warrantyPeriod;
	}

	public Integer getAmcPeriod() {
		return amcPeriod;
	}

	public void setAmcPeriod(Integer amcPeriod) {
		this.amcPeriod = amcPeriod;
	}

	public Long getServicingDate() {
		return servicingDate;
	}

	public void setServicingDate(Long servicingDate) {
		this.servicingDate = servicingDate;
	}

	public Long getNextServicingDate() {
		return nextServicingDate;
	}

	public void setNextServicingDate(Long nextServicingDate) {
		this.nextServicingDate = nextServicingDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}

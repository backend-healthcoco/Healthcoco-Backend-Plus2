package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class EquipmentLogAMCAndServicingRegister extends GenericCollection {
	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Long date;

	private String nameOfEquipment;

	private String idNum;

	private Long dateOfPurchase;

	private Integer warrantyPeriod;

	private Integer amcPeriod;

	private Long servicingDate;

	private Long nextServicingDate;

	private String remark;

	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

}

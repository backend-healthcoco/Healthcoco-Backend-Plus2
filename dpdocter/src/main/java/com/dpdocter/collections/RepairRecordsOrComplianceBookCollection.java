package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class RepairRecordsOrComplianceBookCollection extends GenericCollection {
	private ObjectId id;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private Long date;

	private String nameOfEquipment;

	private String idNum;

	private String complaints;

	private Long breakDownDateTime;

	private String repairDetail;

	private Long repairDateTime;

	private String remark;

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

	public String getComplaints() {
		return complaints;
	}

	public void setComplaints(String complaints) {
		this.complaints = complaints;
	}

	public Long getBreakDownDateTime() {
		return breakDownDateTime;
	}

	public void setBreakDownDateTime(Long breakDownDateTime) {
		this.breakDownDateTime = breakDownDateTime;
	}

	public String getRepairDetail() {
		return repairDetail;
	}

	public void setRepairDetail(String repairDetail) {
		this.repairDetail = repairDetail;
	}

	public Long getRepairDateTime() {
		return repairDateTime;
	}

	public void setRepairDateTime(Long repairDateTime) {
		this.repairDateTime = repairDateTime;
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

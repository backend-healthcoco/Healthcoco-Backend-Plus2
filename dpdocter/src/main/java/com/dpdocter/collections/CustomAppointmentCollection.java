package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "custom_appointment_cl")
public class CustomAppointmentCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId patintId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locatioinId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded = false;
	@Field
	private Date fromDate;
	@Field
	private Integer engageTime;
	@Field
	private Integer treatmentTime;
	@Field
	private Integer waitingTime;
	@Field
	private Date toDate;

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

	public ObjectId getPatintId() {
		return patintId;
	}

	public void setPatintId(ObjectId patintId) {
		this.patintId = patintId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocatioinId() {
		return locatioinId;
	}

	public void setLocatioinId(ObjectId locatioinId) {
		this.locatioinId = locatioinId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getTreatmentTime() {
		return treatmentTime;
	}

	public void setTreatmentTime(Integer treatmentTime) {
		this.treatmentTime = treatmentTime;
	}

	public Integer getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(Integer waitingTime) {
		this.waitingTime = waitingTime;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Integer getEngageTime() {
		return engageTime;
	}

	public void setEngageTime(Integer engageTime) {
		this.engageTime = engageTime;
	}

}

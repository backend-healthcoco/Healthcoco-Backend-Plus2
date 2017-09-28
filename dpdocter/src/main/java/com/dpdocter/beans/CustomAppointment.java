package com.dpdocter.beans;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;

public class CustomAppointment extends GenericCollection {

	private String id;

	private String patintName;

	private String doctorId;

	private String locatioinId;

	private String hospitalId;

	private Date fromDate;

	private Integer engageTime = 0;

	private Integer treatmentTime = 0;

	private Integer waitingTime = 0;

	private Date toDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatintName() {
		return patintName;
	}

	public void setPatintName(String patintName) {
		this.patintName = patintName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocatioinId() {
		return locatioinId;
	}

	public void setLocatioinId(String locatioinId) {
		this.locatioinId = locatioinId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getEngageTime() {
		return engageTime;
	}

	public void setEngageTime(Integer engageTime) {
		this.engageTime = engageTime;
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

}

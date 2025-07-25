package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class CustomAppointment extends GenericCollection {

	private String id;

	private String patientName;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Date date;

	private Integer inTime = 0;

	private Integer outTime = 0;

	private Integer engageTime = 0;

	private Integer treatmentTime = 0;

	private Integer waitingTime = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatintName(String patintName) {
		this.patientName = patintName;
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

	@Override
	public String toString() {
		return "CustomAppointment [id=" + id + ", patientName=" + patientName + ", doctorId=" + doctorId
				+ ", locatioinId=" + locationId + ", hospitalId=" + hospitalId + ", date=" + date + ", inTime=" + inTime
				+ ", outTime=" + outTime + ", engageTime=" + engageTime + ", treatmentTime=" + treatmentTime
				+ ", waitingTime=" + waitingTime + "]";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getInTime() {
		return inTime;
	}

	public void setInTime(Integer inTime) {
		this.inTime = inTime;
	}

	public Integer getOutTime() {
		return outTime;
	}

	public void setOutTime(Integer outTime) {
		this.outTime = outTime;
	}

}

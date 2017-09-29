package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class CustomAppointment extends GenericCollection {

	private String id;

	private String patintName;

	private String doctorId;

	private String locatioinId;

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
		return "CustomAppointment [id=" + id + ", patintName=" + patintName + ", doctorId=" + doctorId
				+ ", locatioinId=" + locatioinId + ", hospitalId=" + hospitalId + ", date=" + date + ", inTime="
				+ inTime + ", outTime=" + outTime + ", engageTime=" + engageTime + ", treatmentTime=" + treatmentTime
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

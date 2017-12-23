package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BrokenAppointment extends GenericCollection {
	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Long date;

	private Integer totalAppointment;

	private Integer noOfBrokenAppointment;

	private String work;

	private String patientName;

	private String remark;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Integer getTotalAppointment() {
		return totalAppointment;
	}

	public void setTotalAppointment(Integer totalAppointment) {
		this.totalAppointment = totalAppointment;
	}

	public Integer getNoOfBrokenAppointment() {
		return noOfBrokenAppointment;
	}

	public void setNoOfBrokenAppointment(Integer noOfBrokenAppointment) {
		this.noOfBrokenAppointment = noOfBrokenAppointment;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

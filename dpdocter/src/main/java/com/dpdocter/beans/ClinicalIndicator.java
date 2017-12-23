package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ClinicalIndicatorEnum;

public class ClinicalIndicator extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Long date;

	private String nameOfPt;

	private Integer totalOfDone;

	private Integer totalOfFailure;

	private String doctorName;

	private String remark;

	private ClinicalIndicatorEnum indicatortype;

	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getNameOfPt() {
		return nameOfPt;
	}

	public void setNameOfPt(String nameOfPt) {
		this.nameOfPt = nameOfPt;
	}

	public Integer getTotalOfDone() {
		return totalOfDone;
	}

	public void setTotalOfDone(Integer totalOfDone) {
		this.totalOfDone = totalOfDone;
	}

	public Integer getTotalOfFailure() {
		return totalOfFailure;
	}

	public void setTotalOfFailure(Integer totalOfFailure) {
		this.totalOfFailure = totalOfFailure;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public ClinicalIndicatorEnum getIndicatortype() {
		return indicatortype;
	}

	public void setIndicatortype(ClinicalIndicatorEnum indicatortype) {
		this.indicatortype = indicatortype;
	}

}

package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ClinicalIndicatorEnum;

@Document(collection = "clinical_indicator_cl")
public class ClinicalIndicatorCollection extends GenericCollection {
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
	private String nameOfPt;
	@Field
	private Integer totalOfDone;
	@Field
	private Integer totalOfFailure;
	@Field
	private String doctorName;
	@Field
	private String remark;
	@Field
	private Boolean discarded = false;
	@Field
	private ClinicalIndicatorEnum indicatortype = ClinicalIndicatorEnum.RCT_FAILURE;

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

	public ClinicalIndicatorEnum getIndicatortype() {
		return indicatortype;
	}

	public void setIndicatortype(ClinicalIndicatorEnum indicatortype) {
		this.indicatortype = indicatortype;
	}

}

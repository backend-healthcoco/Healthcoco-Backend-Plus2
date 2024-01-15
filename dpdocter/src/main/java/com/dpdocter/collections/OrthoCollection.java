package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Fields;
import com.dpdocter.beans.IPRDetail;
import com.dpdocter.enums.RetainerType;

//@Document(collection = "ortho_cl")
public class OrthoCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Indexed
	private ObjectId patientId;

	@Field
	private Boolean discarded = false;
	
	@Field
	private String createdBy;

	@Field
	private Date startDate;

	@Field
	private String treatmentType;

	@Field
	private String brandOfAligner;

	@Field
	private Integer noOfUpperAligner;

	@Field
	private Integer noOfLowerAligner;

	@Field
	private Integer noOfDaysToWearAligner;
	@Field

	private boolean hasAttachment = false;

	@Field
	private List<Fields> toothNumbers;

	@Field
	private boolean isIPR = false;
	
	@Field
	private boolean isZeroAlignerRequired = false;

	@Field
	private List<IPRDetail> iprDetail;

	@Field
	private RetainerType retainerType;

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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(String treatmentType) {
		this.treatmentType = treatmentType;
	}

	public String getBrandOfAligner() {
		return brandOfAligner;
	}

	public void setBrandOfAligner(String brandOfAligner) {
		this.brandOfAligner = brandOfAligner;
	}

	public Integer getNoOfUpperAligner() {
		return noOfUpperAligner;
	}

	public void setNoOfUpperAligner(Integer noOfUpperAligner) {
		this.noOfUpperAligner = noOfUpperAligner;
	}

	public Integer getNoOfLowerAligner() {
		return noOfLowerAligner;
	}

	public void setNoOfLowerAligner(Integer noOfLowerAligner) {
		this.noOfLowerAligner = noOfLowerAligner;
	}

	public Integer getNoOfDaysToWearAligner() {
		return noOfDaysToWearAligner;
	}

	public void setNoOfDaysToWearAligner(Integer noOfDaysToWearAligner) {
		this.noOfDaysToWearAligner = noOfDaysToWearAligner;
	}

	public boolean isHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	public List<Fields> getToothNumbers() {
		return toothNumbers;
	}

	public void setToothNumbers(List<Fields> toothNumbers) {
		this.toothNumbers = toothNumbers;
	}

	public boolean isIPR() {
		return isIPR;
	}

	public void setIPR(boolean isIPR) {
		this.isIPR = isIPR;
	}

	public List<IPRDetail> getIprDetail() {
		return iprDetail;
	}

	public void setIprDetail(List<IPRDetail> iprDetail) {
		this.iprDetail = iprDetail;
	}

	public RetainerType getRetainerType() {
		return retainerType;
	}

	public void setRetainerType(RetainerType retainerType) {
		this.retainerType = retainerType;
	}

	public boolean isZeroAlignerRequired() {
		return isZeroAlignerRequired;
	}

	public void setZeroAlignerRequired(boolean isZeroAlignerRequired) {
		this.isZeroAlignerRequired = isZeroAlignerRequired;
	}

}

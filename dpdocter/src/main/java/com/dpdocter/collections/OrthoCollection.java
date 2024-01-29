package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Fields;
import com.dpdocter.beans.IPRDetail;
import com.dpdocter.enums.BrandOfAligner;
import com.dpdocter.enums.RetainerType;
import com.dpdocter.enums.TreatmentType;

@Document(collection = "ortho_data_cl")
public class OrthoCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId patientId;

	@Field
	private Boolean discarded = false;

	@Field
	private Date startDate;

	@Field
	private TreatmentType treatmentType;

	@Field
	private BrandOfAligner brandOfAligner;

	@Field
	private Integer noOfUpperAligner;

	@Field
	private Integer noOfLowerAligner;

	@Field
	private Integer noOfDaysToWearAligner;
	@Field

	private Boolean hasAttachment = false;

	@Field
	private List<String> toothNumbers;

	@Field
	private Boolean isIPR = false;

	@Field
	private Boolean isZeroAlignerRequired = false;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	
	public TreatmentType getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
	}

	public BrandOfAligner getBrandOfAligner() {
		return brandOfAligner;
	}

	public void setBrandOfAligner(BrandOfAligner brandOfAligner) {
		this.brandOfAligner = brandOfAligner;
	}

	public Boolean getIsIPR() {
		return isIPR;
	}

	public void setIsIPR(Boolean isIPR) {
		this.isIPR = isIPR;
	}

	public Boolean getIsZeroAlignerRequired() {
		return isZeroAlignerRequired;
	}

	public void setIsZeroAlignerRequired(Boolean isZeroAlignerRequired) {
		this.isZeroAlignerRequired = isZeroAlignerRequired;
	}

	public Boolean getHasAttachment() {
		return hasAttachment;
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

	public Boolean isHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(Boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	public List<String> getToothNumbers() {
		return toothNumbers;
	}

	public void setToothNumbers(List<String> toothNumbers) {
		this.toothNumbers = toothNumbers;
	}

	public Boolean isIPR() {
		return isIPR;
	}

	public void setIPR(Boolean isIPR) {
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

	public Boolean isZeroAlignerRequired() {
		return isZeroAlignerRequired;
	}

	public void setZeroAlignerRequired(Boolean isZeroAlignerRequired) {
		this.isZeroAlignerRequired = isZeroAlignerRequired;
	}

}

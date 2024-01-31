package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.IPRDetail;
import com.dpdocter.enums.BrandOfAligner;
import com.dpdocter.enums.RetainerType;
import com.dpdocter.enums.TreatmentType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OrthoEditRequest {
	private String id;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String createdBy;

	private Date startDate;

	private TreatmentType treatmentType;

	private BrandOfAligner brandOfAligner;

	private String noOfUpperAligner;
	
	private String noOfLowerAligner;
	
	private String noOfDaysToWearAligner;
	
	private Boolean hasAttachment = false;

	private List<String> toothNumbers;
	
	private Boolean isIPR = false;
	
	private Boolean isZeroAlignerRequired = false;

	private List<IPRDetail> iprDetail;
	
	private RetainerType retainerType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public String getNoOfUpperAligner() {
		return noOfUpperAligner;
	}

	public void setNoOfUpperAligner(String noOfUpperAligner) {
		this.noOfUpperAligner = noOfUpperAligner;
	}

	public String getNoOfLowerAligner() {
		return noOfLowerAligner;
	}

	public void setNoOfLowerAligner(String noOfLowerAligner) {
		this.noOfLowerAligner = noOfLowerAligner;
	}

	public String getNoOfDaysToWearAligner() {
		return noOfDaysToWearAligner;
	}

	public void setNoOfDaysToWearAligner(String noOfDaysToWearAligner) {
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
	
}

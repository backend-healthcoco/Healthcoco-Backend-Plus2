package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Fields;
import com.dpdocter.beans.IPRDetail;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.RetainerType;

public class OrthoResponse extends GenericCollection{
	private String id;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String createdBy;

	private Date startDate;

	private String treatmentType;

	private String brandOfAligner;

	private String noOfUpperAligner;
	
	private String noOfLowerAligner;
	
	private String noOfDaysToWearAligner;
	
	private boolean hasAttachment = false;

	private List<Fields> toothNumbers;
	
	private boolean isIPR = false;
	private boolean isZeroAlignerRequired = false;

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

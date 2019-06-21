package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.EyeTest;

public class EyePrescriptionCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId patientId;

	@Field
	private EyeTest leftEyeTest;

	@Field
	private EyeTest rightEyeTest;

	@Field
	private String type;

	@Field
	private Double pupilaryDistance;

	@Field
	private String lensType;

	@Field
	private String usage;

	@Field
	private String remarks;

	@Field
	private String replacementInterval;

	@Field
	private String lensColor;

	@Field
	private String lensBrand;

	@Field
	private String prescriptionCode;

	@Field
	private Boolean inHistory = false;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean isOTPVerified = false;

	@Field
	private ObjectId visitId;

	@Field
	private String quality;

	@Field
	private Boolean isPatientDiscarded = false;
	
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

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsOTPVerified() {
		return isOTPVerified;
	}

	public void setIsOTPVerified(Boolean isOTPVerified) {
		this.isOTPVerified = isOTPVerified;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public ObjectId getVisitId() {
		return visitId;
	}

	public void setVisitId(ObjectId visitId) {
		this.visitId = visitId;
	}

	public String getPrescriptionCode() {
		return prescriptionCode;
	}

	public void setPrescriptionCode(String prescriptionCode) {
		this.prescriptionCode = prescriptionCode;
	}

	public EyeTest getLeftEyeTest() {
		return leftEyeTest;
	}

	public void setLeftEyeTest(EyeTest leftEyeTest) {
		this.leftEyeTest = leftEyeTest;
	}

	public EyeTest getRightEyeTest() {
		return rightEyeTest;
	}

	public void setRightEyeTest(EyeTest rightEyeTest) {
		this.rightEyeTest = rightEyeTest;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getPupilaryDistance() {
		return pupilaryDistance;
	}

	public void setPupilaryDistance(Double pupilaryDistance) {
		this.pupilaryDistance = pupilaryDistance;
	}

	public String getLensType() {
		return lensType;
	}

	public void setLensType(String lensType) {
		this.lensType = lensType;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReplacementInterval() {
		return replacementInterval;
	}

	public void setReplacementInterval(String replacementInterval) {
		this.replacementInterval = replacementInterval;
	}

	public String getLensColor() {
		return lensColor;
	}

	public void setLensColor(String lensColor) {
		this.lensColor = lensColor;
	}

	public String getLensBrand() {
		return lensBrand;
	}

	public void setLensBrand(String lensBrand) {
		this.lensBrand = lensBrand;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "EyePrescriptionCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", leftEyeTest=" + leftEyeTest + ", rightEyeTest=" + rightEyeTest + ", type=" + type
				+ ", pupilaryDistance=" + pupilaryDistance + ", lensType=" + lensType + ", usage=" + usage
				+ ", remarks=" + remarks + ", replacementInterval=" + replacementInterval + ", lensColor=" + lensColor
				+ ", lensBrand=" + lensBrand + ", prescriptionCode=" + prescriptionCode + ", inHistory=" + inHistory
				+ ", discarded=" + discarded + ", isOTPVerified=" + isOTPVerified + ", visitId=" + visitId
				+ ", quality=" + quality + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}

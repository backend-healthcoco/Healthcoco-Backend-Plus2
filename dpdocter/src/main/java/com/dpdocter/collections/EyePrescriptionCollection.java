package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.EyeTest;
import com.dpdocter.beans.VisualAcuity;

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
	private List<VisualAcuity> visualAcuities;

	@Field
	private List<EyeTest> eyeTests;

	@Field
	private Boolean inHistory = false;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean isOTPVerified = false;

	@Field
	private String visitId;

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

	public List<VisualAcuity> getVisualAcuities() {
		return visualAcuities;
	}

	public void setVisualAcuities(List<VisualAcuity> visualAcuities) {
		this.visualAcuities = visualAcuities;
	}

	public List<EyeTest> getEyeTests() {
		return eyeTests;
	}

	public void setEyeTests(List<EyeTest> eyeTests) {
		this.eyeTests = eyeTests;
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

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	@Override
	public String toString() {
		return "EyeObservationCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", visualAcuities=" + visualAcuities
				+ ", eyeTests=" + eyeTests + ", inHistory=" + inHistory + ", discarded=" + discarded
				+ ", isOTPVerified=" + isOTPVerified + "]";
	}

}

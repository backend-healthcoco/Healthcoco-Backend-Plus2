package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.EyeTest;
import com.dpdocter.beans.VisualAcuity;

public class EyeObservationCollection extends GenericCollection {

	private ObjectId id;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private ObjectId patientId;

	private List<VisualAcuity> visualAcuities;

	private List<EyeTest> eyeTests;

	private Boolean inHistory = false;

	private Boolean discarded = false;

	private Boolean isOTPVerified = false;

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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "EyeObservationCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", visualAcuities=" + visualAcuities
				+ ", eyeTests=" + eyeTests + ", inHistory=" + inHistory + ", discarded=" + discarded
				+ ", isOTPVerified=" + isOTPVerified + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}

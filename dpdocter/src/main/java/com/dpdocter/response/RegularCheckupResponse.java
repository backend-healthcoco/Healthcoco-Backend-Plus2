package com.dpdocter.response;

import org.bson.types.ObjectId;

import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientVisit;

public class RegularCheckupResponse {

	private ObjectId id;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private PatientCard patient;

	private String localPatientName;

	private String locationName;

	private PatientVisit visit;

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

	public PatientCard getPatient() {
		return patient;
	}

	public void setPatient(PatientCard patient) {
		this.patient = patient;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public PatientVisit getVisit() {
		return visit;
	}

	public void setVisit(PatientVisit visit) {
		this.visit = visit;
	}

	@Override
	public String toString() {
		return "RegularCheckupResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patient=" + patient + ", localPatientName=" + localPatientName
				+ ", locationName=" + locationName + ", visit=" + visit + "]";
	}

}

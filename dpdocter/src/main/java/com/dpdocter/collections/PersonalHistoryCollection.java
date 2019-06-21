package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "personal_history_cl")
public class PersonalHistoryCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String diet;
	@Field
	private String addictions;
	@Field
	private String bowelHabit;
	@Field
	private String bladderHabit;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDiet() {
		return diet;
	}

	public void setDiet(String diet) {
		this.diet = diet;
	}

	public String getAddictions() {
		return addictions;
	}

	public void setAddictions(String addictions) {
		this.addictions = addictions;
	}

	public String getBowelHabit() {
		return bowelHabit;
	}

	public void setBowelHabit(String bowelHabit) {
		this.bowelHabit = bowelHabit;
	}

	public String getBladderHabit() {
		return bladderHabit;
	}

	public void setBladderHabit(String bladderHabit) {
		this.bladderHabit = bladderHabit;
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PersonalHistoryCollection [id=" + id + ", diet=" + diet + ", addictions=" + addictions + ", bowelHabit="
				+ bowelHabit + ", bladderHabit=" + bladderHabit + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}

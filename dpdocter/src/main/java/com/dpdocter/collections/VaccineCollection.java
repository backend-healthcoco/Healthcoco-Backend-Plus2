package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Age;
import com.dpdocter.enums.VaccineRoute;
import com.dpdocter.enums.VaccineStatus;

@Document(collection = "vaccine_cl")
public class VaccineCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId vaccineId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private String name;
	@Field
	private String longName;
	@Field
	private Date dueDate;
	@Field
	private VaccineStatus status = VaccineStatus.PLANNED;
	@Field
	private VaccineRoute route;
	@Field
	private String bodySite;
	@Field
	private Integer dosage;
	@Field
	private ObjectId vaccineBrandId;
	@Field
	private Date givenDate;
	@Field
	private Age age;
	@Field
	private String duration;
	@Field
	private Integer periodTime;
	@Field
	private String note;
	@Field
	private Boolean isUpdatedByPatient = false;
	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public VaccineStatus getStatus() {
		return status;
	}

	public void setStatus(VaccineStatus status) {
		this.status = status;
	}

	public VaccineRoute getRoute() {
		return route;
	}

	public void setRoute(VaccineRoute route) {
		this.route = route;
	}

	public String getBodySite() {
		return bodySite;
	}

	public void setBodySite(String bodySite) {
		this.bodySite = bodySite;
	}

	public Integer getDosage() {
		return dosage;
	}

	public void setDosage(Integer dosage) {
		this.dosage = dosage;
	}

	public ObjectId getVaccineBrandId() {
		return vaccineBrandId;
	}

	public void setVaccineBrandId(ObjectId vaccineBrandId) {
		this.vaccineBrandId = vaccineBrandId;
	}

	public Date getGivenDate() {
		return givenDate;
	}

	public void setGivenDate(Date givenDate) {
		this.givenDate = givenDate;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
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

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(Integer periodTime) {
		this.periodTime = periodTime;
	}

	public ObjectId getVaccineId() {
		return vaccineId;
	}

	public void setVaccineId(ObjectId vaccineId) {
		this.vaccineId = vaccineId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getIsUpdatedByPatient() {
		return isUpdatedByPatient;
	}

	public void setIsUpdatedByPatient(Boolean isUpdatedByPatient) {
		this.isUpdatedByPatient = isUpdatedByPatient;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "VaccineCollection [id=" + id + ", vaccineId=" + vaccineId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", name=" + name
				+ ", longName=" + longName + ", dueDate=" + dueDate + ", status=" + status + ", route=" + route
				+ ", bodySite=" + bodySite + ", dosage=" + dosage + ", vaccineBrandId=" + vaccineBrandId
				+ ", givenDate=" + givenDate + ", age=" + age + ", duration=" + duration + ", periodTime=" + periodTime
				+ ", note=" + note + ", isUpdatedByPatient=" + isUpdatedByPatient + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}
}

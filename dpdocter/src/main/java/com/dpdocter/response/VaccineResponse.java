package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.VaccineBrand;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.VaccineRoute;
import com.dpdocter.enums.VaccineStatus;

public class VaccineResponse extends GenericCollection {

	private String id;
	private String vaccineId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String name;
	private Date dueDate;
	private VaccineStatus status = VaccineStatus.GIVEN;
	private VaccineRoute route;
	private String bodySite;
	private Integer dosage;
	private Date givenDate;
	private Age age;
	private VaccineBrand vaccineBrand;
	private String note;
	private String duration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public VaccineBrand getVaccineBrand() {
		return vaccineBrand;
	}

	public void setVaccineBrand(VaccineBrand vaccineBrand) {
		this.vaccineBrand = vaccineBrand;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getVaccineId() {
		return vaccineId;
	}

	public void setVaccineId(String vaccineId) {
		this.vaccineId = vaccineId;
	}

}

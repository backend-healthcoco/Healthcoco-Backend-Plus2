package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Age;
import com.dpdocter.enums.VaccineRoute;
import com.dpdocter.enums.VaccineStatus;

public class VaccineCollection extends GenericCollection {

	private ObjectId id;
	private String name;
	private Date dueDate;
	private VaccineStatus status = VaccineStatus.GIVEN;
	private VaccineRoute route;
	private String bodySite;
	private Integer dosage;
	private ObjectId vaccineBrandId;
	private Date givenDate;
	private Age age;

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

	@Override
	public String toString() {
		return "VaccineCollection [id=" + id + ", name=" + name + ", dueDate=" + dueDate + ", status=" + status
				+ ", route=" + route + ", bodySite=" + bodySite + ", dosage=" + dosage + ", vaccineBrandId="
				+ vaccineBrandId + ", givenDate=" + givenDate + ", age=" + age + "]";
	}

}

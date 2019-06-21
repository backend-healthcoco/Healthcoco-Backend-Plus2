package com.dpdocter.beans;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.VaccineRoute;
import com.dpdocter.enums.VaccineStatus;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Vaccine extends GenericCollection {

	private String id;
	private String name;
	private Date dueDate;
	private VaccineStatus status = VaccineStatus.PLANNED;
	private VaccineRoute route;
	private String bodySite;
	private Integer dosage;
	private Date givenDate;
	private VaccineBrand vaccineBrand;
	private Age age;
	private String duration;
	private Integer periodTime;

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

	@Override
	public String toString() {
		return "Vaccine [name=" + name + ", dueDate=" + dueDate + ", status=" + status + ", route=" + route
				+ ", bodySite=" + bodySite + ", dosage=" + dosage + ", givenDate=" + givenDate + ", age=" + age + "]";
	}

}

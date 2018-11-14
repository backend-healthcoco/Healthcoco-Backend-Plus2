package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class VaccineBrandAssociation extends GenericCollection {

	private String id;
	private String vaccineId;
	private String vaccineBrandId;
	private String isActive;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVaccineId() {
		return vaccineId;
	}

	public void setVaccineId(String vaccineId) {
		this.vaccineId = vaccineId;
	}

	public String getVaccineBrandId() {
		return vaccineBrandId;
	}

	public void setVaccineBrandId(String vaccineBrandId) {
		this.vaccineBrandId = vaccineBrandId;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "VaccineBrandAssociation [id=" + id + ", vaccineId=" + vaccineId + ", vaccineBrandId=" + vaccineBrandId
				+ ", isActive=" + isActive + "]";
	}

}

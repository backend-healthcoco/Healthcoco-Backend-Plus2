package com.dpdocter.response;

import com.dpdocter.beans.VaccineBrand;
import com.dpdocter.collections.GenericCollection;

public class VaccineBrandAssociationResponse extends GenericCollection {

	private String id;
	private String vaccineId;
	private String vaccineBrandId;
	private VaccineBrand vaccineBrand;
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

	public VaccineBrand getVaccineBrand() {
		return vaccineBrand;
	}

	public void setVaccineBrand(VaccineBrand vaccineBrand) {
		this.vaccineBrand = vaccineBrand;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

}

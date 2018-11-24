package com.dpdocter.response;

import java.util.List;

public class GroupedVaccineBrandAssociationResponse {

	private String id;
	private String name;
	private List<VaccineBrandAssociationResponse> vaccineBrandAssociationResponses;

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

	public List<VaccineBrandAssociationResponse> getVaccineBrandAssociationResponses() {
		return vaccineBrandAssociationResponses;
	}

	public void setVaccineBrandAssociationResponses(
			List<VaccineBrandAssociationResponse> vaccineBrandAssociationResponses) {
		this.vaccineBrandAssociationResponses = vaccineBrandAssociationResponses;
	}

}

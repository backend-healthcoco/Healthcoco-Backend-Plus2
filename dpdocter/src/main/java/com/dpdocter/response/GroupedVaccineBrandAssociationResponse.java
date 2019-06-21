package com.dpdocter.response;

import java.util.List;

public class GroupedVaccineBrandAssociationResponse {

	private String id;
	private String name;
	private String duration;
	private Integer periodTime;
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
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public List<VaccineBrandAssociationResponse> getVaccineBrandAssociationResponses() {
		return vaccineBrandAssociationResponses;
	}

	public void setVaccineBrandAssociationResponses(
			List<VaccineBrandAssociationResponse> vaccineBrandAssociationResponses) {
		this.vaccineBrandAssociationResponses = vaccineBrandAssociationResponses;
	}

	public Integer getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(Integer periodTime) {
		this.periodTime = periodTime;
	}

}

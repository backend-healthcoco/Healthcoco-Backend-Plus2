package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Document(indexName = "services_in", type = "services")
public class ESServicesDocument {
	@Id
	private String id;

	@Field(type = FieldType.String)
	private String service;

	@Field(type = FieldType.String)
	private String formattedService;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@MultiField(mainField = @Field(type = FieldType.String))
	private List<String> specialities;

	/*
	 * @MultiField(mainField = @Field(type = FieldType.String, index =
	 * FieldIndex.not_analyzed)) private List<String> formattedSpecialities;
	 */

	@Field(type = FieldType.String)
	private List<String> specialityIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<String> getSpecialityIds() {
		return specialityIds;
	}

	public void setSpecialityIds(List<String> specialityIds) {
		this.specialityIds = specialityIds;
	}

	public String getFormattedService() {
		return formattedService;
	}

	public void setFormattedService(String formattedService) {
		this.formattedService = formattedService;
	}


	@Override
	public String toString() {
		return "ESServicesDocument [id=" + id + ", service=" + service + ", formattedService=" + formattedService
				+ ", updatedTime=" + updatedTime + ", specialities=" + specialities + ", specialityIds=" + specialityIds
				+ "]";
	}

}

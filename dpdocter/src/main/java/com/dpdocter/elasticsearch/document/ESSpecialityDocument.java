package com.dpdocter.elasticsearch.document;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "specialities_in", type = "specialities")
public class ESSpecialityDocument {
	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String speciality;

	@Field(type = FieldType.Text)
	private String superSpeciality;

	@Field(type = FieldType.Text)
	private String metaTitle;

	@Field(type = FieldType.Text)
	private String formattedSpeciality;

	@Field(type = FieldType.Text)
	private String formattedSuperSpeciality;

	@Field(type = FieldType.Text)
	private String code;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSuperSpeciality() {
		return superSpeciality;
	}

	public void setSuperSpeciality(String superSpeciality) {
		this.superSpeciality = superSpeciality;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getFormattedSpeciality() {
		return formattedSpeciality;
	}

	public void setFormattedSpeciality(String formattedSpeciality) {
		this.formattedSpeciality = formattedSpeciality;
	}

	public String getFormattedSuperSpeciality() {
		return formattedSuperSpeciality;
	}

	public void setFormattedSuperSpeciality(String formattedSuperSpeciality) {
		this.formattedSuperSpeciality = formattedSuperSpeciality;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	@Override
	public String toString() {
		return "ESSpecialityDocument [id=" + id + ", speciality=" + speciality + ", superSpeciality=" + superSpeciality
				+ ", formattedSpeciality=" + formattedSpeciality + ", formattedSuperSpeciality="
				+ formattedSuperSpeciality + ", metaTitle=" + metaTitle + ", code=" + code + ", updatedTime="
				+ updatedTime + "]";
	}
}

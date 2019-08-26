package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Document(indexName = "symptom_disease_condition_in", type = "symptom_disease_condition")
public class ESSymptomDiseaseConditionDocument {
    @Id
    private String id;

    @Field(type = FieldType.String)
    private String name;
    
    @Field(type = FieldType.String)
    private String type;
    
    @Field(type = FieldType.Date)
    private Date updatedTime = new Date();

	@MultiField(mainField = @Field(type = FieldType.String))
    private List<String> specialities;
    
	@MultiField(mainField = @Field(type = FieldType.String))
    private List<String> formattedSpecialities;
    
	@MultiField(mainField = @Field(type = FieldType.String))
    private List<String> specialityIds;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public List<String> getFormattedSpecialities() {
		return formattedSpecialities;
	}

	public void setFormattedSpecialities(List<String> formattedSpecialities) {
		this.formattedSpecialities = formattedSpecialities;
	}

	public List<String> getSpecialityIds() {
		return specialityIds;
	}

	public void setSpecialityIds(List<String> specialityIds) {
		this.specialityIds = specialityIds;
	}

	@Override
	public String toString() {
		return "ESSymptomDiseaseConditionDocument [id=" + id + ", name=" + name + ", type=" + type + ", updatedTime="
				+ updatedTime + ", specialities=" + specialities + ", formattedSpecialities=" + formattedSpecialities
				+ ", specialityIds=" + specialityIds + "]";
	}
}

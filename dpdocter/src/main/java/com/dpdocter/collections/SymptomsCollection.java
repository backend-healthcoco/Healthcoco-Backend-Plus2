package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "symptom_cl")
public class SymptomsCollection {

    @Id
    private String id;

    @Field
    private String specialityId;

    @Field
    private String symptom;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSpecialityId() {
	return specialityId;
    }

    public void setSpecialityId(String specialityId) {
	this.specialityId = specialityId;
    }

    public String getSymptom() {
	return symptom;
    }

    public void setSymptom(String symptom) {
	this.symptom = symptom;
    }

    @Override
    public String toString() {
	return "SymptomsCollection [id=" + id + ", specialityId=" + specialityId + ", symptom=" + symptom + "]";
    }
}

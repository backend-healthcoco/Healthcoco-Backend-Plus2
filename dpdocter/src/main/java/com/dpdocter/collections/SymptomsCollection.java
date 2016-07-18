package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "symptom_cl")
public class SymptomsCollection {

    @Id
    private ObjectId id;

    @Field
    private ObjectId specialityId;

    @Field
    private String symptom;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getSpecialityId() {
	return specialityId;
    }

    public void setSpecialityId(ObjectId specialityId) {
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

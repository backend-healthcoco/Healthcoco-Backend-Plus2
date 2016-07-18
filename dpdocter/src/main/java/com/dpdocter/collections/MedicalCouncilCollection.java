package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "medical_council_cl")
public class MedicalCouncilCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private String medicalCouncil;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public String getMedicalCouncil() {
	return medicalCouncil;
    }

    public void setMedicalCouncil(String medicalCouncil) {
	this.medicalCouncil = medicalCouncil;
    }

    @Override
    public String toString() {
	return "MedicalCouncilCollection [id=" + id + ", medicalCouncil=" + medicalCouncil + "]";
    }

}

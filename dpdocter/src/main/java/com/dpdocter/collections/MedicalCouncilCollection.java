package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "medical_council_cl")
public class MedicalCouncilCollection {
    @Id
    private String id;

    @Field
    private String medicalCouncil;

    public String getId() {
	return id;
    }

    public void setId(String id) {
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

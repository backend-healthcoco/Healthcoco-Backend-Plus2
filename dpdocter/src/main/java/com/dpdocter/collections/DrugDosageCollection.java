package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "drug_dosage_cl")
public class DrugDosageCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String dosage;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDosage() {
	return dosage;
    }

    public void setDosage(String dosage) {
	this.dosage = dosage;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

    @Override
    public String toString() {
	return "DrugDosageCollection [id=" + id + ", dosage=" + dosage + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", discarded=" + discarded + "]";
    }

}

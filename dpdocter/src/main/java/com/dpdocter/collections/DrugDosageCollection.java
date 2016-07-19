package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DosageWithTime;

@Document(collection = "drug_dosage_cl")
public class DrugDosageCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Field
    private DosageWithTime dosage;

    @Field
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

    @Field
    private Boolean discarded = false;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public DosageWithTime getDosage() {
	return dosage;
    }

    public void setDosage(DosageWithTime dosage) {
	this.dosage = dosage;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
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

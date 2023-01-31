package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "treatment_services_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'doctorId' : 1, 'treatmentCode': 1}") })
public class TreatmentServicesCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String name;

	@Field
	private String speciality;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId doctorId;

	@Field
	private Boolean discarded = false;

	@Field
	private long rankingCount = 0;

	@Field
	private double cost = 0.0;

	@Field
	private String treatmentCode;

	@Field
	private String category;

	@Field
	private List<String> fieldsRequired;
	
	@Field
	private ObjectId ratelistId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
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

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getTreatmentCode() {
		return treatmentCode;
	}

	public void setTreatmentCode(String treatmentCode) {
		this.treatmentCode = treatmentCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getFieldsRequired() {
		return fieldsRequired;
	}

	public void setFieldsRequired(List<String> fieldsRequired) {
		this.fieldsRequired = fieldsRequired;
	}

	public ObjectId getRatelistId() {
		return ratelistId;
	}

	public void setRatelistId(ObjectId ratelistId) {
		this.ratelistId = ratelistId;
	}

	@Override
	public String toString() {
		return "TreatmentServicesCollection [id=" + id + ", name=" + name + ", speciality=" + speciality
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
				+ ", discarded=" + discarded + ", rankingCount=" + rankingCount + ", cost=" + cost + ", treatmentCode="
				+ treatmentCode + ", category=" + category + ", fieldsRequired=" + fieldsRequired + "]";
	}

}

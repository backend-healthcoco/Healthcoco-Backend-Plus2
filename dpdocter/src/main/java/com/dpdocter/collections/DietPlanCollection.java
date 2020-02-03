package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DietplanItem;
import com.dpdocter.beans.MealQuantity;
@Document(collection = "diet_plan_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class DietPlanCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String uniquePlanId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean discarded = false;
	@Field
	private List<DietplanItem> items;
	@Field
	private MealQuantity calories;
	@Field
	private Boolean isPatientDiscarded = false;
	@Field
	private String advice;
	@Field
	private String principle;
	
	public String getPrinciple() {
		return principle;
	}
	public void setPrinciple(String principle) {
		this.principle = principle;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUniquePlanId() {
		return uniquePlanId;
	}

	public void setUniquePlanId(String uniquePlanId) {
		this.uniquePlanId = uniquePlanId;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<DietplanItem> getItems() {
		return items;
	}

	public void setItems(List<DietplanItem> items) {
		this.items = items;
	}

	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}
	@Override
	public String toString() {
		return "DietPlanCollection [id=" + id + ", uniquePlanId=" + uniquePlanId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", discarded=" + discarded + ", items=" + items + ", calories=" + calories + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}

package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DietPlan extends GenericCollection {

	private String id;

	private String uniquePlanId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private Boolean discarded = false;

	private List<DietplanAddItem> items;

	private MealQuantity calories;
	
	private List<EquivalentQuantities> equivalentMeasurements;
	
	private String advice;
	
	private String principle;
	
	private long timeTaken;
	
	private String cloneTemplateId;
	
	private String cloneTemplateName;
	  
	public String getPrinciple() {
		return principle;
	}
	public void setPrinciple(String principle) {
		this.principle = principle;
	}
	
	public MealQuantity getCalories() {
		return calories;
	}

	public void setCalories(MealQuantity calories) {
		this.calories = calories;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniquePlanId() {
		return uniquePlanId;
	}

	public void setUniquePlanId(String uniquePlanId) {
		this.uniquePlanId = uniquePlanId;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<DietplanAddItem> getItems() {
		return items;
	}

	public void setItems(List<DietplanAddItem> items) {
		this.items = items;
	}

	public List<EquivalentQuantities> getEquivalentMeasurements() {
		return equivalentMeasurements;
	}

	public void setEquivalentMeasurements(List<EquivalentQuantities> equivalentMeasurements) {
		this.equivalentMeasurements = equivalentMeasurements;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}
	public long getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}
	public String getCloneTemplateId() {
		return cloneTemplateId;
	}
	public void setCloneTemplateId(String cloneTemplateId) {
		this.cloneTemplateId = cloneTemplateId;
	}
	public String getCloneTemplateName() {
		return cloneTemplateName;
	}
	public void setCloneTemplateName(String cloneTemplateName) {
		this.cloneTemplateName = cloneTemplateName;
	}
	@Override
	public String toString() {
		return "DietPlan [id=" + id + ", uniquePlanId=" + uniquePlanId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", items=" + items + ", calories=" + calories + ", equivalentMeasurements=" + equivalentMeasurements
				+ ", advice=" + advice + ", principle=" + principle + ", timeTaken=" + timeTaken + ", cloneTemplateId="
				+ cloneTemplateId + ", cloneTemplateName=" + cloneTemplateName + "]";
	}
	
}

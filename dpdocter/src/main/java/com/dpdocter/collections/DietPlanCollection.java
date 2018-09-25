package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DietplanItem;

public class DietPlanCollection {
	private ObjectId id;

	private String uniquePlanId;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private ObjectId patientId;

	private Boolean discarded = false;

	private List<DietplanItem> items;

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

}

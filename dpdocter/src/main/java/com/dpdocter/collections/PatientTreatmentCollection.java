package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Treatment;

@Document(collection = "patient_treatment_cl")
public class PatientTreatmentCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private List<Treatment> treatments;

	@Field
	private ObjectId patientId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId doctorId;

	@Field
	private Discount totalDiscount;

	@Field
	private double totalCost = 0.0;

	@Field
	private double grandTotal = 0.0;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean inHistory = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public Boolean isDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean isInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	@Override
	public String toString() {
		return "PatientTreatmentCollection [id=" + id + ", treatments=" + treatments + ", patientId=" + patientId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
				+ ", totalCost=" + totalCost + ", discarded=" + discarded + ", inHistory=" + inHistory + "]";
	}

}

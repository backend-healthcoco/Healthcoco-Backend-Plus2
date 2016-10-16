package com.dpdocter.beans;

import org.bson.types.ObjectId;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PatientTreatmentStatus;

public class Treatment extends GenericCollection {

	private ObjectId treatmentServiceId;

	private PatientTreatmentStatus status;

	private double cost = 0.0;

	private String note;
	
	private Discount discount;

	private double finalCost=0.0;

	private Quentity quantity;

	
	public ObjectId getTreatmentServiceId() {
		return treatmentServiceId;
	}

	public void setTreatmentServiceId(ObjectId treatmentServiceId) {
		this.treatmentServiceId = treatmentServiceId;
	}

	public PatientTreatmentStatus getStatus() {
		return status;
	}

	public void setStatus(PatientTreatmentStatus status) {
		this.status = status;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Quentity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quentity quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Treatment [treatmentServiceId=" + treatmentServiceId + ", status=" + status + ", cost=" + cost
				+ ", note=" + note + ", quantity=" + quantity + "]";
	}

	public double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

}

package com.dpdocter.request;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Quantity;
import com.dpdocter.enums.PatientTreatmentStatus;

public class TreatmentRequest {

	private String treatmentServiceId;

	private PatientTreatmentStatus status;

	private double cost = 0.0;

	private String note;
	
	private Discount discount;

	private double finalCost=0.0;

	private Quantity quantity;

	public String getTreatmentServiceId() {
		return treatmentServiceId;
	}

	public void setTreatmentServiceId(String treatmentServiceId) {
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

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
	

}

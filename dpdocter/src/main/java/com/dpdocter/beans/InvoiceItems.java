package com.dpdocter.beans;

import org.bson.types.ObjectId;

import com.dpdocter.enums.PatientTreatmentStatus;

public class InvoiceItems {

	private ObjectId doctorId;
	
	private String name;
	
	private String type;// = [SERVICE || PRODUCT] 
	
	private Quantity quantity;
	
	private PatientTreatmentStatus status;
	
	private Double cost = 0.0;

	private Discount discount;
	
	private Tax tax;

	private Double finalCost = 0.0;

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public PatientTreatmentStatus getStatus() {
		return status;
	}

	public void setStatus(PatientTreatmentStatus status) {
		this.status = status;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public Double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(Double finalCost) {
		this.finalCost = finalCost;
	}

	@Override
	public String toString() {
		return "InvoiceItems [doctorId=" + doctorId + ", name=" + name + ", type=" + type + ", quantity=" + quantity
				+ ", status=" + status + ", cost=" + cost + ", discount=" + discount + ", tax=" + tax + ", finalCost="
				+ finalCost + "]";
	}
}

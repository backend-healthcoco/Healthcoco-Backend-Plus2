package com.dpdocter.response;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Quantity;
import com.dpdocter.beans.Tax;
import com.dpdocter.enums.InvoiceItemType;
import com.dpdocter.enums.PatientTreatmentStatus;

public class InvoiceItemResponse {

	private String itemId;
	
	private String doctorId;
	
	private String doctorName;
	
	private String name;
	
	private InvoiceItemType type; 
	
	private Quantity quantity;
	
	private PatientTreatmentStatus status;
	
	private Double cost = 0.0;

	private Discount discount;
	
	private Tax tax;

	private Double finalCost = 0.0;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InvoiceItemType getType() {
		return type;
	}

	public void setType(InvoiceItemType type) {
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
		return "InvoiceItemResponse [itemId=" + itemId + ", doctorId=" + doctorId + ", doctorName=" + doctorName
				+ ", name=" + name + ", type=" + type + ", quantity=" + quantity + ", status=" + status + ", cost="
				+ cost + ", discount=" + discount + ", tax=" + tax + ", finalCost=" + finalCost + "]";
	}	
}

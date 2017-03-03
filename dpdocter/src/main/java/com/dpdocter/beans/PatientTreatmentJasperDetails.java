package com.dpdocter.beans;

public class PatientTreatmentJasperDetails {

	private int no;

	private String treatmentServiceName;

	private String note;
	
	private String quantity;
	
	private String cost;
	
	private String discount;
	
	private String status;

	private String finalCost;

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getTreatmentServiceName() {
		return treatmentServiceName;
	}

	public void setTreatmentServiceName(String treatmentServiceName) {
		this.treatmentServiceName = treatmentServiceName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(String finalCost) {
		this.finalCost = finalCost;
	}

	@Override
	public String toString() {
		return "PatientTreatmentJasperDetails [no=" + no + ", treatmentServiceName=" + treatmentServiceName + ", note="
				+ note + ", quantity=" + quantity + ", cost=" + cost + ", discount=" + discount + ", status=" + status
				+ ", finalCost=" + finalCost + "]";
	}
}

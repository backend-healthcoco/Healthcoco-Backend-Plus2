package com.dpdocter.beans;

public class TreatmentDownloadData {
	
	private String doctorName;

	private String patientName;

	private String patientId;

	private String date;
	
	private String treatmentName;

	private String status;

	private double cost = 0.0;

	private String quantity;

	private String quantityType;

	private String discount;

	private String discountUnit;

	private double finalCost=0.0;

	private String note;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTreatmentName() {
		return treatmentName;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getQuantityType() {
		return quantityType;
	}

	public void setQuantityType(String quantityType) {
		this.quantityType = quantityType;
	}

	public String getDiscountUnit() {
		return discountUnit;
	}

	public void setDiscountUnit(String discountUnit) {
		this.discountUnit = discountUnit;
	}

	@Override
	public String toString() {
		return "TreatmentDownloadData [doctorName=" + doctorName + ", patientName=" + patientName + ", patientId="
				+ patientId + ", date=" + date + ", treatmentName=" + treatmentName + ", status=" + status + ", cost="
				+ cost + ", quantity=" + quantity + ", quantityType=" + quantityType + ", discount=" + discount
				+ ", discountUnit=" + discountUnit + ", finalCost=" + finalCost + ", note=" + note + "]";
	}
}

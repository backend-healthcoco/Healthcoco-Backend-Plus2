package com.dpdocter.beans;

public class InvoiceDownloadData {

	private String doctorName;

	private String patientName;

	private String patientId;

	private String date;

	private String invoiceId;

	private String name;

	private Double cost = 0.0;

	private String quantityValue;
	private String quantityType;

	private String discountValue;
	private String discountUnit;

	private String taxValue;
	private String taxUnit;

	private Double finalCost = 0.0;

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

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
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

	public String getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(String quantityValue) {
		this.quantityValue = quantityValue;
	}

	public String getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(String discountValue) {
		this.discountValue = discountValue;
	}

	public String getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(String taxValue) {
		this.taxValue = taxValue;
	}

	public String getTaxUnit() {
		return taxUnit;
	}

	public void setTaxUnit(String taxUnit) {
		this.taxUnit = taxUnit;
	}

	public Double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(Double finalCost) {
		this.finalCost = finalCost;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "InvoiceDownloadData [doctorName=" + doctorName + ", patientName=" + patientName + ", patientId="
				+ patientId + ", date=" + date + ", invoiceId=" + invoiceId + ", name=" + name + ", cost=" + cost
				+ ", quantity=" + quantityValue + ", quantityType=" + quantityType + ", discount=" + discountValue
				+ ", discountUnit=" + discountUnit + ", tax=" + taxValue + ", taxUnit=" + taxUnit + ", finalCost="
				+ finalCost + ", note=" + note + "]";
	}
}

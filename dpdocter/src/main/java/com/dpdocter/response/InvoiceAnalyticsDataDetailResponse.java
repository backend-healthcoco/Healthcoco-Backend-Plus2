package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.enums.PatientAnalyticType;

public class InvoiceAnalyticsDataDetailResponse {

	private String id;

	private Date date;

	private String uniqueInvoiceId;

	private String localPatientName;

	private String firstName;

	private String doctorName;

	private String mobileNumber;
	private PatientAnalyticType patientAnalyticType;
	private String referedBy;

	private String services;

	private Double cost = 0.0;

	private Double discount = 0.0;

	private Double tax = 0.0;

	private Double invoiceAmount = 0.0;

	private Double balanceAmount = 0.0;

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PatientAnalyticType getPatientAnalyticType() {
		return patientAnalyticType;
	}

	public void setPatientAnalyticType(PatientAnalyticType patientAnalyticType) {
		this.patientAnalyticType = patientAnalyticType;
	}

	public String getReferedBy() {
		return referedBy;
	}

	public void setReferedBy(String referedBy) {
		this.referedBy = referedBy;
	}

	@Override
	public String toString() {
		return "InvoiceAnalyticsDataDetailResponse [id=" + id + ", date=" + date + ", uniqueInvoiceId="
				+ uniqueInvoiceId + ", localPatientName=" + localPatientName + ", firstName=" + firstName
				+ ", doctorName=" + doctorName + ", mobileNumber=" + mobileNumber + ", services=" + services + ", cost="
				+ cost + ", discount=" + discount + ", tax=" + tax + ", invoiceAmount=" + invoiceAmount
				+ ", balanceAmount=" + balanceAmount + "]";
	}

}

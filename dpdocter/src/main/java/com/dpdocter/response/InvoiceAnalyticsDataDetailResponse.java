package com.dpdocter.response;

import java.util.Date;

public class InvoiceAnalyticsDataDetailResponse {

	private Date date;
	
	private String uniqueInvoiceId; 
	
	private String patientName;
	
	private String services;
	
	private Double cost;
	
	private Double discount;
	
	private Double tax;
	
	private Double invoiceAmount;
	
	private Double balanceAmount;

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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

	@Override
	public String toString() {
		return "InvoiceAnalyticsDataDetailResponse [date=" + date + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", patientName=" + patientName + ", services=" + services + ", cost=" + cost + ", discount="
				+ discount + ", tax=" + tax + ", invoiceAmount=" + invoiceAmount + ", balanceAmount=" + balanceAmount
				+ "]";
	}
}

package com.dpdocter.response;

import java.util.Date;
import java.util.List;

public class IncomeAnalyticsDataResponse {

	private String serviceName;

	private String title;

	private String doctorName;

	private String groupName;

	private Date date;

	private Double cost;

	private Double discount;

	private Double tax;

	private Double invoiceAmount;

	List<InvoiceAnalyticsDataDetailResponse> invoices;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public List<InvoiceAnalyticsDataDetailResponse> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<InvoiceAnalyticsDataDetailResponse> invoices) {
		this.invoices = invoices;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "IncomeAnalyticsDataResponse [serviceName=" + serviceName + ", title=" + title + ", doctorName="
				+ doctorName + ", groupName=" + groupName + ", date=" + date + ", cost=" + cost + ", discount="
				+ discount + ", tax=" + tax + ", invoiceAmount=" + invoiceAmount + ", invoices=" + invoices + "]";
	}
}

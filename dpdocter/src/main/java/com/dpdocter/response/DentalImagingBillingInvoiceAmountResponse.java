package com.dpdocter.response;

import java.util.Date;

public class DentalImagingBillingInvoiceAmountResponse {

	private Double totalCost;

	private Date date;

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}

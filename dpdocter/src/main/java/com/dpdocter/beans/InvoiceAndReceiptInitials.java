package com.dpdocter.beans;

public class InvoiceAndReceiptInitials {

	private String locationId;
	
	private String invoiceInitial;

	private String receiptInitial;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getInvoiceInitial() {
		return invoiceInitial;
	}

	public void setInvoiceInitial(String invoiceInitial) {
		this.invoiceInitial = invoiceInitial;
	}

	public String getReceiptInitial() {
		return receiptInitial;
	}

	public void setReceiptInitial(String receiptInitial) {
		this.receiptInitial = receiptInitial;
	}

	@Override
	public String toString() {
		return "InvoiceAndReceiptInitials [locationId=" + locationId + ", invoiceInitial=" + invoiceInitial
				+ ", receiptInitial=" + receiptInitial + "]";
	}
}

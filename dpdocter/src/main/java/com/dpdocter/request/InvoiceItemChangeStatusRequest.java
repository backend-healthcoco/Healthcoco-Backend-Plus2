package com.dpdocter.request;

import com.dpdocter.enums.PatientTreatmentStatus;

public class InvoiceItemChangeStatusRequest {

	private String invoiceId;

	private String itemId;

	private PatientTreatmentStatus status;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public PatientTreatmentStatus getStatus() {
		return status;
	}

	public void setStatus(PatientTreatmentStatus status) {
		this.status = status;

	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public String toString() {
		return "InvoiceItemChangeStatusRequest [invoiceId=" + invoiceId + ", itemId=" + itemId + ", status=" + status
				+ "]";
	}

}

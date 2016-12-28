package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class InvoiceIdWithAmount {

	private ObjectId invoiceId;
	
    private	Double usedAdvanceAmount = 0.0;

	public InvoiceIdWithAmount() {
		super();
	}

	public InvoiceIdWithAmount(ObjectId invoiceId, Double usedAdvanceAmount) {
		super();
		this.invoiceId = invoiceId;
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public ObjectId getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(ObjectId invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	@Override
	public String toString() {
		return "InvoiceIdWithAmount [invoiceId=" + invoiceId + ", usedAdvanceAmount=" + usedAdvanceAmount + "]";
	}	
}

package com.dpdocter.response;

public class InvoiceIdWithAmountResponse {

	private String invoiceId;
	
    private	Double usedAdvanceAmount = 0.0;

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
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
		return "InvoiceIdWithAmountResponse [invoiceId=" + invoiceId + ", usedAdvanceAmount=" + usedAdvanceAmount + "]";
	}
}

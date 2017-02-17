package com.dpdocter.response;

public class InvoiceIdWithAmountResponse {

	private String uniqueInvoiceId;
	
	private String invoiceId;
	
    private	Double usedAdvanceAmount = 0.0;

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

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
		return "InvoiceIdWithAmountResponse [uniqueInvoiceId=" + uniqueInvoiceId + ", invoiceId=" + invoiceId
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + "]";
	}

}

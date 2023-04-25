package com.dpdocter.response;

public class AdvanceReceiptIdWithAmountResponse {

	private String receiptId;

	private Double usedAdvanceAmount = 0.0;

	private String uniqueReceiptId;

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public String getUniqueReceiptId() {
		return uniqueReceiptId;
	}

	public void setUniqueReceiptId(String uniqueReceiptId) {
		this.uniqueReceiptId = uniqueReceiptId;
	}

	@Override
	public String toString() {
		return "AdvanceReceiptIdWithAmountResponse [receiptId=" + receiptId + ", usedAdvanceAmount=" + usedAdvanceAmount
				+ ", uniqueReceiptId=" + uniqueReceiptId + "]";
	}
}

package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class AdvanceReceiptIdWithAmount {

	private ObjectId receiptId;
	
    private	Double usedAdvanceAmount = 0.0;

    private String uniqueReceiptId;
    
	public AdvanceReceiptIdWithAmount() {
		super();
	}

	public AdvanceReceiptIdWithAmount(ObjectId receiptId, Double usedAdvanceAmount) {
		super();
		this.receiptId = receiptId;
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public ObjectId getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(ObjectId receiptId) {
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
		return "AdvanceReceiptIdWithAmount [receiptId=" + receiptId + ", usedAdvanceAmount=" + usedAdvanceAmount
				+ ", uniqueReceiptId=" + uniqueReceiptId + "]";
	}
}

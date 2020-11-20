package com.dpdocter.beans;

public class OnInitAuth {

	private String transactionId;
	
	private String mode;
	
	private OnInitMeta meta;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public OnInitMeta getMeta() {
		return meta;
	}

	public void setMeta(OnInitMeta meta) {
		this.meta = meta;
	}
	
	
}

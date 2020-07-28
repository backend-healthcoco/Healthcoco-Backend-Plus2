package com.dpdocter.enums;

public enum PaymentMode {

	CASH("CASH"),CHEQUE("CHEQUE"),COD("COD"), CREDIT_CARD("CREDIT_CARD"), DEBIT_CARD("DEBIT_CARD"), WALLET("WALLET"),
	NETBANKING("NETBANKING"),ONLINE("ONLINE"),FREE("FREE");

	private String type;

	public String getType() {
		return type;
	}

	private PaymentMode(String type) {
		this.type = type;
	}

}

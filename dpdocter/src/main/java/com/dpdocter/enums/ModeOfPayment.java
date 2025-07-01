package com.dpdocter.enums;

public enum ModeOfPayment {

//	CASH, WALLET, CARD, ONLINE, UPI, CHEQUE,
//
//	NEFT, IMPS, RGTS, DEMAND_DRAFT, DEBIT_CARD, CREDIT_CARD, OTHER;
	CASH("CASH"), WALLET("WALLET"), CARD("CARD"), ONLINE("ONLINE"), UPI("UPI"), CHEQUE("CHEQUE"),

	NEFT("NEFT"), IMPS("IMPS"), RGTS("RGTS"), DEMAND_DRAFT("DEMAND DRAFT"), DEBIT_CARD("DEBIT CARD"),
	CREDIT_CARD("CREDIT CARD"), OTHER("OTHER");

	private String type;

	public String getType() {
		return type;
	}

	private ModeOfPayment(String type) {
		this.type = type;
	}
}

package com.dpdocter.enums;

public enum Currency {
	INR("INR"), USD("USD");

	private String currency;

	Currency(String currency) {
		this.currency = currency;
	}

	public String getCurrency() {
		return currency;
	}

}

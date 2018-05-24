package com.dpdocter.beans;

public class InvoiceTax {

	private String name;

	private Tax tax;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	@Override
	public String toString() {
		return "InvoiceTax [name=" + name + ", tax=" + tax + "]";
	}

}

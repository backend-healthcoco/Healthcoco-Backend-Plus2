package com.dpdocter.beans;

public class DentalWorkCardValue {

	private String name;
	private String value;
	private Integer quantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "DentalWorkCardValue [name=" + name + ", value=" + value + ", quantity=" + quantity + "]";
	}

}

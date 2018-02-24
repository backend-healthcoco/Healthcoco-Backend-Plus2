package com.dpdocter.beans;

import java.util.List;

public class DentalToothNumber {

	private List<String> toothNumber;
	private String type;

	public List<String> getToothNumber() {
		return toothNumber;
	}

	public void setToothNumber(List<String> toothNumber) {
		this.toothNumber = toothNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DentalToothNumber [toothNumber=" + toothNumber + ", type=" + type + "]";
	}

}

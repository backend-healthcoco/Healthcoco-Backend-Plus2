package com.dpdocter.beans;

import com.dpdocter.enums.MedicineQuantityType;

public class OrderQuantity {

private int number;
	
	private MedicineQuantityType type;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public MedicineQuantityType getType() {
		return type;
	}

	public void setType(MedicineQuantityType type) {
		this.type = type;
	}
	
	
}

package com.dpdocter.beans;

import com.dpdocter.enums.QuantityEnum;

public class RecipeAddItem {
	private String id;

	private String name;

	private int value;
	private QuantityEnum type;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public QuantityEnum getType() {
		return type;
	}

	public void setType(QuantityEnum type) {
		this.type = type;
	}

}

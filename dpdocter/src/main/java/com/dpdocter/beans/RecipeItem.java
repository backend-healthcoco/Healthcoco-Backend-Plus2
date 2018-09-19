package com.dpdocter.beans;

import org.bson.types.ObjectId;

import com.dpdocter.enums.QuantityEnum;

public class RecipeItem {
	private ObjectId id;
	private String name;
	private double value;
	private QuantityEnum type;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
}

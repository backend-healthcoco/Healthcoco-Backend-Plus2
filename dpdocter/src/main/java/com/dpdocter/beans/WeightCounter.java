package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.WeightUnit;

public class WeightCounter extends GenericCollection {
	private String id;

	private Double weight = 0.0;

	private WeightUnit unit = WeightUnit.KG;

	private String userId;

	private Boolean discarded = false;

	private Date date;

	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public WeightUnit getUnit() {
		return unit;
	}

	public void setUnit(WeightUnit unit) {
		this.unit = unit;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

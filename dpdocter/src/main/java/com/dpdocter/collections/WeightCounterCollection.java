package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.WeightUnit;

@Document(collection = "weight_counter_cl")
public class WeightCounterCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private Double weight = 0.0;
	@Field
	private WeightUnit unit = WeightUnit.KG;
	@Field
	private ObjectId userId;
	@Field
	private Boolean discarded = false;
	@Field
	private Date date = new Date();

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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}

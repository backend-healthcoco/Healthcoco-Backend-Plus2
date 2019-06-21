package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "water_counter_cl")
public class WaterCounterCollection extends GenericCollection{
	@Id
	private ObjectId id;

	@Field
	private Double noOfLiter = 0.0;

	@Field
	private ObjectId userId;

	@Field
	private Boolean discarded = false;

	@Field
	private Date date;

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

	public Double getNoOfLiter() {
		return noOfLiter;
	}

	public void setNoOfLiter(Double noOfLiter) {
		this.noOfLiter = noOfLiter;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}

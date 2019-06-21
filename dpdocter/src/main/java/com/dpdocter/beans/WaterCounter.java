package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class WaterCounter extends GenericCollection {

	private String id;

	private Double noOfLiter = 0.0;

	private String userId;

	private Boolean discarded = false;

	private Date date=new Date();

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

	public Double getNoOfLiter() {
		return noOfLiter;
	}

	public void setNoOfLiter(Double noOfLiter) {
		this.noOfLiter = noOfLiter;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}

package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class WeightCounterSetting extends GenericCollection {
	private String id;

	private String userId;

	private Boolean showCounter = true;

	private List<String> days;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getShowCounter() {
		return showCounter;
	}

	public void setShowCounter(Boolean showCounter) {
		this.showCounter = showCounter;
	}

	public List<String> getDays() {
		return days;
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

}

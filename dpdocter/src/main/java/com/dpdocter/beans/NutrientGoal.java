package com.dpdocter.beans;

public class NutrientGoal{
	
	private String id;
	
	private String value;

	private Boolean discarded = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "NutrientGoal [id=" + id + ", value=" + value + ", discarded=" + discarded + "]";
	}

}

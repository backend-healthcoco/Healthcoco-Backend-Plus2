package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class NutritionDisease extends GenericCollection {

	private String id;

	private String disease;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "NutritionDisease [id=" + id + ", disease=" + disease + ", discarded=" + discarded + "]";
	}

}

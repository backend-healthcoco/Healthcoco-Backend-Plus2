package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.NutritionPlanType;

public class NutritionPlanWithNameResponse extends GenericCollection {

	private String id;

	private String title;

	private NutritionPlanType type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public NutritionPlanType getType() {
		return type;
	}

	public void setType(NutritionPlanType type) {
		this.type = type;
	}

}

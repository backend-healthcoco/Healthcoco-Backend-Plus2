package com.dpdocter.elasticsearch.response;

import com.dpdocter.enums.NutrientCategaoryEnum;
import com.dpdocter.enums.NutrientType;

public class ESNutrientResponse {

	private String id;

	private Boolean discarded = false;

	private String name;

	private NutrientType type = NutrientType.MICRO_NUTRIENT;

	private NutrientCategaoryEnum category = NutrientCategaoryEnum.CARBOHYDRATE;
	
	private String nutrientCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NutrientType getType() {
		return type;
	}

	public void setType(NutrientType type) {
		this.type = type;
	}

	public NutrientCategaoryEnum getCategory() {
		return category;
	}

	public void setCategory(NutrientCategaoryEnum category) {
		this.category = category;
	}

	public String getNutrientCode() {
		return nutrientCode;
	}

	public void setNutrientCode(String nutrientCode) {
		this.nutrientCode = nutrientCode;
	}

}

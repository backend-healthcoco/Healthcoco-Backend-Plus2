package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.IngredientItem;

public class ESIngredientResponse {
	
	private String id;

	private String name;

	private List<IngredientItem> nutrients;

	private String note;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IngredientItem> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<IngredientItem> nutrients) {
		this.nutrients = nutrients;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	

}

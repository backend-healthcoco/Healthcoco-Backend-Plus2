package com.dpdocter.request;

import java.util.List;

public class RecipeGetRequest {

	private int size;
	private int page;
	private boolean discarded;
	private String searchTerm;
	private int costOfMaking;
	private List<String> ingredients;
	private List<String> nutrients;
	private String cuisine;
	private long updatedTime;

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public int getCostOfMaking() {
		return costOfMaking;
	}

	public void setCostOfMaking(int costOfMaking) {
		this.costOfMaking = costOfMaking;
	}

	public List<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}

	public List<String> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<String> nutrients) {
		this.nutrients = nutrients;
	}

	public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}
	

}

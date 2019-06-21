package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.request.RecipeCounterAddItem;

public class RecentRecipeResponse {
	private Date date;
	private List<RecipeCounterAddItem> recipes;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<RecipeCounterAddItem> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipeCounterAddItem> recipes) {
		this.recipes = recipes;
	}

}

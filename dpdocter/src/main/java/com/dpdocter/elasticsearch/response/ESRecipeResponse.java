package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.beans.IngredientAddItem;
import com.dpdocter.beans.RecipeItem;

public class ESRecipeResponse {

	private String id;

	
	private String name;


	private int amountInGram;

	
	private List<RecipeItem> includeIngredients;


	private List<RecipeItem> excludeIngredients;

	
	private List<RecipeItem> ingredients;

	
	private List<IngredientAddItem> nutrients;


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


	public int getAmountInGram() {
		return amountInGram;
	}


	public void setAmountInGram(int amountInGram) {
		this.amountInGram = amountInGram;
	}


	public List<RecipeItem> getIncludeIngredients() {
		return includeIngredients;
	}


	public void setIncludeIngredients(List<RecipeItem> includeIngredients) {
		this.includeIngredients = includeIngredients;
	}


	public List<RecipeItem> getExcludeIngredients() {
		return excludeIngredients;
	}


	public void setExcludeIngredients(List<RecipeItem> excludeIngredients) {
		this.excludeIngredients = excludeIngredients;
	}


	public List<RecipeItem> getIngredients() {
		return ingredients;
	}


	public void setIngredients(List<RecipeItem> ingredients) {
		this.ingredients = ingredients;
	}


	public List<IngredientAddItem> getNutrients() {
		return nutrients;
	}


	public void setNutrients(List<IngredientAddItem> nutrients) {
		this.nutrients = nutrients;
	}



}

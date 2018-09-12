package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.Recipe;
import com.dpdocter.request.IngredientSearchRequest;
import com.dpdocter.request.RecipeGetRequest;

public interface RecipeService {
	public Nutrient addEditNutrient(Nutrient request);

	public List<Nutrient> getNutrients(int size, int page, boolean discarded, String searchTerm);

	public Nutrient discardNutrient(String id, boolean discarded);

	public Nutrient getNutrient(String id);

	public Ingredient addEditIngredient(Ingredient request);

	public List<Ingredient> getIngredients(IngredientSearchRequest request);

	public Ingredient discardIngredient(String id, boolean discarded);

	public Ingredient getIngredient(String id);

	Recipe addEditRecipe(Recipe request);

	Recipe getRecipe(String id);

	Recipe discardRecipe(String id, boolean discarded);

	List<Recipe> getRecipes(RecipeGetRequest request);

}

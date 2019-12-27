package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeTemplate;
import com.dpdocter.request.RecipeCounterAddItem;
import com.dpdocter.response.RecentRecipeResponse;
import com.dpdocter.response.RecipeCardResponse;

import common.util.web.Response;

public interface RecipeService {
	public Nutrient addEditNutrient(Nutrient request);

	public List<Nutrient> getNutrients(int size, int page, boolean discarded, String searchTerm, String category,
			String doctorId, String locationId, String hospitalId);

	public Nutrient discardNutrient(String id, boolean discarded);

	public Nutrient getNutrient(String id);

	public Ingredient addEditIngredient(Ingredient request);

	public List<Ingredient> getIngredients(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId);

	public Ingredient discardIngredient(String id, boolean discarded);

	public Ingredient getIngredient(String id);

	Recipe addEditRecipe(Recipe request);

	Recipe getRecipe(String id);

	Recipe discardRecipe(String id, boolean discarded);

	List<RecipeCounterAddItem> getFavouriteRecipe(int size, int page, boolean discarded, String searchTerm, String userId);

	Boolean addFavouriteRecipe(String userId, String recipeId);

	List<RecipeCounterAddItem> getFrequentRecipe(int size, int page, boolean discarded, String userId);

	List<RecentRecipeResponse> getRecentRecipe(int size, int page, String userId, boolean discarded, String mealTime);

	List<Recipe> getRecipeList(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId, String planId);

	List<RecipeCardResponse> getRecipeByPlanId(int size, int page, String planId);

	public RecipeTemplate discardRecipeTemplate(String recipeId, Boolean discarded);

	public Response<RecipeTemplate> getRecipeTemplates(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId);

	public RecipeTemplate getRecipeTemplate(String recipeTemplateId);

	public RecipeTemplate addEditRecipeTemplate(RecipeTemplate request);
}

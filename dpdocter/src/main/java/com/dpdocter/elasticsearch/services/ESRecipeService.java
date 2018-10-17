package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.beans.Exercise;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.response.ESIngredientResponse;
import com.dpdocter.elasticsearch.response.ESNutrientResponse;
import com.dpdocter.elasticsearch.response.ESRecipeResponse;
import com.dpdocter.elasticsearch.response.ESRecipeUserAppResponse;

public interface ESRecipeService {

	public boolean addNutrient(ESNutrientDocument request);

	public boolean addIngredient(ESIngredientDocument request);

	boolean addRecipe(ESRecipeDocument request);

	public List<ESIngredientResponse> searchIngredient(int page, int size, Boolean discarded, String searchTerm);

	public List<ESNutrientResponse> searchNutrient(int page, int size, Boolean discarded, String searchTerm);

	public List<ESRecipeResponse> searchRecipe(int page, int size, Boolean discarded, String searchTerm);

	List<Exercise> searchExercise(int page, int size, Boolean discarded, String searchTerm);

	List<ESRecipeUserAppResponse> searchRecipeForUserApp(int page, int size, Boolean discarded, String searchTerm);

}

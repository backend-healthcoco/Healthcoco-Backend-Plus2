package com.dpdocter.elasticsearch.services;

import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;

public interface ESRecipeService {

	public boolean addNutrient(ESNutrientDocument request);

	public boolean addIngredient(ESIngredientDocument request);

	boolean addRecipe(ESRecipeDocument request);

}

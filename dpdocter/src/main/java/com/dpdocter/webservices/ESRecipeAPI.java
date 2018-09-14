package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.response.ESIngredientResponse;
import com.dpdocter.elasticsearch.response.ESNutrientResponse;
import com.dpdocter.elasticsearch.response.ESRecipeResponse;
import com.dpdocter.elasticsearch.services.ESRecipeService;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_RECIPE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_RECIPE_BASE_URL, description = "Endpoint for solr recipe")
public class ESRecipeAPI {

	private static Logger logger = Logger.getLogger(ESRecipeAPI.class.getName());

	@Autowired
	private ESRecipeService esRecipeService;

	@Path(value = PathProxy.SolrRecipeUrls.SEARCH_RECIPES)
	@GET
	@ApiOperation(value = PathProxy.SolrRecipeUrls.SEARCH_RECIPES, notes = PathProxy.SolrRecipeUrls.SEARCH_RECIPES)
	public Response<ESRecipeResponse> searchRecipe(@QueryParam("page") int page, @QueryParam("size") int size,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

		List<ESRecipeResponse> recipeDocuments = esRecipeService.searchRecipe(page, size, discarded, searchTerm);
		Response<ESRecipeResponse> response = new Response<ESRecipeResponse>();
		response.setDataList(recipeDocuments);
		return response;
	}

	@Path(value = PathProxy.SolrRecipeUrls.SEARCH_INGREDIENTS)
	@GET
	@ApiOperation(value = PathProxy.SolrRecipeUrls.SEARCH_INGREDIENTS, notes = PathProxy.SolrRecipeUrls.SEARCH_INGREDIENTS)
	public Response<ESIngredientResponse> searchIngredient(@QueryParam("page") int page, @QueryParam("size") int size,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

		List<ESIngredientResponse> ingredientDocument = esRecipeService.searchIngredient(page, size, discarded,
				searchTerm);
		Response<ESIngredientResponse> response = new Response<ESIngredientResponse>();
		response.setDataList(ingredientDocument);
		return response;
	}

	@Path(value = PathProxy.SolrRecipeUrls.SEARCH_NUTRIENTS)
	@GET
	@ApiOperation(value = PathProxy.SolrRecipeUrls.SEARCH_NUTRIENTS, notes = PathProxy.SolrRecipeUrls.SEARCH_NUTRIENTS)
	public Response<ESNutrientResponse> searchNutrient(@QueryParam("page") int page, @QueryParam("size") int size,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {

		List<ESNutrientResponse> nutrientDocument = esRecipeService.searchNutrient(page, size, discarded, searchTerm);
		Response<ESNutrientResponse> response = new Response<ESNutrientResponse>();
		response.setDataList(nutrientDocument);
		return response;
	}

}

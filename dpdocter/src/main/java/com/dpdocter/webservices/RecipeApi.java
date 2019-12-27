package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeTemplate;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.services.ESRecipeService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.RecentRecipeResponse;
import com.dpdocter.response.RecipeCardResponse;
import com.dpdocter.services.RecipeService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.RECIPE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RECIPE_BASE_URL, description = "Endpoint for recipe")
public class RecipeApi {
	private static Logger logger = Logger.getLogger(RecipeApi.class.getName());

	@Autowired
	private RecipeService recipeService;

	@Autowired
	private ESRecipeService esRecipeService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Path(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT)
	@POST
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT, notes = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT)
	public Response<Nutrient> addEditNutrient(Nutrient request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"name,doctor,location or hospital Id should not be null or empty");

		}

		Nutrient nutrient = recipeService.addEditNutrient(request);
		if (nutrient != null) {
			ESNutrientDocument document = new ESNutrientDocument();
			BeanUtil.map(nutrient, document);
			esRecipeService.addNutrient(document);
		}
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(nutrient);
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT)
	@POST
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT, notes = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT)
	public Response<Ingredient> addEditIngredient(Ingredient request) {

		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"name,doctor,location or hospital Id should not be null or empty");

		}
		Ingredient ingredient = recipeService.addEditIngredient(request);
		if (ingredient != null) {
			ESIngredientDocument document = new ESIngredientDocument();
			BeanUtil.map(ingredient, document);
			esRecipeService.addIngredient(document);
		}

		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(ingredient);
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_INGREDIENT)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_INGREDIENT, notes = PathProxy.RecipeUrls.GET_INGREDIENT)
	public Response<Ingredient> getIngredient(@PathParam("ingredientId") String ingredientId) {

		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Ingredient ingredient = recipeService.getIngredient(ingredientId);
		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(ingredient);
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_NUTRIENT)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENT, notes = PathProxy.RecipeUrls.GET_NUTRIENT)
	public Response<Nutrient> getNutrient(@PathParam("nutrientId") String nutrientId) {

		if (DPDoctorUtils.anyStringEmpty(nutrientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Nutrient nutrient = recipeService.getNutrient(nutrientId);
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(nutrient);
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_NUTRIENTS)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENTS, notes = PathProxy.RecipeUrls.GET_NUTRIENTS)
	public Response<Nutrient> getNutrients(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") Boolean discarded,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("category") String category) {

		Response<Nutrient> response = new Response<Nutrient>();
		response.setDataList(recipeService.getNutrients(size, page, discarded, searchTerm, category, doctorId,
				locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_INGREDIENTS)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_INGREDIENTS, notes = PathProxy.RecipeUrls.GET_INGREDIENTS)
	public Response<Ingredient> getIngredients(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") Boolean discarded,
			@QueryParam("searchTerm") String searchTerm) {

		Response<Ingredient> response = new Response<Ingredient>();
		response.setDataList(
				recipeService.getIngredients(size, page, discarded, searchTerm, doctorId, locationId, hospitalId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.DELETE_INGREDIENT)
	@DELETE
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_INGREDIENT, notes = PathProxy.RecipeUrls.DELETE_INGREDIENT)
	public Response<Ingredient> deleteIngredient(@PathParam("ingredientId") String ingredientId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(recipeService.discardIngredient(ingredientId, discarded));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.DELETE_NUTRIENT)
	@DELETE
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_NUTRIENT, notes = PathProxy.RecipeUrls.DELETE_NUTRIENT)
	public Response<Nutrient> deleteNutrient(@PathParam("nutrientId") String nutrientId,
			@PathParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(nutrientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(recipeService.discardNutrient(nutrientId, discarded));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.DELETE_RECIPE)
	@DELETE
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_RECIPE, notes = PathProxy.RecipeUrls.DELETE_RECIPE)
	public Response<Recipe> deleteRecipe(@PathParam("recipeId") String recipeId, @PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Recipe> response = new Response<Recipe>();
		response.setData(recipeService.discardRecipe(recipeId, discarded));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_RECIPES)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES, notes = PathProxy.RecipeUrls.GET_RECIPES)
	public Response<Recipe> getRecipes(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@QueryParam("searchTerm") String searchTerm,@QueryParam("planId") String planId) {

		Response<Recipe> response = new Response<Recipe>();
		response.setDataList(
				recipeService.getRecipeList(size, page, discarded, searchTerm, doctorId, locationId, hospitalId, planId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_RECIPE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE, notes = PathProxy.RecipeUrls.GET_RECIPE)
	public Response<Recipe> getRecipe(@PathParam("recipeId") String recipeId) {
		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Recipe> response = new Response<Recipe>();
		response.setData(recipeService.getRecipe(recipeId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE)
	@POST
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE, notes = PathProxy.RecipeUrls.ADD_EDIT_RECIPE)
	public Response<Recipe> addEditRecipes(Recipe request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"name,doctorId,locationId or hospitalId should not be null or empty");

		}
		Recipe recipe = recipeService.addEditRecipe(request);
		Response<Recipe> response = new Response<Recipe>();

		if (recipe != null) {
			transnationalService.addResource(new ObjectId(request.getId()), Resource.RECIPE, true);

			ESRecipeDocument document = new ESRecipeDocument();
			BeanUtil.map(recipe, document);
			esRecipeService.addRecipe(document);
		}
		if (recipe != null) {
			if (recipe.getRecipeImages() != null && !recipe.getRecipeImages().isEmpty())
				for (int index = 0; index <= recipe.getRecipeImages().size(); index++) {
					recipe.getRecipeImages().add(index, getFinalImageURL(recipe.getRecipeImages().get(index)));
				}
			if (!DPDoctorUtils.anyStringEmpty(recipe.getVideoUrl())) {
				recipe.setVideoUrl(getFinalImageURL(recipe.getVideoUrl()));
			}
		}
		response.setData(recipe);

		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Path(value = PathProxy.RecipeUrls.ADD_FAVOURITE_RECIPE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_FAVOURITE_RECIPE, notes = PathProxy.RecipeUrls.ADD_FAVOURITE_RECIPE)
	public Response<Boolean> getRecipes(@QueryParam("userId") String userId, @QueryParam("recipeId") String recipeId) {
		Response<Boolean> response = new Response<Boolean>();
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}

		response.setData(recipeService.addFavouriteRecipe(userId, recipeId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_RECENT_RECIPE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECENT_RECIPE, notes = PathProxy.RecipeUrls.GET_RECENT_RECIPE)
	public Response<RecentRecipeResponse> getRecentRecipes(@PathParam("userId") String userId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@QueryParam("mealTime") String mealTime) {

		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}

		Response<RecentRecipeResponse> response = new Response<RecentRecipeResponse>();
		response.setDataList(recipeService.getRecentRecipe(size, page, userId, discarded, mealTime));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_FREQUENT_RECIPE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FREQUENT_RECIPE, notes = PathProxy.RecipeUrls.GET_FREQUENT_RECIPE)
	public Response<RecentRecipeResponse> getFrequenttRecipes(@PathParam("userId") String userId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}
		Response<RecentRecipeResponse> response = new Response<RecentRecipeResponse>();
		response.setDataList(recipeService.getFrequentRecipe(size, page, discarded, userId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_FAVOURITE_RECIPE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FAVOURITE_RECIPE, notes = PathProxy.RecipeUrls.GET_FAVOURITE_RECIPE)
	public Response<RecentRecipeResponse> getFavouritetRecipes(@PathParam("userId") String userId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}
		Response<RecentRecipeResponse> response = new Response<RecentRecipeResponse>();
		response.setDataList(recipeService.getFrequentRecipe(size, page, discarded, userId));
		return response;
	}
	
	@Path(value = PathProxy.RecipeUrls.GET_RECIPES_BY_PLAN_ID)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES_BY_PLAN_ID, notes = PathProxy.RecipeUrls.GET_RECIPES_BY_PLAN_ID)
	public Response<RecipeCardResponse> getRecipesByPlanId(@QueryParam("planId") String planId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}
		Response<RecipeCardResponse> response = new Response<RecipeCardResponse>();
		response.setDataList(recipeService.getRecipeByPlanId(size, page, planId));
		return response;
	}
	
	@Path(value = PathProxy.RecipeUrls.DELETE_RECIPE_TEMPLATE)
	@DELETE
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_RECIPE_TEMPLATE, notes = PathProxy.RecipeUrls.DELETE_RECIPE_TEMPLATE)
	public Response<RecipeTemplate> deleteRecipeTemplate(@PathParam("recipeId") String recipeId, @PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("discarded") @DefaultValue("true") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<RecipeTemplate> response = new Response<RecipeTemplate>();
		response.setData(recipeService.discardRecipeTemplate(recipeId, discarded));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_RECIPES_TEMPLATE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES_TEMPLATE, notes = PathProxy.RecipeUrls.GET_RECIPES_TEMPLATE)
	public Response<RecipeTemplate> getRecipeTemplates(@PathParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("size") int size, @QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@QueryParam("searchTerm") String searchTerm) {

		Response<RecipeTemplate> response = recipeService.getRecipeTemplates(size, page, discarded, searchTerm, doctorId, locationId, hospitalId);
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.GET_RECIPE_TEMPLATE)
	@GET
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE_TEMPLATE, notes = PathProxy.RecipeUrls.GET_RECIPE_TEMPLATE)
	public Response<RecipeTemplate> getRecipeTemplate(@PathParam("recipeTemplateId") String recipeTemplateId) {
		if (DPDoctorUtils.anyStringEmpty(recipeTemplateId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<RecipeTemplate> response = new Response<RecipeTemplate>();
		response.setData(recipeService.getRecipeTemplate(recipeTemplateId));
		return response;
	}

	@Path(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_TEMPLATE)
	@POST
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_TEMPLATE, notes = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_TEMPLATE)
	public Response<RecipeTemplate> addEditRecipeTemplate(RecipeTemplate request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}

		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
				request.getName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput,
					"name,doctorId,locationId or hospitalId should not be null or empty");

		}
		RecipeTemplate recipe = recipeService.addEditRecipeTemplate(request);
		Response<RecipeTemplate> response = new Response<RecipeTemplate>();

		response.setData(recipe);

		return response;
	}
}

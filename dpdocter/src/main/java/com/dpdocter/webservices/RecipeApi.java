package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.FoodCommunity;
import com.dpdocter.beans.FoodGroup;
import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.NutrientGoal;
import com.dpdocter.beans.NutritionDisease;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeNutrientType;
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
(PathProxy.RECIPE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RECIPE_BASE_URL, description = "Endpoint for recipe")
public class RecipeApi {
	private static Logger logger = LogManager.getLogger(RecipeApi.class.getName());

	@Autowired
	private RecipeService recipeService;

	@Autowired
	private ESRecipeService esRecipeService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private TransactionalManagementService transnationalService;

	
	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_NUTRIENT)
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

	
	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_INGREDIENT)
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

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_INGREDIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_INGREDIENT, notes = PathProxy.RecipeUrls.GET_INGREDIENT)
	public Response<Ingredient> getIngredient(@PathVariable("ingredientId") String ingredientId) {

		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Ingredient ingredient = recipeService.getIngredient(ingredientId);
		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(ingredient);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENT, notes = PathProxy.RecipeUrls.GET_NUTRIENT)
	public Response<Nutrient> getNutrient(@PathVariable("nutrientId") String nutrientId) {

		if (DPDoctorUtils.anyStringEmpty(nutrientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Nutrient nutrient = recipeService.getNutrient(nutrientId);
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(nutrient);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENTS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENTS, notes = PathProxy.RecipeUrls.GET_NUTRIENTS)
	public Response<Nutrient> getNutrients(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("category") String category) {

		Response<Nutrient> response = new Response<Nutrient>();
		response.setDataList(recipeService.getNutrients(size, page, discarded, searchTerm, category, doctorId,
				locationId, hospitalId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_INGREDIENTS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_INGREDIENTS, notes = PathProxy.RecipeUrls.GET_INGREDIENTS)
	public Response<Ingredient> getIngredients(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("searchTerm") String searchTerm) {

		Response<Ingredient> response = new Response<Ingredient>();
		response.setDataList(
				recipeService.getIngredients(size, page, discarded, searchTerm, doctorId, locationId, hospitalId));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_INGREDIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_INGREDIENT, notes = PathProxy.RecipeUrls.DELETE_INGREDIENT)
	public Response<Ingredient> deleteIngredient(@PathVariable("ingredientId") String ingredientId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(ingredientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Ingredient> response = new Response<Ingredient>();
		response.setData(recipeService.discardIngredient(ingredientId, discarded));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_NUTRIENT)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_NUTRIENT, notes = PathProxy.RecipeUrls.DELETE_NUTRIENT)
	public Response<Nutrient> deleteNutrient(@PathVariable("nutrientId") String nutrientId,
			@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@PathVariable("hospitalId") String hospitalId,
			@RequestParam("discarded")   Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(nutrientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Nutrient> response = new Response<Nutrient>();
		response.setData(recipeService.discardNutrient(nutrientId, discarded));
		return response;
	}

	
	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_RECIPE, notes = PathProxy.RecipeUrls.DELETE_RECIPE)
	public Response<Recipe> deleteRecipe(@PathVariable("recipeId") String recipeId, @PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("discarded")   Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Recipe> response = new Response<Recipe>();
		response.setData(recipeService.discardRecipe(recipeId, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES, notes = PathProxy.RecipeUrls.GET_RECIPES)
	public Response<Recipe> getRecipes(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("planId") String planId) {

		Response<Recipe> response = new Response<Recipe>();
		response.setDataList(
				recipeService.getRecipeList(size, page, discarded, searchTerm, doctorId, locationId, hospitalId, planId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE, notes = PathProxy.RecipeUrls.GET_RECIPE)
	public Response<Recipe> getRecipe(@PathVariable("recipeId") String recipeId) {
		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<Recipe> response = new Response<Recipe>();
		response.setData(recipeService.getRecipe(recipeId));
		return response;
	}

	
	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE)
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

	
	@GetMapping(value = PathProxy.RecipeUrls.ADD_FAVOURITE_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.ADD_FAVOURITE_RECIPE, notes = PathProxy.RecipeUrls.ADD_FAVOURITE_RECIPE)
	public Response<Boolean> getRecipes(@RequestParam("userId") String userId, @RequestParam("recipeId") String recipeId) {
		Response<Boolean> response = new Response<Boolean>();
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}

		response.setData(recipeService.addFavouriteRecipe(userId, recipeId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECENT_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECENT_RECIPE, notes = PathProxy.RecipeUrls.GET_RECENT_RECIPE)
	public Response<RecentRecipeResponse> getRecentRecipes(@PathVariable("userId") String userId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("mealTime") String mealTime) {

		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}

		Response<RecentRecipeResponse> response = new Response<RecentRecipeResponse>();
		response.setDataList(recipeService.getRecentRecipe(size, page, userId, discarded, mealTime));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_FREQUENT_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FREQUENT_RECIPE, notes = PathProxy.RecipeUrls.GET_FREQUENT_RECIPE)
	public Response<RecentRecipeResponse> getFrequenttRecipes(@PathVariable("userId") String userId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}
		Response<RecentRecipeResponse> response = new Response<RecentRecipeResponse>();
		response.setDataList(recipeService.getFrequentRecipe(size, page, discarded, userId));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_FAVOURITE_RECIPE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FAVOURITE_RECIPE, notes = PathProxy.RecipeUrls.GET_FAVOURITE_RECIPE)
	public Response<RecentRecipeResponse> getFavouritetRecipes(@PathVariable("userId") String userId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}
		Response<RecentRecipeResponse> response = new Response<RecentRecipeResponse>();
		response.setDataList(recipeService.getFrequentRecipe(size, page, discarded, userId));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPES_BY_PLAN_ID)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES_BY_PLAN_ID, notes = PathProxy.RecipeUrls.GET_RECIPES_BY_PLAN_ID)
	public Response<RecipeCardResponse> getRecipesByPlanId(@RequestParam("planId") String planId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(planId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "user Id should not be null or empty");

		}
		Response<RecipeCardResponse> response = new Response<RecipeCardResponse>();
		response.setDataList(recipeService.getRecipeByPlanId(size, page, planId));
		return response;
	}
	
	
	@DeleteMapping(value = PathProxy.RecipeUrls.DELETE_RECIPE_TEMPLATE)
	@ApiOperation(value = PathProxy.RecipeUrls.DELETE_RECIPE_TEMPLATE, notes = PathProxy.RecipeUrls.DELETE_RECIPE_TEMPLATE)
	public Response<RecipeTemplate> deleteRecipeTemplate(@PathVariable("recipeId") String recipeId, @PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("discarded")   Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(recipeId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<RecipeTemplate> response = new Response<RecipeTemplate>();
		response.setData(recipeService.discardRecipeTemplate(recipeId, discarded));
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPES_TEMPLATE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPES_TEMPLATE, notes = PathProxy.RecipeUrls.GET_RECIPES_TEMPLATE)
	public Response<RecipeTemplate> getRecipeTemplates(@PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@RequestParam("searchTerm") String searchTerm) {

		Response<RecipeTemplate> response = recipeService.getRecipeTemplates(size, page, discarded, searchTerm, doctorId, locationId, hospitalId);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE_TEMPLATE)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE_TEMPLATE, notes = PathProxy.RecipeUrls.GET_RECIPE_TEMPLATE)
	public Response<RecipeTemplate> getRecipeTemplate(@PathVariable("recipeTemplateId") String recipeTemplateId) {
		if (DPDoctorUtils.anyStringEmpty(recipeTemplateId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<RecipeTemplate> response = new Response<RecipeTemplate>();
		response.setData(recipeService.getRecipeTemplate(recipeTemplateId));
		return response;
	}

	
	@PostMapping(value = PathProxy.RecipeUrls.ADD_EDIT_RECIPE_TEMPLATE)
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
	
	
	@GetMapping(value = PathProxy.RecipeUrls.GET_FOOD_COMMUNITIES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FOOD_COMMUNITIES, notes = PathProxy.RecipeUrls.GET_FOOD_COMMUNITIES)
	public Response<FoodCommunity> getFoodCommunities(@RequestParam(value ="size") int size, 
			@RequestParam(value ="page") int page,
			@RequestParam(value ="discarded") Boolean discarded, 
			@RequestParam(value ="searchTerm") String searchTerm) {
		Integer count = recipeService.countFoodCommunities(discarded, searchTerm);
		Response<FoodCommunity> response = new Response<FoodCommunity>();
		if (count > 0)
			response.setDataList(recipeService.getFoodCommunities(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.RecipeUrls.GET_FOOD_GROUPS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_FOOD_GROUPS, notes = PathProxy.RecipeUrls.GET_FOOD_GROUPS)
	public Response<FoodGroup> getFoodGroups(@RequestParam(value ="size") int size, 
			@RequestParam(value ="page") int page,
			@RequestParam(value ="discarded") Boolean discarded, 
			@RequestParam(value ="searchTerm") String searchTerm) {
		Integer count = recipeService.countFoodGroups(discarded, searchTerm);
		Response<FoodGroup> response = new Response<FoodGroup>();
		if (count > 0)
			response.setDataList(recipeService.getFoodGroups(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRIENT_GOALS)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRIENT_GOALS, notes = PathProxy.RecipeUrls.GET_NUTRIENT_GOALS)
	public Response<NutrientGoal> getNutrientGoals(@RequestParam(value ="size") int size, 
			@RequestParam(value ="page") int page,
			@RequestParam(value ="discarded") Boolean discarded, 
			@RequestParam(value ="searchTerm") String searchTerm) {
		Integer count = recipeService.countNutrientGoals(discarded, searchTerm);
		Response<NutrientGoal> response = new Response<NutrientGoal>();
		if (count > 0)
			response.setDataList(recipeService.getNutrientGoals(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}

	
	@GetMapping(value = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPES, notes = PathProxy.RecipeUrls.GET_RECIPE_NUTRIENT_TYPES)
	public Response<RecipeNutrientType> getRecipeNutrientTypes(@RequestParam(value ="size") int size, 
			@RequestParam(value ="page") int page,
			@RequestParam(value ="discarded") Boolean discarded, 
			@RequestParam(value ="searchTerm") String searchTerm) {
		Integer count = recipeService.countRecipeNutrientTypes(discarded, searchTerm);
		Response<RecipeNutrientType> response = new Response<RecipeNutrientType>();
		if (count > 0)
			response.setDataList(recipeService.getRecipeNutrientTypes(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.RecipeUrls.GET_NUTRITION_DISEASES)
	@ApiOperation(value = PathProxy.RecipeUrls.GET_NUTRITION_DISEASES, notes = PathProxy.RecipeUrls.GET_NUTRITION_DISEASES)
	public Response<NutritionDisease> getNutritionDisease(@RequestParam(value ="size") int size, 
			@RequestParam(value ="page") int page,
			@RequestParam(value ="discarded") Boolean discarded, 
			@RequestParam(value ="searchTerm") String searchTerm) {
		Integer count = recipeService.countDisease(discarded, searchTerm);
		Response<NutritionDisease> response = new Response<NutritionDisease>();
		if (count > 0)
			response.setDataList(recipeService.getDiseases(size, page, discarded, searchTerm));
		response.setCount(count);
		return response;
	}
}

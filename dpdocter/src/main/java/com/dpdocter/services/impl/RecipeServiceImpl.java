package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.IngredientAddItem;
import com.dpdocter.beans.IngredientItem;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeAddItem;
import com.dpdocter.beans.RecipeItem;
import com.dpdocter.collections.IngredientCollection;
import com.dpdocter.collections.NutrientCollection;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.IngredientRepository;
import com.dpdocter.repository.NutrientRepository;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.request.IngredientSearchRequest;
import com.dpdocter.request.RecipeGetRequest;
import com.dpdocter.services.RecipeService;

import common.util.web.DPDoctorUtils;

@Service
public class RecipeServiceImpl implements RecipeService {

	private static Logger logger = Logger.getLogger(RecipeServiceImpl.class.getName());
	@Autowired
	private NutrientRepository nutrientRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private RecipeRepository recipeRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public Nutrient addEditNutrient(Nutrient request) {
		Nutrient response = null;
		try {
			NutrientCollection nutrientCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutrientCollection = nutrientRepository.findOne(new ObjectId(request.getId()));
				if (nutrientCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutrient Not found with Id");
				}
				nutrientCollection.setUpdatedTime(new Date());
				nutrientCollection.setName(request.getName());
			} else {
				nutrientCollection = new NutrientCollection();
				BeanUtil.map(request, nutrientCollection);
				nutrientCollection.setCreatedBy("ADMIN");
				nutrientCollection.setUpdatedTime(new Date());
				nutrientCollection.setCreatedTime(new Date());
			}
			nutrientCollection = nutrientRepository.save(nutrientCollection);
			response = new Nutrient();
			BeanUtil.map(nutrientCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit nutrient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit nutrient " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Nutrient> getNutrients(int size, int page, boolean discarded, String searchTerm) {
		List<Nutrient> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip(page * size),
						Aggregation.limit(size), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, NutrientCollection.class, Nutrient.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting nutrients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Nutrient discardNutrient(String id, boolean discarded) {
		Nutrient response = null;
		try {
			NutrientCollection nutrientCollection = nutrientRepository.findOne(new ObjectId(id));
			if (nutrientCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Nutrient Not found with Id");
			}
			nutrientCollection.setDiscarded(discarded);
			nutrientCollection.setUpdatedTime(new Date());
			nutrientCollection = nutrientRepository.save(nutrientCollection);
			response = new Nutrient();
			BeanUtil.map(nutrientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete nutrient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard nutrient " + e.getMessage());

		}
		return response;

	}

	@Override
	public Nutrient getNutrient(String id) {
		Nutrient response = null;
		try {
			NutrientCollection nutrientCollection = nutrientRepository.findOne(new ObjectId(id));
			response = new Nutrient();
			BeanUtil.map(nutrientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while getting nutrient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrient " + e.getMessage());

		}
		return response;

	}

	@Override
	public Recipe addEditRecipe(Recipe request) {
		Recipe response = null;
		List<RecipeItem> recipeItems = null;

		try {

			if (request != null) {
				if (!request.getRecipeImages().isEmpty() && request.getRecipeImages() != null)
					for (int index = 0; index < request.getRecipeImages().size(); index++) {
						request.getRecipeImages().add(index,
								request.getRecipeImages().get(index).replace(imagePath, ""));
					}
				if (!DPDoctorUtils.anyStringEmpty(request.getVideoUrl())) {
					request.setVideoUrl(request.getVideoUrl().replace(imagePath, ""));
				}
			}
			RecipeCollection recipeCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				recipeCollection = recipeRepository.findOne(new ObjectId(request.getId()));
				if (recipeCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Recipe Not found with Id");
				}

				recipeCollection.setUpdatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(request.getName())) {
					recipeCollection.setName(request.getName());
				}

				if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
					recipeItems = new ArrayList<RecipeItem>();
					for (RecipeAddItem item : request.getIngredients()) {
						RecipeItem recipeitem = new RecipeItem();
						BeanUtil.map(item, recipeitem);
						recipeItems.add(recipeitem);
					}
					recipeCollection.setIngredients(recipeItems);

				}

				if (request.getExcludeIngredients() != null && !request.getExcludeIngredients().isEmpty()) {
					recipeItems = new ArrayList<RecipeItem>();
					for (RecipeAddItem item : request.getIngredients()) {
						RecipeItem recipeitem = new RecipeItem();
						BeanUtil.map(item, recipeitem);
						recipeItems.add(recipeitem);
					}
					recipeCollection.setExcludeIngredients(recipeItems);
				}

				if (request.getIncludeIngredients() != null && !request.getIncludeIngredients().isEmpty()) {
					recipeItems = new ArrayList<RecipeItem>();
					for (RecipeAddItem item : request.getIngredients()) {
						RecipeItem recipeitem = new RecipeItem();
						BeanUtil.map(item, recipeitem);
						recipeItems.add(recipeitem);
					}
					recipeCollection.setIncludeIngredients(recipeItems);
				}

				if (request.getNutrients() != null && !request.getNutrients().isEmpty()) {
					List<IngredientItem> ingredientItems = new ArrayList<IngredientItem>();
					for (IngredientAddItem item : request.getNutrients()) {
						IngredientItem ingredientItem = new IngredientItem();
						BeanUtil.map(item, ingredientItem);
						ingredientItems.add(ingredientItem);
					}
					recipeCollection.setNutrients(ingredientItems);
				}

				if (request.getRecipeImages() != null && !request.getRecipeImages().isEmpty()) {

					recipeCollection.setRecipeImages(new ArrayList<String>());
					recipeCollection.setRecipeImages(request.getRecipeImages());
				}
				if (request.getRecipeImages() != null && !request.getRecipeImages().isEmpty()) {

					recipeCollection.setRecipeImages(new ArrayList<String>());
					recipeCollection.setRecipeImages(request.getRecipeImages());
				}

			} else {
				recipeCollection = recipeRepository.save(recipeCollection);
				recipeCollection = new RecipeCollection();
				BeanUtil.map(request, recipeCollection);
				recipeCollection.setCreatedBy("ADMIN");
				recipeCollection.setUpdatedTime(new Date());
				recipeCollection.setCreatedTime(new Date());
			}
			response = new Recipe();
			BeanUtil.map(recipeCollection, response);
			if (response != null) {
				if (!response.getRecipeImages().isEmpty() && response.getRecipeImages() != null)
					for (int index = 0; index < response.getRecipeImages().size(); index++) {
						response.getRecipeImages().add(index, getFinalImageURL(response.getRecipeImages().get(index)));
					}
				if (!DPDoctorUtils.anyStringEmpty(response.getVideoUrl())) {
					response.setVideoUrl(getFinalImageURL(response.getVideoUrl()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while addedit Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Recipe> getRecipes(RecipeGetRequest request) {
		List<Recipe> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(request.getDiscarded());
			if (!DPDoctorUtils.anyStringEmpty(request.getSearchTerm())) {
				criteria = criteria.orOperator(new Criteria("name").regex(request.getSearchTerm(), "i"),
						new Criteria("name").regex(request.getSearchTerm()));
			}

			if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
				criteria = criteria.and("ingredients").elemMatch(new Criteria("name").in(request.getIngredients()));
			}
			if (request.getNutrients() != null && !request.getNutrients().isEmpty()) {
				criteria = criteria.and("nutrients").elemMatch(new Criteria("name").in(request.getNutrients()));
			}
			/*
			 * CustomAggregationOperation aggregationOperation = new
			 * CustomAggregationOperation(new BasicDBObject("$group", new
			 * BasicDBObject("_id", "$_id").append("videoUrl", new BasicDBObject("$first",
			 * "$videoUrl")) .append("recipeImages", new BasicDBObject("$first",
			 * "$recipeImages")) .append("includeIngredients", new BasicDBObject("$first",
			 * "$includeIngredients")) .append("excludeIngredients", new
			 * BasicDBObject("$first", "$excludeIngredients")) .append("dishType", new
			 * BasicDBObject("$first", "$dishType")) .append("technique", new
			 * BasicDBObject("$first", "$technique")) .append("isPopular", new
			 * BasicDBObject("$first", "$isPopular")) .append("isHoliday", new
			 * BasicDBObject("$first", "$isHoliday")) .append("discarded", new
			 * BasicDBObject("$first", "$discarded")) .append("direction", new
			 * BasicDBObject("$first", "$direction")) .append("dietaryConcerns", new
			 * BasicDBObject("$first", "$dietaryConcerns")) .append("forMember", new
			 * BasicDBObject("$first", "$forMember")) .append("cost", new
			 * BasicDBObject("$first", "$cost")) .append("meal", new BasicDBObject("$first",
			 * "$meal")) .append("cuisine", new BasicDBObject("$first", "$cuisine"))
			 * .append("course", new BasicDBObject("$first", "$course"))
			 * .append("preparationTime", new BasicDBObject("$first", "$preparationTime"))
			 * .append("createdTime", new BasicDBObject("$first", "$createdTime"))
			 * .append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
			 * .append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			 */
			Aggregation aggregation = null;
			if (request.getSize() > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.unwind("ingredients"),
						Aggregation.unwind("nutrients"), Aggregation.match(criteria),
						Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.unwind("ingredients"),
						Aggregation.unwind("nutrients"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, RecipeCollection.class, Recipe.class).getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (Recipe recipe : response) {
					if (!recipe.getRecipeImages().isEmpty() && recipe.getRecipeImages() != null)
						for (int index = 0; index < recipe.getRecipeImages().size(); index++) {
							recipe.getRecipeImages().add(index, getFinalImageURL(recipe.getRecipeImages().get(index)));
						}
					if (!DPDoctorUtils.anyStringEmpty(recipe.getVideoUrl())) {
						recipe.setVideoUrl(getFinalImageURL(recipe.getVideoUrl()));
					}
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public Recipe discardRecipe(String id, boolean discarded) {
		Recipe response = null;
		try {
			RecipeCollection recipeCollection = recipeRepository.findOne(new ObjectId(id));
			if (recipeCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "recipe Not found with Id");
			}
			recipeCollection.setDiscarded(discarded);
			recipeCollection.setUpdatedTime(new Date());
			recipeCollection = recipeRepository.save(recipeCollection);
			response = new Recipe();
			BeanUtil.map(recipeCollection, response);
			if (response != null) {
				if (!response.getRecipeImages().isEmpty() && response.getRecipeImages() != null)
					for (int index = 0; index < response.getRecipeImages().size(); index++) {
						response.getRecipeImages().add(index, getFinalImageURL(response.getRecipeImages().get(index)));
					}
				if (!DPDoctorUtils.anyStringEmpty(response.getVideoUrl())) {
					response.setVideoUrl(getFinalImageURL(response.getVideoUrl()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while delete Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard Recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public Recipe getRecipe(String id) {
		Recipe response = null;
		try {
			RecipeCollection recipeCollection = recipeRepository.findOne(new ObjectId(id));
			response = new Recipe();
			BeanUtil.map(recipeCollection, response);
			if (response != null) {
				if (!response.getRecipeImages().isEmpty() && response.getRecipeImages() != null)
					for (int index = 0; index < response.getRecipeImages().size(); index++) {
						response.getRecipeImages().add(index, getFinalImageURL(response.getRecipeImages().get(index)));
					}
				if (!DPDoctorUtils.anyStringEmpty(response.getVideoUrl())) {
					response.setVideoUrl(getFinalImageURL(response.getVideoUrl()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public Ingredient addEditIngredient(Ingredient request) {
		Ingredient response = null;
		try {
			IngredientCollection ingredientCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				ingredientCollection = ingredientRepository.findOne(new ObjectId(request.getId()));
				if (ingredientCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "ingredient Not found with Id");
				}

				ingredientCollection.setUpdatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(request.getName())) {
					ingredientCollection.setName(request.getName());
				}
				if (!DPDoctorUtils.anyStringEmpty(request.getNote())) {
					ingredientCollection.setNote(request.getNote());
				}
				if (request.getNutrients() != null && !request.getNutrients().isEmpty()) {
					List<IngredientItem> ingredientItems = new ArrayList<IngredientItem>();
					for (IngredientAddItem item : request.getNutrients()) {
						IngredientItem ingredientItem = new IngredientItem();
						BeanUtil.map(item, ingredientItem);
						ingredientItems.add(ingredientItem);
					}

				}
			} else {
				ingredientCollection = new IngredientCollection();
				BeanUtil.map(request, ingredientCollection);
				ingredientCollection.setCreatedBy("ADMIN");
				ingredientCollection.setUpdatedTime(new Date());
				ingredientCollection.setCreatedTime(new Date());
			}
			ingredientCollection = ingredientRepository.save(ingredientCollection);
			response = new Ingredient();
			BeanUtil.map(ingredientCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while addedit Ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Ingredient " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Ingredient> getIngredients(IngredientSearchRequest request) {
		List<Ingredient> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(request.getDiscarded());
			if (!DPDoctorUtils.anyStringEmpty(request.getSearchTerm()))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + request.getSearchTerm(), "i"),
						new Criteria("name").regex("^" + request.getSearchTerm()));

			if (request.getNutrients() != null && !request.getNutrients().isEmpty()) {
				criteria = criteria.and("nutrients").elemMatch(new Criteria("name").in(request.getNutrients()));
			}
			Aggregation aggregation = null;
			if (request.getSize() > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, IngredientCollection.class, Ingredient.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Ingredients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Ingredients " + e.getMessage());

		}
		return response;
	}

	@Override
	public Ingredient discardIngredient(String id, boolean discarded) {
		Ingredient response = null;
		try {
			IngredientCollection ingredientCollection = ingredientRepository.findOne(new ObjectId(id));
			if (ingredientCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Ingredients Not found with Id");
			}
			ingredientCollection.setDiscarded(discarded);
			ingredientCollection.setUpdatedTime(new Date());
			ingredientCollection = ingredientRepository.save(ingredientCollection);
			response = new Ingredient();
			BeanUtil.map(ingredientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while delete ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discard ingredient " + e.getMessage());

		}
		return response;

	}

	@Override
	public Ingredient getIngredient(String id) {
		Ingredient response = null;
		try {
			IngredientCollection ingredientCollection = ingredientRepository.findOne(new ObjectId(id));
			response = new Ingredient();
			BeanUtil.map(ingredientCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while getting ingredient " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting ingredient " + e.getMessage());

		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

}

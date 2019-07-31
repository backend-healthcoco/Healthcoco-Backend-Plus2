package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Ingredient;
import com.dpdocter.beans.Nutrient;
import com.dpdocter.beans.Recipe;
import com.dpdocter.beans.RecipeItem;
import com.dpdocter.collections.FavouriteRecipeCollection;
import com.dpdocter.collections.IngredientCollection;
import com.dpdocter.collections.MealCounterCollection;
import com.dpdocter.collections.NutrientCollection;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.repository.ESIngredientRepository;
import com.dpdocter.elasticsearch.repository.ESNutrientRepository;
import com.dpdocter.elasticsearch.repository.ESRecipeRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.FavouriteRecipeRepository;
import com.dpdocter.repository.IngredientRepository;
import com.dpdocter.repository.NutrientRepository;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.RecipeCounterAddItem;
import com.dpdocter.response.RecentRecipeResponse;
import com.dpdocter.response.RecipeCardResponse;
import com.dpdocter.services.RecipeService;
import com.mongodb.BasicDBObject;

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

	@Autowired
	private UserRepository userRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESRecipeRepository eSRecipeRepository;

	@Autowired
	private ESIngredientRepository esIngredientRepository;

	@Autowired
	private ESNutrientRepository esNutrientRepository;

	@Autowired
	private FavouriteRecipeRepository favouriteRecipeRepository;

	@Override
	public Nutrient addEditNutrient(Nutrient request) {
		Nutrient response = null;
		try {
			NutrientCollection nutrientCollection = null;

			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (doctor == null) {
				throw new BusinessException(ServiceError.NotFound, "doctor Not found with Id");
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutrientCollection = nutrientRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (nutrientCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutrient Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
						+ " " + doctor.getFirstName());
				request.setCreatedTime(nutrientCollection.getCreatedTime());
				BeanUtil.map(request, nutrientCollection);

			} else {
				nutrientCollection = new NutrientCollection();
				BeanUtil.map(request, nutrientCollection);
				nutrientCollection
						.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
								+ " " + doctor.getFirstName());
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
	public List<Nutrient> getNutrients(int size, int page, boolean discarded, String searchTerm, String category,
			String doctorId, String locationId, String hospitalId) {
		List<Nutrient> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and("category").is(category.toUpperCase());

			if (!DPDoctorUtils.allStringsEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.allStringsEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.allStringsEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
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
			ESNutrientDocument esNutrientDocument = null;
			NutrientCollection nutrientCollection = nutrientRepository.findById(new ObjectId(id)).orElse(null);
			if (nutrientCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Nutrient Not found with Id");
			}
			nutrientCollection.setDiscarded(discarded);
			nutrientCollection.setUpdatedTime(new Date());
			nutrientCollection = nutrientRepository.save(nutrientCollection);
			esNutrientDocument = esNutrientRepository.findById(id).orElse(null);
			if (esNutrientDocument != null) {
				esNutrientDocument.setUpdatedTime(new Date());
				esNutrientDocument.setDiscarded(discarded);
				esNutrientRepository.save(esNutrientDocument);
			}
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
			NutrientCollection nutrientCollection = nutrientRepository.findById(new ObjectId(id)).orElse(null);
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
				if (request.getRecipeImages() != null && !request.getRecipeImages().isEmpty())
					for (int index = 0; index <= request.getRecipeImages().size(); index++) {
						request.getRecipeImages().add(index,
								request.getRecipeImages().get(index).replace(imagePath, ""));
					}
				if (!DPDoctorUtils.anyStringEmpty(request.getVideoUrl())) {
					request.setVideoUrl(request.getVideoUrl().replace(imagePath, ""));
				}

				RecipeCollection recipeCollection = null;
				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "doctor Not found with Id");
				}
				if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
					recipeCollection = recipeRepository.findById(new ObjectId(request.getId())).orElse(null);
					if (recipeCollection == null) {
						throw new BusinessException(ServiceError.NotFound, "Recipe Not found with Id");
					}

					request.setUpdatedTime(new Date());
					request.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
							+ " " + doctor.getFirstName());
					request.setCreatedTime(recipeCollection.getCreatedTime());
					recipeCollection = new RecipeCollection();
					BeanUtil.map(request, recipeCollection);

					if (request.getRecipeImages() != null && !request.getRecipeImages().isEmpty()) {
						recipeCollection.setRecipeImages(new ArrayList<String>());
						recipeCollection.setRecipeImages(request.getRecipeImages());
					}
				} else {
					recipeCollection = new RecipeCollection();
					BeanUtil.map(request, recipeCollection);
					recipeCollection
							.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
									+ " " + doctor.getFirstName());
					recipeCollection.setUpdatedTime(new Date());
					recipeCollection.setCreatedTime(new Date());

				}
				recipeCollection = recipeRepository.save(recipeCollection);
				response = new Recipe();
				BeanUtil.map(recipeCollection, response);

			}
		} catch (BusinessException e) {
			logger.error("Error while addedit Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addedit Recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<Recipe> getRecipeList(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId , String planId) {
		List<Recipe> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex(searchTerm));
			}
			if (!DPDoctorUtils.allStringsEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.allStringsEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.allStringsEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.allStringsEmpty(planId))
				criteria.and("planIds").is(new ObjectId(planId));

		

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			response = mongoTemplate.aggregate(aggregation, RecipeCollection.class, Recipe.class).getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (Recipe recipe : response) {
					if (recipe.getRecipeImages() != null && !recipe.getRecipeImages().isEmpty()) {
						for (int index = 0; index < recipe.getRecipeImages().size(); index++) {
							recipe.getRecipeImages().add(index, getFinalImageURL(recipe.getRecipeImages().get(index)));
						}
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
			ESRecipeDocument document = null;
			RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(id)).orElse(null);
			if (recipeCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "recipe Not found with Id");
			}

			recipeCollection.setDiscarded(discarded);
			recipeCollection.setUpdatedTime(new Date());
			recipeCollection = recipeRepository.save(recipeCollection);
			response = new Recipe();
			document = eSRecipeRepository.findById(id).orElse(null);
			if (document != null) {
				document.setUpdatedTime(new Date());
				document.setDiscarded(discarded);
				eSRecipeRepository.save(document);
			}
			BeanUtil.map(recipeCollection, response);

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
			RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(id)).orElse(null);
			response = new Recipe();
			BeanUtil.map(recipeCollection, response);
			if (response != null) {
				if (response.getRecipeImages() != null && !response.getRecipeImages().isEmpty())
					for (int index = 0; index <= response.getRecipeImages().size(); index++) {
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

			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (doctor == null) {
				throw new BusinessException(ServiceError.NotFound, "doctor Not found with Id");
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				ingredientCollection = ingredientRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (ingredientCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "ingredient Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
						+ " " + doctor.getFirstName());
				request.setCreatedTime(ingredientCollection.getCreatedTime());
				ingredientCollection = new IngredientCollection();
				BeanUtil.map(request, ingredientCollection);
			} else {
				ingredientCollection = new IngredientCollection();
				BeanUtil.map(request, ingredientCollection);
				ingredientCollection
						.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
								+ " " + doctor.getFirstName());
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
	public List<Ingredient> getIngredients(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId) {
		List<Ingredient> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			if (!DPDoctorUtils.allStringsEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.allStringsEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.allStringsEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("quantity", new BasicDBObject("$first", "$quantity"))
							.append("name", new BasicDBObject("$first", "$name"))
							.append("note", new BasicDBObject("$first", "$note"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("nutrients", new BasicDBObject("$first", "$nutrients"))
							.append("equivalentMeasurements", new BasicDBObject("$first", "$equivalentMeasurements"))
							.append("calaries", new BasicDBObject("$first", "$calaries"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
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
			ESIngredientDocument esIngredientDocument = null;
			IngredientCollection ingredientCollection = ingredientRepository.findById(new ObjectId(id)).orElse(null);
			if (ingredientCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Ingredients Not found with Id");
			}
			ingredientCollection.setDiscarded(discarded);
			ingredientCollection.setUpdatedTime(new Date());
			ingredientCollection = ingredientRepository.save(ingredientCollection);
			esIngredientDocument = esIngredientRepository.findById(id).orElse(null);
			if (esIngredientDocument != null) {
				esIngredientDocument.setUpdatedTime(new Date());
				esIngredientDocument.setDiscarded(discarded);
				esIngredientRepository.save(esIngredientDocument);
			}
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
			IngredientCollection ingredientCollection = ingredientRepository.findById(new ObjectId(id)).orElse(null);
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

	@Override
	public Boolean addFavouriteRecipe(String userId, String recipeId) {
		Boolean response = true;
		try {
			FavouriteRecipeCollection recipeCollection = favouriteRecipeRepository.findByRecipeIdAndUserId(new ObjectId(userId),
					new ObjectId(recipeId));

			if (recipeCollection != null) {
				recipeCollection.setDiscarded(!recipeCollection.getDiscarded());
			}

			else {
				UserCollection user = userRepository.findById(new ObjectId(userId)).orElse(null);
				recipeCollection = new FavouriteRecipeCollection();

				recipeCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(user.getTitle()) ? user.getTitle() : "")
						+ " " + user.getFirstName());
				recipeCollection.setUpdatedTime(new Date());
				recipeCollection.setCreatedTime(new Date());
				recipeCollection.setUserId(new ObjectId(userId));
				recipeCollection.setRecipeId(new ObjectId(recipeId));
			}
			recipeCollection = favouriteRecipeRepository.save(recipeCollection);
			response = true;

		} catch (

		BusinessException e) {
			logger.error("Error while add to Favourite Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add to Favourite Recipe " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<RecipeCounterAddItem> getFavouriteRecipe(int size, int page, boolean discarded, String searchTerm,
			String userId) {
		List<RecipeCounterAddItem> response = null;
		try {
			Criteria criteria = new Criteria("favRecipe.discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			if (!DPDoctorUtils.allStringsEmpty(userId))
				criteria.and("favRecipe.userId").is(new ObjectId(userId));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("discarded").is(false)),
						Aggregation.lookup("favourite_recipes_cl", "_id", "recipeId", "favRecipe"),
						Aggregation.unwind("favRecipe"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("discarded").is(false)),
						Aggregation.lookup("favourite_recipes_cl", "_id", "recipeId", "favRecipe"),
						Aggregation.unwind("favRecipe"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			response = mongoTemplate.aggregate(aggregation, RecipeCollection.class, RecipeCounterAddItem.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Favourite Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Favourite Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<RecipeCounterAddItem> getFrequentRecipe(int size, int page, boolean discarded, String userId) {
		List<RecipeCounterAddItem> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.allStringsEmpty(userId))
				criteria.and("userId").is(new ObjectId(userId));
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$racipe.id").append("name", new BasicDBObject("$first", "$racipe.name"))
							.append("quantity", new BasicDBObject("$first", "$racipe.quantity"))
							.append("note", new BasicDBObject("$first", "$racipe.note"))
							.append("equivalentMeasurements",
									new BasicDBObject("$first", "$racipe.equivalentMeasurements"))
							.append("calories", new BasicDBObject("$first", "$racipe.calories"))
							.append("fat", new BasicDBObject("$first", "$racipe.fat"))
							.append("protein", new BasicDBObject("$first", "$racipe.protein"))
							.append("fiber", new BasicDBObject("$first", "$racipe.fiber"))
							.append("carbohydreate", new BasicDBObject("$first", "$racipe.carbohydreate"))));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("recipes"),
						Aggregation.lookup("recipe_cl", "recipes._id", "_id", "recipe"), Aggregation.unwind("recipe"),
						Aggregation.match(new Criteria("recipe.discarded").is(false)),
						Aggregation.sort(new Sort(Direction.DESC, "date")), aggregationOperation,
						Aggregation.skip((long)page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("recipes"),
						Aggregation.lookup("recipe_cl", "recipes._id", "recipeId", "recipe"),
						Aggregation.unwind("recipe"), Aggregation.match(new Criteria("recipe.discarded").is(false)),
						aggregationOperation, Aggregation.sort(new Sort(Direction.DESC, "date")));
			}
			response = mongoTemplate.aggregate(aggregation, MealCounterCollection.class, RecipeCounterAddItem.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Favourite Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Favourite Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<RecentRecipeResponse> getRecentRecipe(int size, int page, String userId, boolean discarded,
			String mealTime) {
		List<RecentRecipeResponse> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.allStringsEmpty(userId))
				criteria.and("userId").is(new ObjectId(userId));

			if (!DPDoctorUtils.allStringsEmpty(mealTime))
				criteria.and("mealTime").is(new ObjectId(mealTime.toUpperCase()));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Direction.DESC, "date")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "date")));
			}
			response = mongoTemplate.aggregate(aggregation, MealCounterCollection.class, RecentRecipeResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Recent Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Recent Recipe " + e.getMessage());

		}
		return response;
	}

	@Override
	public List<RecipeCardResponse> getRecipeByPlanId(int size, int page, String planId) {
		List<RecipeCardResponse> response = null;
		try {
			Criteria criteria = new Criteria("planIds").is(new ObjectId(planId));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, RecipeCollection.class, RecipeCardResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Recipe " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Recent Recipe " + e.getMessage());

		}
		return response;
	}
	
}

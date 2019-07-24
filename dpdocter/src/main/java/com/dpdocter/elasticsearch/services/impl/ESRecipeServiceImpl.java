package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Exercise;
import com.dpdocter.elasticsearch.document.ESExerciseDocument;
import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.repository.ESIngredientRepository;
import com.dpdocter.elasticsearch.repository.ESNutrientRepository;
import com.dpdocter.elasticsearch.repository.ESRecipeRepository;
import com.dpdocter.elasticsearch.response.ESIngredientResponse;
import com.dpdocter.elasticsearch.response.ESNutrientResponse;
import com.dpdocter.elasticsearch.response.ESRecipeResponse;
import com.dpdocter.elasticsearch.response.ESRecipeUserAppResponse;
import com.dpdocter.elasticsearch.services.ESRecipeService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESRecipeServiceImpl implements ESRecipeService {

	private static Logger logger = Logger.getLogger(ESPrescriptionServiceImpl.class.getName());

	@Autowired
	private ESNutrientRepository esNutrientRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ESIngredientRepository esIngredientRepository;

	@Autowired
	private ESRecipeRepository esRecipeRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public boolean addNutrient(ESNutrientDocument request) {
		boolean response = false;
		try {
			esNutrientRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NUTRIENT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Nutrient in ES");
		}
		return response;
	}

	@Override
	public boolean addIngredient(ESIngredientDocument request) {
		boolean response = false;
		try {
			esIngredientRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INGREDIENT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Ingredient in ES");
		}
		return response;
	}

	@Override
	public boolean addRecipe(ESRecipeDocument request) {
		boolean response = false;
		try {
			esRecipeRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.RECIPE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Recipe in ES");
		}
		return response;
	}

	@Override
	public List<ESRecipeResponse> searchRecipe(int page, int size, Boolean discarded, String searchTerm) {
		List<ESRecipeResponse> response = null;
		ESRecipeResponse esRecipeResponse = null;

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			boolQueryBuilder
					.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm.replaceAll("[^a-zA-Z0-9]", " ")));
		}
		List<ESRecipeDocument> recipes = null;
		SearchQuery searchQuery = null;
		if (size > 0)
			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(PageRequest.of(page, size, Direction.ASC, "name")).build();
		else
			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(PageRequest.of(0, 15, Direction.ASC, "name")).build();
		recipes = elasticsearchTemplate.queryForList(searchQuery, ESRecipeDocument.class);
		if (recipes != null && !recipes.isEmpty()) {
			response = new ArrayList<ESRecipeResponse>();
			for (ESRecipeDocument recipe : recipes) {
				esRecipeResponse = new ESRecipeResponse();
				BeanUtil.map(recipe, esRecipeResponse);
				response.add(esRecipeResponse);
			}
		}
		return response;

	}

	@Override
	public List<ESNutrientResponse> searchNutrient(int page, int size, Boolean discarded, String searchTerm) {
		List<ESNutrientResponse> response = null;
		ESNutrientResponse esNutrientResponse = null;
		SearchQuery searchQuery = null;
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			boolQueryBuilder
					.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm.replaceAll("[^a-zA-Z0-9]", "")));
		}
		if (size == 0)
			size = 15;

		searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
				.withPageable(PageRequest.of(page, size, Direction.ASC, "name")).build();
		List<ESNutrientDocument> nutrients = elasticsearchTemplate.queryForList(searchQuery, ESNutrientDocument.class);
		if (nutrients != null && !nutrients.isEmpty()) {
			response = new ArrayList<ESNutrientResponse>();
			for (ESNutrientDocument nutrient : nutrients) {
				esNutrientResponse = new ESNutrientResponse();
				BeanUtil.map(nutrient, esNutrientResponse);
				response.add(esNutrientResponse);
			}
		}
		return response;

	}

	@Override
	public List<ESIngredientResponse> searchIngredient(int page, int size, Boolean discarded, String searchTerm) {
		List<ESIngredientResponse> response = null;
		ESIngredientResponse esIngredientResponse = null;
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			boolQueryBuilder
					.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm.replaceAll("[^a-zA-Z0-9]", " ")));
		}
		if (size <= 0)
			size = 15;
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
				.withPageable(PageRequest.of(page, size, Direction.ASC, "name")).build();
		List<ESIngredientDocument> ingrdients = elasticsearchTemplate.queryForList(searchQuery,
				ESIngredientDocument.class);

		if (ingrdients != null && !ingrdients.isEmpty()) {
			response = new ArrayList<ESIngredientResponse>();
			for (ESIngredientDocument ingrdient : ingrdients) {
				esIngredientResponse = new ESIngredientResponse();
				BeanUtil.map(ingrdient, esIngredientResponse);
				response.add(esIngredientResponse);
			}
		}
		return response;

	}

	@Override
	public List<Exercise> searchExercise(int page, int size, Boolean discarded, String searchTerm) {

		List<Exercise> response = null;
		try {
			Exercise exerciseResponse = null;
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				boolQueryBuilder
						.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm.replaceAll("[^a-zA-Z0-9]", " ")));
			}
			if (size == 0)
				size = 15;
			List<ESExerciseDocument> exercises = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
					.withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("name").order(SortOrder.ASC))
					.withPageable(PageRequest.of(page, size)).build(), ESExerciseDocument.class);

			if (exercises != null && !exercises.isEmpty()) {
				response = new ArrayList<Exercise>();
				for (ESExerciseDocument excerciseDocument : exercises) {
					exerciseResponse = new Exercise();
					BeanUtil.map(excerciseDocument, exerciseResponse);
					response.add(exerciseResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while search exercise: " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while search exercise:  " + e.getCause().getMessage());

		}
		return response;

	}

	@Override
	public List<ESRecipeUserAppResponse> searchRecipeForUserApp(int page, int size, Boolean discarded,
			String searchTerm) {
		List<ESRecipeUserAppResponse> response = null;
		ESRecipeUserAppResponse esRecipeResponse = null;

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded))
				.must(QueryBuilders.matchPhrasePrefixQuery("verified", true));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			boolQueryBuilder
					.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm.replaceAll("[^a-zA-Z0-9]", " ")));
		}
		List<ESRecipeDocument> recipes = null;
		SearchQuery searchQuery = null;
		if (size > 0)
			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(PageRequest.of(page, size, Direction.ASC, "name")).build();
		else
			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(PageRequest.of(0, 15, Direction.ASC, "name")).build();
		recipes = elasticsearchTemplate.queryForList(searchQuery, ESRecipeDocument.class);
		if (recipes != null && !recipes.isEmpty()) {
			response = new ArrayList<ESRecipeUserAppResponse>();
			for (ESRecipeDocument recipe : recipes) {
				esRecipeResponse = new ESRecipeUserAppResponse();
				BeanUtil.map(recipe, esRecipeResponse);
				response.add(esRecipeResponse);
			}
		}
		return response;

	}

}

package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESIngredientDocument;
import com.dpdocter.elasticsearch.document.ESNutrientDocument;
import com.dpdocter.elasticsearch.document.ESRecipeDocument;
import com.dpdocter.elasticsearch.repository.ESIngredientRepository;
import com.dpdocter.elasticsearch.repository.ESNutrientRepository;
import com.dpdocter.elasticsearch.repository.ESRecipeRepository;
import com.dpdocter.elasticsearch.response.ESIngredientResponse;
import com.dpdocter.elasticsearch.response.ESNutrientResponse;
import com.dpdocter.elasticsearch.response.ESRecipeResponse;
import com.dpdocter.elasticsearch.services.ESRecipeService;
import com.dpdocter.enums.Resource;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;

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
	private TransportClient transportClient;

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
				.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm))
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", false));

		List<ESRecipeDocument> recipes = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
				.withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.ASC))
				.withPageable(new PageRequest(size, page)).build(), ESRecipeDocument.class);

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
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm))
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", false));

		List<ESNutrientDocument> nutrients = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
				.withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.ASC))
				.withPageable(new PageRequest(size, page)).build(), ESNutrientDocument.class);
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
				.must(QueryBuilders.matchPhrasePrefixQuery("name", searchTerm))
				.must(QueryBuilders.matchPhrasePrefixQuery("discarded", false));

		List<ESIngredientDocument> ingrdients = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
				.withQuery(boolQueryBuilder).withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC))
				.withPageable(new PageRequest(size, page)).build(), ESIngredientDocument.class);

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

}

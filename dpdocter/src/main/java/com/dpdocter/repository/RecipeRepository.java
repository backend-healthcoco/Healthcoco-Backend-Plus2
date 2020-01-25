package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RecipeCollection;

public interface RecipeRepository extends MongoRepository<RecipeCollection, ObjectId> {

	List<RecipeCollection> findByMealTiming(String time);

	List<RecipeCollection> findByMealTimingAndFoodGroupsValueRegex(String time, String string);

	List<RecipeCollection> findByMealTimingAndFoodGroupsValueIn(String time, List<String> asList);

	List<RecipeCollection> findByFoodGroupsValueIn(List<String> asList);

	RecipeCollection findByNameRegex(String string);

}

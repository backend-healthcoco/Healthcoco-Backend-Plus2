package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.RecipeCollection;

public interface RecipeRepository extends MongoRepository<RecipeCollection, ObjectId> {

	List<RecipeCollection> findByMealTiming(String time);

}

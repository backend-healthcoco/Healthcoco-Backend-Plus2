package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.RecipeStepCollection;

public interface RecipeStepRepository extends MongoRepository<RecipeStepCollection, ObjectId>{

	
	@Query("{'recipeId':?0}")
	RecipeStepCollection findByRecipeId(ObjectId recipeId);

}

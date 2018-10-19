package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.FavouriteRecipeCollection;

public interface FavouriteRecipeRepository extends MongoRepository<FavouriteRecipeCollection, ObjectId> {
	@Query("{'recipeId':?0,'userId':?1}")
	public FavouriteRecipeCollection findByUserId(ObjectId recipeId, ObjectId userId);
}

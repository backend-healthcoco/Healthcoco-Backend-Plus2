package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FavouriteRecipeCollection;

public interface FavouriteRecipeRepository extends MongoRepository<FavouriteRecipeCollection, ObjectId> {

	public FavouriteRecipeCollection findByRecipeIdAndUserId(ObjectId recipeId, ObjectId userId);
}

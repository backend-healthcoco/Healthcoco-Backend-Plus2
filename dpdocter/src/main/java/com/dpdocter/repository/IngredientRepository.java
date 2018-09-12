package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.IngredientCollection;

public interface IngredientRepository extends MongoRepository<IngredientCollection, ObjectId>{

}

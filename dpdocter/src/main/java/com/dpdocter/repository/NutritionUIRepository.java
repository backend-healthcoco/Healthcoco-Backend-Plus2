package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NutritionUICollection;

public interface NutritionUIRepository extends MongoRepository<NutritionUICollection, ObjectId> {

}

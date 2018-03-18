package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NutritionReferenceCollection;

public interface NutritionReferenceRepository extends MongoRepository<NutritionReferenceCollection ,ObjectId>{

}

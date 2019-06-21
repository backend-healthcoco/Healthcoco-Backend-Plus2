package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NutrientCollection;

public interface NutrientRepository extends MongoRepository<NutrientCollection, ObjectId> {

}

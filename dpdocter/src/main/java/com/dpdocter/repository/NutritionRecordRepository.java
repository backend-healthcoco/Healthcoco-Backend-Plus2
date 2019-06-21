package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NutritionRecordCollection;

public interface NutritionRecordRepository extends MongoRepository<NutritionRecordCollection, ObjectId> {

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.elasticsearch.repository.NutritionReferralCollection;

public interface NutritionReferralRepository extends MongoRepository<NutritionReferralCollection, ObjectId> {

}

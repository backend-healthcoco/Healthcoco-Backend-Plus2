package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SubscriptionNutritionPlanCollection;

@Repository
public interface SubscritptionNutritionPlanRepository
		extends MongoRepository<SubscriptionNutritionPlanCollection, ObjectId> {

}

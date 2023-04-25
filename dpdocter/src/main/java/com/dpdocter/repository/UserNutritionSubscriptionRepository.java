package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.UserNutritionSubscriptionCollection;

@Repository
public interface UserNutritionSubscriptionRepository
		extends MongoRepository<UserNutritionSubscriptionCollection, ObjectId> {

}

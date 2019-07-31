package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FeedbackRecommendationCollection;

public interface FeedbackRecommendationRepository extends MongoRepository<FeedbackRecommendationCollection, ObjectId> {
	
}
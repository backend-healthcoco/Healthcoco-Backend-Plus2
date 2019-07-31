package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FeedbackCollection;

public interface FeedbackRepository extends MongoRepository<FeedbackCollection, ObjectId> {

}

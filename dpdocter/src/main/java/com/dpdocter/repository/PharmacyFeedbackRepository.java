package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PharmacyFeedbackCollection;

public interface PharmacyFeedbackRepository extends MongoRepository<PharmacyFeedbackCollection, ObjectId>{

}

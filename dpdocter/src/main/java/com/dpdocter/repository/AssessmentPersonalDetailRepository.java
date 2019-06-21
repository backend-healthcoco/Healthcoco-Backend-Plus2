package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AssessmentPersonalDetailCollection;

public interface AssessmentPersonalDetailRepository
		extends MongoRepository<AssessmentPersonalDetailCollection, ObjectId> {

}

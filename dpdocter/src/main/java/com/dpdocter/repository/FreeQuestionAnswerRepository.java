package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FreeQuestionAnswerCollection;


public interface FreeQuestionAnswerRepository extends MongoRepository<FreeQuestionAnswerCollection, ObjectId> {


}

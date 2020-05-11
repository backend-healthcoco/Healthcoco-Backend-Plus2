package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ConsultationProblemDetailsCollection;

public interface ConsultationProblemDetailsRepository extends MongoRepository<ConsultationProblemDetailsCollection, ObjectId>{

}

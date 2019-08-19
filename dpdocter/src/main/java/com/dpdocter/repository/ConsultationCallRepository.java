package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ConsultationCallCollection;

public interface ConsultationCallRepository extends MongoRepository<ConsultationCallCollection, ObjectId> {

}

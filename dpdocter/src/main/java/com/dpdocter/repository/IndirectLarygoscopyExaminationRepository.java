package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.IndirectLarygoscopyExaminationCollection;

public interface IndirectLarygoscopyExaminationRepository extends MongoRepository<IndirectLarygoscopyExaminationCollection, ObjectId>{

}

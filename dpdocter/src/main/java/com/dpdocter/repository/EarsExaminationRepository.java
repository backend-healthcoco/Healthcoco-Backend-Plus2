package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.EarsExaminationCollection;

public interface EarsExaminationRepository extends  MongoRepository<EarsExaminationCollection, ObjectId>{

}

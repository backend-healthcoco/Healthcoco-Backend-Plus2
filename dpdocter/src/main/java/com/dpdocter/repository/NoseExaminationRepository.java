package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.NoseExaminationCollection;

public interface NoseExaminationRepository extends MongoRepository<NoseExaminationCollection, ObjectId>{

}

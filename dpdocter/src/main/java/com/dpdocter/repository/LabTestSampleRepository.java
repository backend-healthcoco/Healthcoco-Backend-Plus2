package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LabTestSampleCollection;

public interface LabTestSampleRepository extends MongoRepository<LabTestSampleCollection, ObjectId>{

}

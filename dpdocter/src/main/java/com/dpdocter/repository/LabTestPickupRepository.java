package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LabTestPickupCollection;

public interface LabTestPickupRepository extends MongoRepository<LabTestPickupCollection, ObjectId>{

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TreatmentServicesCostCollection;

public interface TreatmentServicesCostRepository extends MongoRepository<TreatmentServicesCostCollection, ObjectId> {
    
}

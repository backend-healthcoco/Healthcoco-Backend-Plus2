package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ECGDetailsCollection;

public interface ECGDetailsRepository extends MongoRepository<ECGDetailsCollection, ObjectId>{

}

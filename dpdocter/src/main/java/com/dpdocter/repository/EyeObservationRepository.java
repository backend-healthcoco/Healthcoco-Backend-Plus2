package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.EyeObservationCollection;

public interface EyeObservationRepository extends MongoRepository<EyeObservationCollection, ObjectId>{

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BloodGlucoseCollection;

public interface BloodGlucoseRepository extends MongoRepository<BloodGlucoseCollection, ObjectId>{

}

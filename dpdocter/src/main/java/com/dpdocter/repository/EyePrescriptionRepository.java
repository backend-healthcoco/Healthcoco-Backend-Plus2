package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.EyeObservationCollection;
import com.dpdocter.collections.EyePrescriptionCollection;

public interface EyePrescriptionRepository extends MongoRepository<EyePrescriptionCollection, ObjectId>{

}

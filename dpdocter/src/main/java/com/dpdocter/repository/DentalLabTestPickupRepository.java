package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalLabPickupCollection;

public interface DentalLabTestPickupRepository extends MongoRepository<DentalLabPickupCollection, ObjectId>{

}

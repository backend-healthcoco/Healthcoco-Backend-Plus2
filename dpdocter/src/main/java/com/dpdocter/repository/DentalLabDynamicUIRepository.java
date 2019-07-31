package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalLabDynamicUICollection;

public interface DentalLabDynamicUIRepository extends MongoRepository<DentalLabDynamicUICollection, ObjectId> {

	DentalLabDynamicUICollection findByDoctorId(ObjectId dentalLabId);

}

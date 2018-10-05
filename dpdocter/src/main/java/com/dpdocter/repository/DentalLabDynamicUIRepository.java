package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalLabDynamicUICollection;

public interface DentalLabDynamicUIRepository extends MongoRepository<DentalLabDynamicUICollection, ObjectId> {

	/*@Query(value = "{'dentalLabId' : ?0}")
	DentalLabDynamicUICollection getbyDentalLabId(ObjectId dentalLabId);*/
	
	 @Query("{'doctorId': ?0}")
	 DentalLabDynamicUICollection findByDentalLabId(ObjectId dentalLabId);

}

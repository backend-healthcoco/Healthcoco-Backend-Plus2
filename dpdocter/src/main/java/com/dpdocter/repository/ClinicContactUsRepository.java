package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ClinicContactUsCollection;

public interface ClinicContactUsRepository extends MongoRepository<ClinicContactUsCollection, ObjectId> {

	

	@Query("{'locationName': ?0}")
	public ClinicContactUsCollection findByLocationName(String locationName);

}

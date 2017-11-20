package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.IPDReportsCollection;

public interface IPDReportsRepository extends MongoRepository<IPDReportsCollection, ObjectId> {

	@Query(value = "{ 'locationId': ?0, 'doctorId': ?1}", count = true)
	Integer getReportsCount(ObjectId locationId, ObjectId doctorId);

	@Query(value = "{ 'locationId': ?0}", count = true)
	Integer getReportsCount(ObjectId locationId);

}

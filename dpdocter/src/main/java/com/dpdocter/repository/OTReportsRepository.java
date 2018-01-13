package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OTReportsCollection;

public interface OTReportsRepository extends MongoRepository<OTReportsCollection, ObjectId> {

	@Query(value = "{ 'locationId': ?0, 'doctorId': ?1,'isPatientDiscarded': ?2}", count = true)
	Integer getReportsCount(ObjectId locationId, ObjectId doctorId, Boolean isPatientDiscarded);

	@Query(value = "{ 'locationId': ?0,'isPatientDiscarded': ?1}", count = true)
	Integer getReportsCount(ObjectId locationId, Boolean isPatientDiscarded);

}

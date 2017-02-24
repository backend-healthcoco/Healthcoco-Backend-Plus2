package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OTReportsCollection;

public interface OTReportsRepository extends MongoRepository<OTReportsCollection, ObjectId> {

	@Query(value = "{'patientId': ?0,'doctorId' : ?1, 'locationId': ?2}", count = true)
    Integer getReportsCount(ObjectId patientId, ObjectId doctorId, ObjectId locationId );
}

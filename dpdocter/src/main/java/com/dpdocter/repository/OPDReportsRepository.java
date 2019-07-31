package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OPDReportsCollection;

public interface OPDReportsRepository extends MongoRepository<OPDReportsCollection, ObjectId> {

	@Query("{'prescriptionId': ?0}")
	public OPDReportsCollection findByPrescriptionId(ObjectId prescriptionId);
	
	@Query("{'visitId': ?0}")
	public OPDReportsCollection findByVisitId(ObjectId visitId);

	@Query(value = "{ 'locationId': ?0, 'doctorId': ?1}", count = true)
	Integer getReportsCount(ObjectId locationId, ObjectId doctorId);

}

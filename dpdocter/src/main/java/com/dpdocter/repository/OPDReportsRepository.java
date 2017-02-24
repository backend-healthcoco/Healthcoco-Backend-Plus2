package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OPDReportsCollection;

public interface OPDReportsRepository extends MongoRepository<OPDReportsCollection, ObjectId> {

	@Query("{'prescriptionId': ?0}")
	public OPDReportsCollection getOPDReportByPrescriptionId(ObjectId prescriptionId);

	@Query(value = "{'patientId': ?0,'doctorId' : ?1, 'locationId': ?2}", count = true)
    Integer getReportsCount(ObjectId patientId, ObjectId doctorId, ObjectId locationId );

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OPDReportsCollection;

public interface OPDReportsRepository extends MongoRepository<OPDReportsCollection, ObjectId> {

	@Query
	public OPDReportsCollection getOPDReportById(String id);
	
}

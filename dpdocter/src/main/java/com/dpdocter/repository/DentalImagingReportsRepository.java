package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DentalImagingReportsCollection;

public interface DentalImagingReportsRepository extends MongoRepository<DentalImagingReportsCollection, ObjectId>{

	@Query("{'requestId' :?0 , 'discarded' :?1}")
	List<DentalImagingReportsCollection> getReportsByRequestId(ObjectId requestId ,Boolean discarded);
	
}

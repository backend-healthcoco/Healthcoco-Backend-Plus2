package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.VaccineCollection;
import com.dpdocter.collections.VersionControlCollection;

public interface VaccineRepository extends MongoRepository<VaccineCollection, ObjectId> {

	 @Query("{'patientId': ?0 , 'doctorId':?1 , 'locationId':?2 , 'hospitalId':?3}")
	 public List<VaccineCollection> findBypatientdoctorlocationhospital(ObjectId patientId , ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
	
	 
	 @Query("{'patientId': ?0}")
	 public List<VaccineCollection> findBypatientId(ObjectId patientId);
	
}

package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VaccineCollection;

public interface VaccineRepository extends MongoRepository<VaccineCollection, ObjectId> {

//	 @Query("{'patientId': ?0 , 'doctorId':?1 , 'locationId':?2 , 'hospitalId':?3}")
//	 public List<VaccineCollection> findBypatientdoctorlocationhospital(ObjectId patientId , ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);
//	 
//	 @Query("{'dueDate': {'$gte': ?0}, 'dueDate': {'$lte': ?1}}")
//	 public List<VaccineCollection> findVaccinations(DateTime start, DateTime end);
	 
//	 @Query("{'patientId': ?0}")
	 public List<VaccineCollection> findByPatientId(ObjectId patientId);
	
}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PatientShareProfileCollection;

public interface PatientShareRepository extends MongoRepository<PatientShareProfileCollection,ObjectId>{

	PatientShareProfileCollection findByPatientUserDemographicsHealthId(String requestId);


}

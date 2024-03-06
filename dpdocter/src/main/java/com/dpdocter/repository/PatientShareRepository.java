package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PatientShareProfileCollection;

public interface PatientShareRepository extends MongoRepository<PatientShareProfileCollection,ObjectId>{

	//List<PatientShareProfileCollection> findByPatientUserDemographicsHealthId(String healthId);

	List<PatientShareProfileCollection> findByProfilePatientHealthId(String healthId);


}

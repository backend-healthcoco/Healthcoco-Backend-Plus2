package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientAdmissionCollection;
@Repository
public interface PatientAdmissionRepository extends MongoRepository<PatientAdmissionCollection, String>{
	
	PatientAdmissionCollection findByUserId(String userId);

}

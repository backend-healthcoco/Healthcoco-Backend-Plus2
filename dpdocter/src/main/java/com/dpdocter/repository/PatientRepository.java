package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientCollection;
@Repository
public interface PatientRepository extends MongoRepository<PatientCollection, String>{
	public PatientCollection findByUserId(String userId);
}

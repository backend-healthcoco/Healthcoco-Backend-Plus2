package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorContactUsCollection;

public interface DoctorContactUsRepository extends MongoRepository<DoctorContactUsCollection, String>{
	
	

}

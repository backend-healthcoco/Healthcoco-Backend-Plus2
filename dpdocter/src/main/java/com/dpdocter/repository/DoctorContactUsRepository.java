package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DoctorContactUsCollection;

public interface DoctorContactUsRepository extends MongoRepository<DoctorContactUsCollection, ObjectId>{
	
	@Query("{'id': ?0}")
	public DoctorContactUsCollection findByContactId(String contactId);

	@Query("{'userName': ?0, 'emailAddress': ?0}")
	public DoctorContactUsCollection findByEmailIdAndUserName(String emailAddress);
}

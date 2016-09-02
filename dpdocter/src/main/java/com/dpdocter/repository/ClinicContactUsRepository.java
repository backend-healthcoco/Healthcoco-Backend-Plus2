package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ClinicContactUsCollection;

public interface ClinicContactUsRepository extends MongoRepository<ClinicContactUsCollection, String> {
	
	@Query("{'id': ?0}")
	public ClinicContactUsCollection findByContactId(String contactId);

	@Query("{'locationName': ?0}")
	public ClinicContactUsCollection findByLocationName(String locationName);

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LocaleCollection;

public interface LocaleRepository extends MongoRepository<LocaleCollection, ObjectId>{

	@Query("{'contactNumber' : ?0}")
	public LocaleCollection findByMobileNumber(String contactNumber);
	
}

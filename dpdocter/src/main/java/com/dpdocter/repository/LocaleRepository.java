package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LocaleCollection;

public interface LocaleRepository extends MongoRepository<LocaleCollection, ObjectId>{

	public LocaleCollection findByContactNumber(String contactNumber);
	
}

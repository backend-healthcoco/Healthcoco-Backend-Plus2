package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CountryCollection;

public interface CountryRepository extends MongoRepository<CountryCollection, ObjectId> {

	@Query("{'countryCode' : ?0}")
	CountryCollection findByCountryCode(String string);

}

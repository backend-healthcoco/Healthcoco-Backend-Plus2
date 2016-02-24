package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CityCollection;

public interface CityRepository extends MongoRepository<CityCollection, String> {

	@Query("{'stateId': ?0}")
	List<CityCollection> findAll(String stateId);

}

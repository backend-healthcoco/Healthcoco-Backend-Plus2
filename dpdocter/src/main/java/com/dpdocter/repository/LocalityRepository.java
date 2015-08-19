package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LocalityCollection;

public interface LocalityRepository extends MongoRepository<LocalityCollection, String> {

	@Query("{'cityId': ?0}")
	List<LocalityCollection> findByCityId(String cityId);

}

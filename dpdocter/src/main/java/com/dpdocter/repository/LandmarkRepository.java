package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LandmarkCollection;

public interface LandmarkRepository extends MongoRepository<LandmarkCollection, String> {

	@Query("{'cityId': ?0}")
	List<LandmarkCollection> findByCityId(String cityId);

}

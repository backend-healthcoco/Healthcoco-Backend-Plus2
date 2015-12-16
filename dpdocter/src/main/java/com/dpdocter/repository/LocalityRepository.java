package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LandmarkLocalityCollection;

public interface LocalityRepository extends MongoRepository<LandmarkLocalityCollection, String> {

    @Query("{'cityId': ?0}")
    List<LandmarkLocalityCollection> findByCityId(String cityId);

}

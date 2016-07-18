package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LandmarkLocalityCollection;

public interface LandmarkLocalityRepository extends MongoRepository<LandmarkLocalityCollection, ObjectId> {

    @Query("{'cityId': ?0}")
    List<LandmarkLocalityCollection> findByCityId(ObjectId cityId);

}

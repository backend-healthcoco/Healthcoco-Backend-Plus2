package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.LandmarkLocalityCollection;

public interface LandmarkLocalityRepository extends MongoRepository<LandmarkLocalityCollection, ObjectId> {

    List<LandmarkLocalityCollection> findByCityId(ObjectId cityId);

}

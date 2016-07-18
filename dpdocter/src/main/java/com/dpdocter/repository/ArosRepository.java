package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ArosCollection;

public interface ArosRepository extends MongoRepository<ArosCollection, ObjectId> {

    @Query(value = "{'roleOrUserId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2}")
    ArosCollection findOne(ObjectId roleOrUserId, ObjectId locationId, ObjectId hospitalId);

}

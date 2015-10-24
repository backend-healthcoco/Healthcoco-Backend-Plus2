package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ArosCollection;

public interface ArosRepository extends MongoRepository<ArosCollection, String> {

    @Query(value = "{'roleOrUserId' : ?0, 'locationId' : ?1, 'hospitalId' : ?2}")
    ArosCollection findOne(String roleOrUserId, String locationId, String hospitalId);

}

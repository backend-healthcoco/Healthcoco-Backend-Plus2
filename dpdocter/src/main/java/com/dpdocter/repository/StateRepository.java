package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.StateCollection;

public interface StateRepository extends MongoRepository<StateCollection, String> {

    @Query("{'state': ?0}")
    StateCollection findByName(String state);

}

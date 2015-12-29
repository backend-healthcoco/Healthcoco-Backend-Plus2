package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.StateCollection;

public interface StateRepository extends MongoRepository<StateCollection, String>  {

}

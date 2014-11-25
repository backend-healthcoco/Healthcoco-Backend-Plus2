package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserLocationCollection;

public interface UserLocationRepository extends MongoRepository<UserLocationCollection, String>{

}

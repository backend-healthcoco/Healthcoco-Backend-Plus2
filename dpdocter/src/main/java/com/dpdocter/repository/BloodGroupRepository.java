package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BloodGroupCollection;

public interface BloodGroupRepository extends MongoRepository<BloodGroupCollection, String> {

}

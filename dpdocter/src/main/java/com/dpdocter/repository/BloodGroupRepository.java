package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.BloodGroupCollection;

public interface BloodGroupRepository extends MongoRepository<BloodGroupCollection, String> {

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<BloodGroupCollection> find(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<BloodGroupCollection> find(Date date, Sort sort);

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserLocationCollection;

public interface UserLocationRepository extends MongoRepository<UserLocationCollection, ObjectId>, PagingAndSortingRepository<UserLocationCollection, ObjectId> {

}

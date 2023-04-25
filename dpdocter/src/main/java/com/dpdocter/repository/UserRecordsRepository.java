package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserRecordsCollection;

public interface UserRecordsRepository extends MongoRepository<UserRecordsCollection, ObjectId>,
		PagingAndSortingRepository<UserRecordsCollection, ObjectId> {

}

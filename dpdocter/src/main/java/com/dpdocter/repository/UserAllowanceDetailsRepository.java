package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserAllowanceDetailsCollection;

public interface UserAllowanceDetailsRepository extends MongoRepository<UserAllowanceDetailsCollection, ObjectId>, PagingAndSortingRepository<UserAllowanceDetailsCollection, ObjectId> {

	UserAllowanceDetailsCollection findByUserIds(ObjectId userId);

}

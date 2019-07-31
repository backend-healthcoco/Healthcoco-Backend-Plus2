package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserRemindersCollection;

public interface UserRemindersRepository extends MongoRepository<UserRemindersCollection, ObjectId>, PagingAndSortingRepository<UserRemindersCollection, ObjectId> {

	UserRemindersCollection findByUserId(ObjectId userId);

}

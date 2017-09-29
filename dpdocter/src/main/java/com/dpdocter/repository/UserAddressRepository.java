package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserAddressCollection;

public interface UserAddressRepository extends MongoRepository<UserAddressCollection, ObjectId>, PagingAndSortingRepository<UserAddressCollection, ObjectId> {

	@Query("{'id': ?0, 'userIds': ?1}")
	UserAddressCollection find(ObjectId addressId, ObjectId userIds);

	@Query("{'id': ?0, 'mobileNumber': ?1}")
	UserAddressCollection find(ObjectId addressId, String mobileNumber);

}

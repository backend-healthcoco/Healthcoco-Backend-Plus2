package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserAddressCollection;

public interface UserAddressRepository extends MongoRepository<UserAddressCollection, ObjectId>, PagingAndSortingRepository<UserAddressCollection, ObjectId> {

	UserAddressCollection findByIdAndUserIds(ObjectId addressId, ObjectId userIds);

	UserAddressCollection findByIdAndMobileNumber(ObjectId addressId, String mobileNumber);

}

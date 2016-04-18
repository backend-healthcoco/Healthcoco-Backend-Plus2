package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserDeviceCollection;

public interface UserDeviceRepository extends MongoRepository<UserDeviceCollection, String>, PagingAndSortingRepository<UserDeviceCollection, String> {

	@Query("{'userId': ?0}")
	UserDeviceCollection findByUserId(String userId);

}

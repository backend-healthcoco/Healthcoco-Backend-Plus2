package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserDeviceCollection;

public interface UserDeviceRepository extends MongoRepository<UserDeviceCollection, ObjectId>, PagingAndSortingRepository<UserDeviceCollection, ObjectId> {

	@Query("{'userIds': ?0}")
	List<UserDeviceCollection> findByUserId(ObjectId userId);

	@Query("{'deviceId': ?0}")
	UserDeviceCollection findByDeviceId(String deviceId);

	@Query("{'role': ?0}")
	List<UserDeviceCollection> findByRole(String role);

	@Query("{'role': ?0, 'deviceType': ?1}")
	List<UserDeviceCollection> findByRoleAndType(String role, String type);

}

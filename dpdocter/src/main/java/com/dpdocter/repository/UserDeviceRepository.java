package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserDeviceCollection;

public interface UserDeviceRepository extends MongoRepository<UserDeviceCollection, String>, PagingAndSortingRepository<UserDeviceCollection, String> {

	@Query("{'userIds': ?0}")
	List<UserDeviceCollection> findByUserId(String userId);

	@Query("{'deviceId': ?0}")
	UserDeviceCollection findByDeviceId(String deviceId);

	@Query("{'role': ?0}")
	List<UserDeviceCollection> findByRole(String role);

	@Query("{'role': ?0, 'deviceType': ?1}")
	List<UserDeviceCollection> findByRoleAndType(String role, String type);

}

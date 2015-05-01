package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserLocationCollection;

public interface UserLocationRepository extends MongoRepository<UserLocationCollection, String> {
	List<UserLocationCollection> findByUserId(String userId);

}

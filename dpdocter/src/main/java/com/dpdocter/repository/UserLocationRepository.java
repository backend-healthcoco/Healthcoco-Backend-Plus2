package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.UserLocationCollection;

public interface UserLocationRepository extends MongoRepository<UserLocationCollection, String> {
    List<UserLocationCollection> findByUserId(String userId);

    @Query("{'userId' : ?0, 'locationId' : ?1}")
    UserLocationCollection findByUserIdAndLocationId(String userId, String locationId);

}

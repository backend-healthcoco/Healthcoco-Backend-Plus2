package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserLocationCollection;

public interface UserLocationRepository extends MongoRepository<UserLocationCollection, String>, PagingAndSortingRepository<UserLocationCollection, String> {
    @Query("{'userId' : ?0}")
    List<UserLocationCollection> findByUserId(String userId);

    @Query("{'userId' : ?0, 'locationId' : ?1}")
    UserLocationCollection findByUserIdAndLocationId(String userId, String locationId);

    @Query("{'locationId' : ?0}")
	List<UserLocationCollection> findByLocationId(String locationId, Pageable pageable);

    @Query("{'locationId' : ?0}")
	List<UserLocationCollection> findByLocationId(String locationId, Sort sort);

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserResourceFavouriteCollection;
import com.dpdocter.enums.Resource;

public interface UserResourceFavouriteRepository extends MongoRepository<UserResourceFavouriteCollection, ObjectId>, PagingAndSortingRepository<UserResourceFavouriteCollection, ObjectId> {

	 @Query("{'userId': ?0, 'resourceId': ?1, 'resourceType': ?2, 'locationId': ?3}")
	UserResourceFavouriteCollection find(ObjectId userId, ObjectId resourceId, String resourceType, ObjectId locationId);

}

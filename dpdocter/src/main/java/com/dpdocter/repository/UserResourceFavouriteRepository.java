package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserResourceFavouriteCollection;

public interface UserResourceFavouriteRepository extends MongoRepository<UserResourceFavouriteCollection, ObjectId>, PagingAndSortingRepository<UserResourceFavouriteCollection, ObjectId> {

	UserResourceFavouriteCollection findByUserIdAndResourceIdAndResourceTypeAndLocationId(ObjectId userId, ObjectId resourceId, String resourceType, ObjectId locationId);

	 @Query(value = "{'resourceId' : ?0, 'resourceType': ?1, 'locationId': ?2, 'userId' : ?3, 'discarded' : ?4}", count = true)
	Integer findCount(ObjectId resourceId, String resourceType, ObjectId locationId, ObjectId userId, boolean discarded);

}

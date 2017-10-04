package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.RankingCountCollection;

public interface RankingCountRepository extends MongoRepository<RankingCountCollection, ObjectId>, PagingAndSortingRepository<RankingCountCollection, ObjectId> {

	@Query("{'resourceId': ?0, 'locationId': ?1, 'resourceType': ?2}")
	RankingCountCollection findByResourceIdAndLocationId(ObjectId resourceId, ObjectId locationId, String resourceType);

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.RankingCountCollection;

public interface RankingCountRepository extends MongoRepository<RankingCountCollection, ObjectId>, PagingAndSortingRepository<RankingCountCollection, ObjectId> {

	RankingCountCollection findByResourceIdAndLocationIdAndResourceType(ObjectId resourceId, ObjectId locationId, String resourceType);

}

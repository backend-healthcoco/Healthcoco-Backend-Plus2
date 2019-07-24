package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AdviceCollection;

public interface AdviceRepository
		extends MongoRepository<AdviceCollection, ObjectId>, PagingAndSortingRepository<AdviceCollection, ObjectId> {
	@Query("{'id':?0,'discarded':false}")
	AdviceCollection findById(ObjectId adviceId);
}

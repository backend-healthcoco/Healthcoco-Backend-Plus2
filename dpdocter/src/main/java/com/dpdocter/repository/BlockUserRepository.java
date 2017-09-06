package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.BlockUserCollection;

public interface BlockUserRepository extends MongoRepository<BlockUserCollection, ObjectId> {
	@Query("{'userIds':?0}")
	BlockUserCollection findByUserId(ObjectId userId);

}

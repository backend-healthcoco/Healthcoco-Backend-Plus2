package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.BlockUserCollection;

public interface BlockUserRepository extends MongoRepository<BlockUserCollection, ObjectId> {

	BlockUserCollection findByUserIds(ObjectId userId);

}

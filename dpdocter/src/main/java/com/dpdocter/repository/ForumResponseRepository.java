package com.dpdocter.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ForumResponseCollection;

public interface ForumResponseRepository extends MongoRepository<ForumResponseCollection, ObjectId>{

	Optional<ForumResponseCollection> findByIdAndUserId(ObjectId id, ObjectId userId);

}

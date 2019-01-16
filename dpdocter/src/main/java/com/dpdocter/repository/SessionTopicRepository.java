package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.SessionTopicCollection;

public interface SessionTopicRepository extends MongoRepository<SessionTopicCollection, ObjectId> {
	@Query("{'topic': {'$in': ?0}}")
	List<SessionTopicCollection> findByTopic(List<String> topics);

	@Query("{'id': {'$in': ?0}}")
	List<SessionTopicCollection> findByids(List<ObjectId> topics);

}

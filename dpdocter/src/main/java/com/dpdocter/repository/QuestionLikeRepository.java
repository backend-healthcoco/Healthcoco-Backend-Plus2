package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.QuestionLikeCollection;

public interface QuestionLikeRepository extends MongoRepository<QuestionLikeCollection, ObjectId> {

	@Query("{'questionId': ?0,'userId': ?1}")
	public QuestionLikeCollection findbyQuestionAndUserId(ObjectId questionId, ObjectId userId);
}

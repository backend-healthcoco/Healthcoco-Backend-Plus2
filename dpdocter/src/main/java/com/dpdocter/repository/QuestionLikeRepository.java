package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.QuestionLikeCollection;

public interface QuestionLikeRepository extends MongoRepository<QuestionLikeCollection, ObjectId> {

	public QuestionLikeCollection findByQuestionIdAndUserId(ObjectId questionId, ObjectId userId);
}

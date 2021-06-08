package com.dpdocter.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CommentCollection;
import com.dpdocter.collections.ForumResponseCollection;

public interface CommentRepository extends MongoRepository<CommentCollection,ObjectId>{

	CommentCollection findByIdAndUserId(ObjectId id, ObjectId userId);

}

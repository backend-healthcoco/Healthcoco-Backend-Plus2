package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.BlogLikesCollection;

public interface BlogLikesRepository extends MongoRepository<BlogLikesCollection, ObjectId> {
	@Query("{'blogId' : ?0, 'userId' : ?1}")
	public BlogLikesCollection findbyBlogIdAndUserId(ObjectId blogId, ObjectId userId);

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.BlogLikesCollection;

@Repository
public interface BlogLikesRepository extends MongoRepository<BlogLikesCollection, ObjectId> {

	public BlogLikesCollection findByBlogIdAndUserId(ObjectId blogId, ObjectId userId);

}

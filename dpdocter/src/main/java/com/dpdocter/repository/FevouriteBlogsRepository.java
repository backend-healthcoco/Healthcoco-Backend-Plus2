package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.FavouriteBlogsCollection;

@Repository
public interface FevouriteBlogsRepository extends MongoRepository<FavouriteBlogsCollection, ObjectId>,
		PagingAndSortingRepository<FavouriteBlogsCollection, ObjectId> {
	@Query("{'blogId': ?0,'userId': ?1}")
	public FavouriteBlogsCollection findbyBlogIdAndUserId(ObjectId blogId, ObjectId userId);

	
}

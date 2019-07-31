package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.FavouriteBlogsCollection;

@Repository
public interface FevouriteBlogsRepository extends MongoRepository<FavouriteBlogsCollection, ObjectId>,
		PagingAndSortingRepository<FavouriteBlogsCollection, ObjectId> {

	public FavouriteBlogsCollection findByBlogIdAndUserId(ObjectId blogId, ObjectId userId);

	
}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.EmailTrackCollection;

public interface EmailTrackRepository extends MongoRepository<EmailTrackCollection, ObjectId>, PagingAndSortingRepository<EmailTrackCollection, ObjectId> {

}

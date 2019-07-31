package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.IssueTrackCollection;

public interface IssueTrackRepository extends MongoRepository<IssueTrackCollection, ObjectId>, PagingAndSortingRepository<IssueTrackCollection, ObjectId> {

}

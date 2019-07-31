package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ResumeCollection;

public interface ResumeRepository extends MongoRepository<ResumeCollection, ObjectId>, PagingAndSortingRepository<ResumeCollection, ObjectId> {

}

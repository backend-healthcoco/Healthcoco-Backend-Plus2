package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PresentComplaintHistoryCollection;

public interface PresentComplaintHistoryRepository extends MongoRepository<PresentComplaintHistoryCollection, ObjectId> , PagingAndSortingRepository<PresentComplaintHistoryCollection, ObjectId>{

}

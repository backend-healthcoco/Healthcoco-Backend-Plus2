package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.MenstrualHistoryCollection;

public interface MenstrualHistoryRepository extends MongoRepository<MenstrualHistoryCollection, ObjectId>,PagingAndSortingRepository<MenstrualHistoryCollection, ObjectId>{

}

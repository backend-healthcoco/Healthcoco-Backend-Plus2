package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ObservationCollection;

public interface ObservationRepository extends MongoRepository<ObservationCollection, ObjectId>, PagingAndSortingRepository<ObservationCollection, ObjectId> {

}

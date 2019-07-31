package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.InvestigationCollection;

public interface InvestigationRepository extends MongoRepository<InvestigationCollection, ObjectId>, PagingAndSortingRepository<InvestigationCollection, ObjectId> {

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.SuggestionCollection;

public interface SuggestionRepository extends MongoRepository<SuggestionCollection, ObjectId>,
		PagingAndSortingRepository<SuggestionCollection, ObjectId> {

}

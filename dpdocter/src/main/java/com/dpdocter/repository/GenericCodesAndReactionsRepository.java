package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.GenericCodesAndReactionsCollection;

public interface GenericCodesAndReactionsRepository extends MongoRepository<GenericCodesAndReactionsCollection, ObjectId>, PagingAndSortingRepository<GenericCodesAndReactionsCollection, ObjectId> {

	@Query("{'codes': {$in : ?0}}")
	GenericCodesAndReactionsCollection find(List<String> codes);

	@Query("{'genericCode': {$in : ?0}}")
	List<GenericCodesAndReactionsCollection> findbyGenericCodes(List<String> asList);

	@Query("{'$or': [{'codes.reaction': ?0}, {'codes.reaction': { $exists: false}}]}")
	List<GenericCodesAndReactionsCollection> findByReaction(String string);
	
	  

}

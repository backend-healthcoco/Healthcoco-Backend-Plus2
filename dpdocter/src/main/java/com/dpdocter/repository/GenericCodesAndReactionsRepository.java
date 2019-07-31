package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.GenericCodesAndReactionsCollection;

public interface GenericCodesAndReactionsRepository extends MongoRepository<GenericCodesAndReactionsCollection, ObjectId>, PagingAndSortingRepository<GenericCodesAndReactionsCollection, ObjectId> {

	List<GenericCodesAndReactionsCollection> findByGenericCodeIn(List<String> asList);

}

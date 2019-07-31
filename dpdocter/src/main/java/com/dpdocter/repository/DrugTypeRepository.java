package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, ObjectId>, PagingAndSortingRepository<DrugTypeCollection, ObjectId> {

}

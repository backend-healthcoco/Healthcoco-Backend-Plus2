package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDirectionCollection;

public interface DrugDirectionRepository extends MongoRepository<DrugDirectionCollection, ObjectId>, PagingAndSortingRepository<DrugDirectionCollection, ObjectId> {

}

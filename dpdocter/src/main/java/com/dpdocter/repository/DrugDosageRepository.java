package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDosageCollection;

public interface DrugDosageRepository extends MongoRepository<DrugDosageCollection, ObjectId>, PagingAndSortingRepository<DrugDosageCollection, ObjectId> {

}

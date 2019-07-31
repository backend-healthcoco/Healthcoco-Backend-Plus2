package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugDurationUnitCollection;

public interface DrugDurationUnitRepository
	extends MongoRepository<DrugDurationUnitCollection, ObjectId>, PagingAndSortingRepository<DrugDurationUnitCollection, ObjectId> {

}

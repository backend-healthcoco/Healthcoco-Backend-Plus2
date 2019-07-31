package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DrugStrengthUnitCollection;

public interface DrugStrengthUnitRepository
	extends MongoRepository<DrugStrengthUnitCollection, ObjectId>, PagingAndSortingRepository<DrugStrengthUnitCollection, ObjectId> {

}

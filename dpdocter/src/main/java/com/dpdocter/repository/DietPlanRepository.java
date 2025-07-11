package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DietPlanCollection;

public interface DietPlanRepository extends MongoRepository<DietPlanCollection, ObjectId>, PagingAndSortingRepository<DietPlanCollection, ObjectId> {

}

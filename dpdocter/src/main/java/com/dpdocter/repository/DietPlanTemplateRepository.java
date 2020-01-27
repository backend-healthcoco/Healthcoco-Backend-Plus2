package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DietPlanTemplateCollection;

public interface DietPlanTemplateRepository extends MongoRepository<DietPlanTemplateCollection, ObjectId>, PagingAndSortingRepository<DietPlanTemplateCollection, ObjectId> {

}

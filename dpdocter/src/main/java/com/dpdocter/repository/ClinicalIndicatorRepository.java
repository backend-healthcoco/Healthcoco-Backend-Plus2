package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ClinicalIndicatorCollection;

public interface ClinicalIndicatorRepository extends MongoRepository<ClinicalIndicatorCollection, ObjectId>, PagingAndSortingRepository<ClinicalIndicatorCollection, ObjectId> {

}

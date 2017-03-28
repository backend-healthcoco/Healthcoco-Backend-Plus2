package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DischargeSummaryCollection;

public interface DischargeSummaryRepository extends MongoRepository<DischargeSummaryCollection, ObjectId>{

}

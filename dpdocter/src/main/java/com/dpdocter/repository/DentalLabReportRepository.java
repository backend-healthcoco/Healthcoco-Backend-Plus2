package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalLabReportsCollection;

public interface DentalLabReportRepository extends MongoRepository<DentalLabReportsCollection, ObjectId>{

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalImagingReportsCollection;

public interface DentalImagingReportsRepository extends MongoRepository<DentalImagingReportsCollection, ObjectId>{

}

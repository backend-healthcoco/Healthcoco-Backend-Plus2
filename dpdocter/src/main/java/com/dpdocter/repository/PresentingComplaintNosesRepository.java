package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PresentingComplaintNoseCollection;

public interface PresentingComplaintNosesRepository extends MongoRepository<PresentingComplaintNoseCollection, ObjectId>{

}

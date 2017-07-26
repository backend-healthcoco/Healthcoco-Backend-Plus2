package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PresentingComplaintEarsCollection;

public interface PresentingComplaintEarsRepository extends MongoRepository<PresentingComplaintEarsCollection, ObjectId>{

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PresentingComplaintNoseCollection;

public interface PresentingComplaintNotesRepository extends MongoRepository<PresentingComplaintNoseCollection, ObjectId>{

}

package com.dpdocter.elasticsearch.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PresentingComplaintNotesCollection;

public interface PresentingComplaintNotesRepository extends MongoRepository<PresentingComplaintNotesCollection, ObjectId>{

}

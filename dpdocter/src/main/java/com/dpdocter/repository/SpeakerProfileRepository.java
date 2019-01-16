package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.SpeakerProfileCollection;



public interface SpeakerProfileRepository extends MongoRepository<SpeakerProfileCollection, ObjectId> {

}

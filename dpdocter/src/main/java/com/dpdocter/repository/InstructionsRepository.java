package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InstructionsCollection;

public interface InstructionsRepository extends MongoRepository<InstructionsCollection, ObjectId>{

}

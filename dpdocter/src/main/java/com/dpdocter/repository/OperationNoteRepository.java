package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OperationNoteCollection;

public interface OperationNoteRepository extends MongoRepository<OperationNoteCollection, ObjectId> {

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ImportContactsRequestCollection;

public interface ImportContactsRequestRepository extends MongoRepository<ImportContactsRequestCollection, ObjectId> {

}

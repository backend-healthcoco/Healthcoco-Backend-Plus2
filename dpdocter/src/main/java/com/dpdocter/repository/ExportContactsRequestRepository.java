package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ExportContactsRequestCollection;

public interface ExportContactsRequestRepository extends MongoRepository<ExportContactsRequestCollection, ObjectId> {

}

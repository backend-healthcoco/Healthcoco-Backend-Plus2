package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ExportContactsRequestCollection;

public interface ExportContactsRequestRepository extends MongoRepository<ExportContactsRequestCollection, String> {

}

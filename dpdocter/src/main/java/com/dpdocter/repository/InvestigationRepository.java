package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InvestigationCollection;

public interface InvestigationRepository extends MongoRepository<InvestigationCollection, String> {

}

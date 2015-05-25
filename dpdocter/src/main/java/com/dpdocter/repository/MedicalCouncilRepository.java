package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MedicalCouncilCollection;

public interface MedicalCouncilRepository extends MongoRepository<MedicalCouncilCollection, String> {

}

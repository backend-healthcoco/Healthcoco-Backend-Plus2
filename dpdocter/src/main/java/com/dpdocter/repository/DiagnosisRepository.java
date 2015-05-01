package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DiagnosisCollection;

public interface DiagnosisRepository extends MongoRepository<DiagnosisCollection, String> {

}

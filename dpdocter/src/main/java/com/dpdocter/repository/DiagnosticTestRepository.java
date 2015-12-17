package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DiagnosticTestCollection;

public interface DiagnosticTestRepository extends MongoRepository<DiagnosticTestCollection, String> {

}

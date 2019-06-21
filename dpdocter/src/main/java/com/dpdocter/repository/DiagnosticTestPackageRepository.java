package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagnosticTestPackageCollection;

public interface DiagnosticTestPackageRepository extends MongoRepository<DiagnosticTestPackageCollection, ObjectId>, PagingAndSortingRepository<DiagnosticTestPackageCollection, ObjectId> {

}

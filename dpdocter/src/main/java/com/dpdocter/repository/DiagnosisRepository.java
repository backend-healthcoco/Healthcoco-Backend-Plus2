package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DiagnosisCollection;

public interface DiagnosisRepository extends MongoRepository<DiagnosisCollection, ObjectId>, PagingAndSortingRepository<DiagnosisCollection, ObjectId> {

}

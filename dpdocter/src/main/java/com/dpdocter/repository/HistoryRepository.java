package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DiseasesCollection;

public interface HistoryRepository extends MongoRepository<DiseasesCollection, String> {

}

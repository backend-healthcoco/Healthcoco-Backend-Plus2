package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugDirectionCollection;

public interface DrugDirectionRepository extends MongoRepository<DrugDirectionCollection, String> {

}

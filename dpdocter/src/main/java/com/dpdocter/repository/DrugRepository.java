package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugCollection;

public interface DrugRepository extends MongoRepository<DrugCollection, String> {

}

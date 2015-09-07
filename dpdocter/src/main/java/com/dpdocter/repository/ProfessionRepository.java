package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ProfessionCollection;

public interface ProfessionRepository extends MongoRepository<ProfessionCollection, String> {

}

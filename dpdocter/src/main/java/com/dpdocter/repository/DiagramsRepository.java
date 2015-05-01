package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DiagramsCollection;

public interface DiagramsRepository extends MongoRepository<DiagramsCollection, String> {

}

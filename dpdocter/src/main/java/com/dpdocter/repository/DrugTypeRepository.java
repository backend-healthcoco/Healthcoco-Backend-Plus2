package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DrugTypeCollection;

public interface DrugTypeRepository extends MongoRepository<DrugTypeCollection, String> {

}

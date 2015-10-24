package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AcosCollection;

public interface AcosRepository extends MongoRepository<AcosCollection, String> {

}

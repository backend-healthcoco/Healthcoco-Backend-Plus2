package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ArosAcosCollection;

public interface ArosAcosRepository extends MongoRepository<ArosAcosCollection, String> {

    @Query(value = "{'arosId' : ?0}")
    ArosAcosCollection findByArosId(String arosId);

}

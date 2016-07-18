package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ArosAcosCollection;

public interface ArosAcosRepository extends MongoRepository<ArosAcosCollection, ObjectId> {

    @Query(value = "{'arosId' : ?0}")
    ArosAcosCollection findByArosId(ObjectId arosId);

}

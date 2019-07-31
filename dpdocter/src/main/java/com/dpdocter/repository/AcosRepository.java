package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AcosCollection;

public interface AcosRepository extends MongoRepository<AcosCollection, ObjectId> {

}

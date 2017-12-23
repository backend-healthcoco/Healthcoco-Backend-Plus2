package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ImplantCollection;

public interface ImplantRepository extends MongoRepository<ImplantCollection, ObjectId> {

}

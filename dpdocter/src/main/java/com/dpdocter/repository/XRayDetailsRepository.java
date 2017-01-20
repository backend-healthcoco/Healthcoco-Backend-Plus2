package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.XRayDetailsCollection;

public interface XRayDetailsRepository extends MongoRepository<XRayDetailsCollection, ObjectId> {

}

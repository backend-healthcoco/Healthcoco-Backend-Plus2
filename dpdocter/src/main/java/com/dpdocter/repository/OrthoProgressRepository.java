package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OrthoProgressCollection;

public interface OrthoProgressRepository extends MongoRepository<OrthoProgressCollection, ObjectId> {

	OrthoProgressCollection findByPlanId(ObjectId id);

}

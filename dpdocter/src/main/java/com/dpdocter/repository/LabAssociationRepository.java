package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.LabAssociationCollection;

public interface LabAssociationRepository extends MongoRepository<LabAssociationCollection, ObjectId> {

	@Query("{'parentLabId': ?0,'daughterLabId': ?1}")
	public LabAssociationCollection findbyParentIdandDaughterId(ObjectId parentLabId, ObjectId daughterLabId);
	
}

package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.CollectionBoyLabAssociationCollection;

public interface CollectionBoyLabAssociationRepository extends MongoRepository<CollectionBoyLabAssociationCollection, ObjectId>{

	@Query("{'collectionBoyId' : ?0, 'parentLabId': ?1,'daughterLabId': ?2}")
	public CollectionBoyLabAssociationCollection findbyParentIdandDaughterId(ObjectId collectionBoyId , ObjectId parentLabId, ObjectId daughterLabId);
	
	@Query("{ 'parentLabId': ?0,'daughterLabId': ?1}")
	public CollectionBoyLabAssociationCollection findbyParentIdandDaughterId( ObjectId parentLabId, ObjectId daughterLabId);
	
	@Query("{'collectionBoyId' : ?0}")
	public List<CollectionBoyLabAssociationCollection> findAllAssociationByCollectionBoyId(ObjectId collectionBoyId);
	
}

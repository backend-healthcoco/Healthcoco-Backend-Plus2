package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.CollectionBoyLabAssociationCollection;

public interface CollectionBoyLabAssociationRepository extends MongoRepository<CollectionBoyLabAssociationCollection, ObjectId>{

	public CollectionBoyLabAssociationCollection findByCollectionBoyIdAndParentLabIdAndDaughterLabId(ObjectId collectionBoyId , ObjectId parentLabId, ObjectId daughterLabId);
	
	public CollectionBoyLabAssociationCollection findByParentLabIdAndDaughterLabId( ObjectId parentLabId, ObjectId daughterLabId);
	
	public CollectionBoyLabAssociationCollection findByParentLabIdAndDaughterLabIdAndIsActive( ObjectId parentLabId, ObjectId daughterLabId , Boolean isActive);
	
	public List<CollectionBoyLabAssociationCollection> findByCollectionBoyId(ObjectId collectionBoyId);
	
}

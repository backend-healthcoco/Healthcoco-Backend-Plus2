package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.InventoryItemCollection;
import com.dpdocter.collections.InventorySettingsCollection;

public interface InventoryItemRepository extends MongoRepository<InventoryItemCollection, ObjectId>{

	@Query("{ 'locationId': ?0, 'hospitalId': ?1 , 'resourceId': ?2}")
    public InventoryItemCollection findByLocationIdHospitalIdResourceId( ObjectId locationId, ObjectId hospitalId ,ObjectId resourceId);
	
	
}

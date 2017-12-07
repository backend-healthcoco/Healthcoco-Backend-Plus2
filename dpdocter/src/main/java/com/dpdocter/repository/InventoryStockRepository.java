package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.InventoryStockCollection;

public interface InventoryStockRepository extends MongoRepository<InventoryStockCollection, ObjectId>{

	@Query("{'itemId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	public InventoryStockCollection getByResourceIdLocationIdHospitalId(String resourceId,String locationId,String hospitalId);
	
}
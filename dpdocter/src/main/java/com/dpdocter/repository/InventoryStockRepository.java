package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.InventoryItemCollection;
import com.dpdocter.collections.InventoryStockCollection;

public interface InventoryStockRepository extends MongoRepository<InventoryStockCollection, ObjectId>{


	@Query("{'itemId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	public InventoryStockCollection getByResourceIdLocationIdHospitalId(String resourceId,String locationId,String hospitalId);
	
	
	@Query("{ 'locationId': ?0, 'hospitalId': ?1 , 'resourceId': ?2 , 'invoiceId': ?3 }")
    public InventoryStockCollection findByLocationIdHospitalIdResourceIdInvoiceId( ObjectId locationId, ObjectId hospitalId ,ObjectId resourceId , ObjectId invoiceId);
	
}
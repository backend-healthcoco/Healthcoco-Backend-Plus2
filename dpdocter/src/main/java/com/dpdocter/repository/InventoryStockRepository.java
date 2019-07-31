package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InventoryStockCollection;

public interface InventoryStockRepository extends MongoRepository<InventoryStockCollection, ObjectId>{

	
	public List<InventoryStockCollection> findByLocationIdAndHospitalIdAndResourceIdAndInvoiceId( ObjectId locationId, ObjectId hospitalId ,String resourceId , ObjectId invoiceId);

}
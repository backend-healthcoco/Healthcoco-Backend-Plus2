package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InventoryItemCollection;

public interface InventoryItemRepository extends MongoRepository<InventoryItemCollection, ObjectId>{

	public InventoryItemCollection findByLocationIdAndHospitalIdAndResourceId( ObjectId locationId, ObjectId hospitalId ,String resourceId);
}

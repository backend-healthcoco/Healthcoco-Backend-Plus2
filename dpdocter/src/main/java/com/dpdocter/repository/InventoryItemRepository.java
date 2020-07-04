package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InventoryItemCollection;

public interface InventoryItemRepository extends MongoRepository<InventoryItemCollection, ObjectId>{

	public List<InventoryItemCollection> findByLocationIdAndHospitalIdAndResourceId( ObjectId locationId, ObjectId hospitalId ,String resourceId);

	public List<InventoryItemCollection> findByLocationIdAndHospitalIdAndName(ObjectId objectId, ObjectId objectId2,
			String drugName);
}

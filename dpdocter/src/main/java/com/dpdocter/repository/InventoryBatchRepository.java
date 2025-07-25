package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.InventoryBatchCollection;

public interface InventoryBatchRepository extends MongoRepository<InventoryBatchCollection, ObjectId>{

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.MedicineOrderCollection;

public interface MedicineOrderRepository extends MongoRepository<MedicineOrderCollection, ObjectId>{

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TrackingOrderCollection;

public interface TrackingOrderRepository extends MongoRepository<TrackingOrderCollection, ObjectId>{

}

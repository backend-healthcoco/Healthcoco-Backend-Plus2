package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SubscriptionCollection;

@Repository
public interface SubscriptionRepository extends MongoRepository<SubscriptionCollection, ObjectId> {

	SubscriptionCollection findByDoctorId(ObjectId doctorId);

}
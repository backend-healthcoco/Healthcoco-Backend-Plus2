package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PushNotificationCollection;

public interface PushNotificationRepository extends MongoRepository<PushNotificationCollection, ObjectId> {

}

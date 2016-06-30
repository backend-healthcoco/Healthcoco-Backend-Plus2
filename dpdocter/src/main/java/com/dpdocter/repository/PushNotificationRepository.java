package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PushNotificationCollection;

public interface PushNotificationRepository extends MongoRepository<PushNotificationCollection, String> {

}

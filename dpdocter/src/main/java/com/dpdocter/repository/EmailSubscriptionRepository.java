package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.EmailSubscriptionCollection;

public interface EmailSubscriptionRepository extends MongoRepository<EmailSubscriptionCollection, ObjectId> {

	public EmailSubscriptionCollection findBySubscriberId(ObjectId subscriberId);

}

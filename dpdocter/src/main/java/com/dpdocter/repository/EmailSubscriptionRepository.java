package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.EmailSubscriptionCollection;

public interface EmailSubscriptionRepository extends MongoRepository<EmailSubscriptionCollection, ObjectId> {
	@Query("{'subscriberId': ?0}")
	public EmailSubscriptionCollection findBySubscriberId(ObjectId subscriberId);

}

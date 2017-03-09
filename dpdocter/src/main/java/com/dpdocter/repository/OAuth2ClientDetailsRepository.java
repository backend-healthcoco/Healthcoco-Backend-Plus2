package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OAuth2ClientDetailsCollection;

@Repository
public interface OAuth2ClientDetailsRepository extends MongoRepository<OAuth2ClientDetailsCollection, ObjectId> {

	@Query("{'clientId': ?0}")
	public OAuth2ClientDetailsCollection getClientDetailById(String clientId);
}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OAuth2ClientDetailsCollection;

@Repository
public interface OAuth2ClientDetailsRepository extends MongoRepository<OAuth2ClientDetailsCollection, ObjectId> {

	public OAuth2ClientDetailsCollection findByClientId(String clientId);
}

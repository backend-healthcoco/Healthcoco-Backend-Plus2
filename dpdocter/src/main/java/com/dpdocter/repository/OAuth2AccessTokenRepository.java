package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;

public interface OAuth2AccessTokenRepository extends MongoRepository<OAuth2AuthenticationAccessTokenCollection, ObjectId> {

	public List<OAuth2AuthenticationAccessTokenCollection> findByClientIdAndUserName(String clientId, String userName);


}

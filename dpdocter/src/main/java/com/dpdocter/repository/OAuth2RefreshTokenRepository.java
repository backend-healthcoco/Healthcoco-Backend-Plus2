package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;

public interface OAuth2RefreshTokenRepository
		extends MongoRepository<OAuth2AuthenticationRefreshTokenCollection, ObjectId> {

	@Query("{'authentication.userAuthentication.details.client_id':?0,    'authentication.userAuthentication.details.username':?1}")
	public List<OAuth2AuthenticationRefreshTokenCollection> findByclientIdAndUserName(String clientId, String mobileNumber);

}

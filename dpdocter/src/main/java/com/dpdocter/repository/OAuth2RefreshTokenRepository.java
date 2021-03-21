package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;

@Repository
public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshTokenCollection, ObjectId>{

	
	public OAuth2AuthenticationRefreshTokenCollection findByTokenId(String tokenId);
	
	@Query("{'authentication.userAuthentication.details.client_id':?0,    'authentication.userAuthentication.details.username':?1}")
	public List<OAuth2AuthenticationRefreshTokenCollection> findByAuthenticationUserAuthenticationDetailsClient_IdAndAuthenticationUserAuthenticationDetailsUsername(String clientId, String mobileNumber);
	
}

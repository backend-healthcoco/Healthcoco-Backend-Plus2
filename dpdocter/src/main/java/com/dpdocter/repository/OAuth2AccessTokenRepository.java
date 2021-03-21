package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;

@Repository
public interface OAuth2AccessTokenRepository extends MongoRepository<OAuth2AuthenticationAccessTokenCollection, ObjectId> {

	public OAuth2AuthenticationAccessTokenCollection findByTokenId(String tokenId);

	public OAuth2AuthenticationAccessTokenCollection findByRefreshToken(String refreshToken);

	public OAuth2AuthenticationAccessTokenCollection findByAuthenticationId(String authenticationId);

	public List<OAuth2AuthenticationAccessTokenCollection> findByClientIdAndUserName(String clientId, String userName);

	public List<OAuth2AuthenticationAccessTokenCollection> findByClientId(String clientId);

}

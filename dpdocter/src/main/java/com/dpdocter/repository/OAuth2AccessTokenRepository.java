package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;

@Repository
public interface OAuth2AccessTokenRepository extends MongoRepository<OAuth2AuthenticationAccessTokenCollection, ObjectId> {

	@Query("{'tokenId': ?0}")
    public OAuth2AuthenticationAccessTokenCollection findByTokenId(String tokenId);

	@Query("{'refreshToken': ?0}")
    public OAuth2AuthenticationAccessTokenCollection findByRefreshToken(String refreshToken);

	@Query("{'authenticationId': ?0}")
    public OAuth2AuthenticationAccessTokenCollection findByAuthenticationId(String authenticationId);

	@Query("{'clientId': ?0 , 'userName': ?1}")
	public List<OAuth2AuthenticationAccessTokenCollection> findByClientIdAndUserName(String clientId, String userName);

	@Query("{'clientId': ?0}")
    public List<OAuth2AuthenticationAccessTokenCollection> findByClientId(String clientId);

}

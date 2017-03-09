package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;

@Repository
public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshTokenCollection, ObjectId>{

	@Query("{'tokenId': ?0}")
	public OAuth2AuthenticationRefreshTokenCollection findByTokenId(String tokenId);
	
}

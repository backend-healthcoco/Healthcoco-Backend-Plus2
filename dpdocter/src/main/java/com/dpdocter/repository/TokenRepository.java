package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.TokenCollection;

public interface TokenRepository extends MongoRepository<TokenCollection, String> {

}

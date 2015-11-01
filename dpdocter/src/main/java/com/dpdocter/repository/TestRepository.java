package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserCollection;

public interface TestRepository extends MongoRepository<UserCollection, String>, TestRepositoryCustom {

}

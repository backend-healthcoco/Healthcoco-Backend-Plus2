package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.UserRoleCollection;

public interface UserRoleRepository extends MongoRepository<UserRoleCollection, String> {
    @Query("{'userId': ?0}")
    public List<UserRoleCollection> findByUserId(String userId);
}

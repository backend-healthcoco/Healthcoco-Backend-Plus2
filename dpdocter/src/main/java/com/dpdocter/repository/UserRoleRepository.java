package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserRoleCollection;

public interface UserRoleRepository extends MongoRepository<UserRoleCollection,String>{

}

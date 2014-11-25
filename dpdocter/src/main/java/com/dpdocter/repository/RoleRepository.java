package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RoleCollection;
@Repository
public interface RoleRepository extends MongoRepository<RoleCollection,String>{
	public RoleCollection findByRole(String role);
}

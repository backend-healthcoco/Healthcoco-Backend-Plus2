package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.RoleCollection;

/**
 * @author veeraj
 */
@Repository
public interface RoleRepository extends MongoRepository<RoleCollection, String> {

    @Query("{'role':?0}")
    public RoleCollection findByRole(String role);

}

package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.UserRoleCollection;

public interface UserRoleRepository extends MongoRepository<UserRoleCollection, String> {
    @Query("{'userId': ?0}")
    public List<UserRoleCollection> findByUserId(String userId);

    @Query("{'userId': {$in: ?0}}")
    public List<UserRoleCollection> findByUserIds(List<String> userIds);

    @Query("{'userId': ?0, 'roleId': {$in: ?1}}")
    public UserRoleCollection findByUserIdAndRoleId(String userId, List<String> roleIds);

    @Query("{'roleId': ?0}")
	public UserRoleCollection findByRoleId(String id);
}

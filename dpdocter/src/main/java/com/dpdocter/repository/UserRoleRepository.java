package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.UserRoleCollection;

public interface UserRoleRepository extends MongoRepository<UserRoleCollection, ObjectId> {
    @Query("{'userId': ?0}")
    public List<UserRoleCollection> findByUserId(ObjectId userId);

    @Query("{'userId': {$in: ?0}}")
    public List<UserRoleCollection> findByUserIds(List<ObjectId> userIds);

    @Query("{'userId': ?0, 'roleId': {$in: ?1}}")
    public UserRoleCollection findByUserIdAndRoleId(ObjectId userId, List<ObjectId> roleIds);

    @Query("{'roleId': ?0}")
	public List<UserRoleCollection> findByRoleId(ObjectId id);
}

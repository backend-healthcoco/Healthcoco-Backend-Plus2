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

	@Query("{'roleId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	public List<UserRoleCollection> findByRoleIdLocationIdHospitalId(ObjectId roleId, ObjectId locationId,
			ObjectId hospitalId);

	@Query("{'userId': ?0, 'locationId': ?1, 'hospitalId': ?2}")
	public UserRoleCollection findByUserIdLocationIdHospitalId(ObjectId userId, ObjectId locationId,
			ObjectId hospitalId);

	@Query("{'userId': ?0, 'locationId': ?1, 'hospitalId': ?2,'roleId': ?3}")
	public UserRoleCollection findByUserIdLocationIdHospitalIdRoleId(ObjectId userId, ObjectId locationId,
			ObjectId hospitalId, ObjectId roleId);

	@Query("{'roleId': ?0}")
	public List<UserRoleCollection> findByRoleId(ObjectId roleId);

    @Query("{'userId': ?0, 'locationId': ?1}")
	public UserRoleCollection findByUserIdLocationId(ObjectId doctorObjectId, ObjectId locationObjectId);

}

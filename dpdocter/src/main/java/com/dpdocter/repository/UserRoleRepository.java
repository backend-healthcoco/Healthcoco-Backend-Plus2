package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.UserRoleCollection;

public interface UserRoleRepository extends MongoRepository<UserRoleCollection, ObjectId> {
	
	public List<UserRoleCollection> findByRoleIdAndLocationIdAndHospitalId(ObjectId roleId, ObjectId locationId,
			ObjectId hospitalId);

	public UserRoleCollection findByUserIdAndLocationIdAndHospitalId(ObjectId userId, ObjectId locationId,
			ObjectId hospitalId);

	public List<UserRoleCollection> findByRoleId(ObjectId roleId);

	public UserRoleCollection findByUserIdAndLocationId(ObjectId doctorObjectId, ObjectId locationObjectId);

}

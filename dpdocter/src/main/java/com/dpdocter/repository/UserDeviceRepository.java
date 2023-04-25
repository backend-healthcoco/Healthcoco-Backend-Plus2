package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.UserDeviceCollection;

public interface UserDeviceRepository extends MongoRepository<UserDeviceCollection, ObjectId>,
		PagingAndSortingRepository<UserDeviceCollection, ObjectId> {

	List<UserDeviceCollection> findByUserIds(ObjectId userId);

	UserDeviceCollection findByDeviceId(String deviceId);

	List<UserDeviceCollection> findByRole(String role);

	List<UserDeviceCollection> findByRoleAndDeviceType(String role, String type);

	List<UserDeviceCollection> findByLocaleId(ObjectId localeId);
}

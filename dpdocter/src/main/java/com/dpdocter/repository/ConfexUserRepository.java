package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ConfexUserCollection;

public interface ConfexUserRepository extends MongoRepository<ConfexUserCollection, ObjectId>,
		PagingAndSortingRepository<ConfexUserCollection, ObjectId> {
	@Query("{'mobileNumber':?0}")
	public ConfexUserCollection findAdminByMobileNumber(String mobileNumber);
	
	
	@Query("{'userName':?0}")
	public ConfexUserCollection findAdminByUserName(String userName);

	@Query("{'conferenceId':?0}")
	public List<ConfexUserCollection> findAdminByConferenceId(ObjectId conferenceId);

}

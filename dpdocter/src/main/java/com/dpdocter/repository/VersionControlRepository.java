package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VersionControlCollection;

public interface VersionControlRepository extends MongoRepository<VersionControlCollection, String> {
	
	// @Query("{'appType': ?0 , 'deviceType':?1}")
	    public VersionControlCollection findByAppTypeAndDeviceType(String appType , String deviceType);

}

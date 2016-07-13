package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.VersionControlCollection;
import com.dpdocter.enums.ApplicationType;

public interface VersionControlRepository extends MongoRepository<VersionControlCollection, String> {
	
	 @Query("{'appType': ?0 , 'deviceType':?1}")
	    public VersionControlCollection findByApplicationType(String appType , String deviceType);

}

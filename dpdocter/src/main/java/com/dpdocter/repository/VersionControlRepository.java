package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.VersionControlCollection;

public interface VersionControlRepository extends MongoRepository<VersionControlCollection, String> {

	public VersionControlCollection findByAppTypeAndDeviceType(String appType, String deviceType);

}

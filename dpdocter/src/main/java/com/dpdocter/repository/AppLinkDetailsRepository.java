package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.AppLinkDetailsCollection;

@Repository
public interface AppLinkDetailsRepository extends MongoRepository<AppLinkDetailsCollection, ObjectId> {

	AppLinkDetailsCollection findByMobileNumber(String mobileNumber);
}
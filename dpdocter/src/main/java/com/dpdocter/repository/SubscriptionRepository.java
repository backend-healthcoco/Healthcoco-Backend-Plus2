package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.enums.PackageType;

@Repository
public interface SubscriptionRepository extends MongoRepository<SubscriptionCollection, ObjectId> {

	List<SubscriptionCollection> findByDoctorId(ObjectId doctorId);
	
	List<SubscriptionCollection> findByDoctorIdAndPackageName(ObjectId doctorId,PackageType packageType);

}
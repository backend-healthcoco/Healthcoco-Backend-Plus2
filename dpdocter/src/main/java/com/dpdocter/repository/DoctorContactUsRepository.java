package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorContactUsCollection;

public interface DoctorContactUsRepository extends MongoRepository<DoctorContactUsCollection, ObjectId>{

	List<DoctorContactUsCollection> findByMobileNumber(String mobileNumber);

	DoctorContactUsCollection findByEmailAddressIgnoreCase(String email);
	
}

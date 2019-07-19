package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.OTPCollection;

public interface OTPRepository extends MongoRepository<OTPCollection, ObjectId>, PagingAndSortingRepository<OTPCollection, ObjectId> {
    @Query("{'mobileNumber' : ?0, 'otpNumber' : ?1, 'generatorId':?2}")
    OTPCollection findById(String mobileNumber, String otpNumber, String generatorId);

    @Query("{'state' : {'$ne' : ?0}}")
    List<OTPCollection> findNonExpiredOtp(String expired);
}

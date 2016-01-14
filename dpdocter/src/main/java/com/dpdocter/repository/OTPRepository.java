package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.OTPCollection;
import com.dpdocter.enums.OTPState;

public interface OTPRepository extends MongoRepository<OTPCollection, String>, PagingAndSortingRepository<OTPCollection, String> {
    @Query("{'mobileNumber' : ?0, 'otpNumber' : ?1}")
    OTPCollection findOne(String mobileNumber, String otpNumber);

    @Query("{'state' : {'$ne' : ?0}}")
	List<OTPCollection> findNonExpiredOtp(OTPState expired);
}

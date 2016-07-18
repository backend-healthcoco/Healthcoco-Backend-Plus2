package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorOTPCollection;

public interface DoctorOTPRepository extends MongoRepository<DoctorOTPCollection, ObjectId>, PagingAndSortingRepository<DoctorOTPCollection, ObjectId> {

    @Query("{'userLocationId': ?0, 'patientId': ?1}")
    List<DoctorOTPCollection> find(ObjectId userLocationId, ObjectId patientId, Pageable pageRequest);

}

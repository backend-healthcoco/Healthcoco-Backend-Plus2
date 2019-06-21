package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorLabFavouriteDoctorCollection;

@Repository
public interface DoctorLabFevouriteDoctorRepository
		extends MongoRepository<DoctorLabFavouriteDoctorCollection, ObjectId> {

}

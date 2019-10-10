package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.DoctorLabFavouriteDoctorCollection;

@Repository
public interface DoctorLabFevouriteDoctorRepository
		extends MongoRepository<DoctorLabFavouriteDoctorCollection, ObjectId> {

	DoctorLabFavouriteDoctorCollection findByDoctorIdAndLocationIdAndHospitalIdAndFavouriteDoctorIdAndFavouriteLocationIdAndFavouriteHospitalIdAndDiscarded(
			ObjectId objectId, ObjectId objectId2, ObjectId objectId3, ObjectId objectId4, ObjectId objectId5,
			ObjectId objectId6, boolean b);

}

package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.DentalWorksAmountCollection;

public interface DentalWorksAmountRepository  extends MongoRepository<DentalWorksAmountCollection, ObjectId>{

	@Query("{'doctorId' :?0 , 'locationId' :?1 , 'hospitalId' :?2 , 'dentalLabLocationId' : ?3 , 'dentalLabHospitalId' :?4}")
	public DentalWorksAmountCollection findByDoctorIdLocationIdHospitalIdDentalLabLocationIdDentalLabHospitalId(ObjectId doctorId, ObjectId locationId,ObjectId hospitalId,ObjectId dentalLabLocationId,ObjectId dentalLabHospitalId);

}

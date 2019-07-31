package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DentalWorksAmountCollection;

public interface DentalWorksAmountRepository  extends MongoRepository<DentalWorksAmountCollection, ObjectId>{

	public DentalWorksAmountCollection findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(ObjectId doctorId, ObjectId locationId,ObjectId hospitalId,ObjectId dentalLabLocationId,ObjectId dentalLabHospitalId);

}

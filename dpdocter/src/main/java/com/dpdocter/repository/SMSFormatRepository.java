package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SMSFormatCollection;

@Repository
public interface SMSFormatRepository extends MongoRepository<SMSFormatCollection, ObjectId> {
	
	    SMSFormatCollection findByDoctorIdAndLocationIdAndHospitalIdAndType(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String type);

	    List<SMSFormatCollection> findByDoctorIdAndLocationIdAndHospitalId(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

}

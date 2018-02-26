package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.SharedReportCollection;

@Repository
public interface SharedReportRepository extends MongoRepository<SharedReportCollection, ObjectId> {

	@Query("{'doctorId':?0,'locationId': ?1,'hospitalId': ?2,'patientId': ?3,'reportId': ?4}")
	SharedReportCollection findbyDoctorIdLocationIdHospitalIdAndreportId(ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, ObjectId patientId, ObjectId reportId);
}

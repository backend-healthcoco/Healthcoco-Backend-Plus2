package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.DoctorCalendarViewCollection;

public interface DoctorCalendarViewRepository extends MongoRepository<DoctorCalendarViewCollection,ObjectId>{

	DoctorCalendarViewCollection findByDoctorIdAndLocationId(ObjectId doctorId, ObjectId locationId);

}

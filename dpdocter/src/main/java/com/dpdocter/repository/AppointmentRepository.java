package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AppointmentCollection;

public interface AppointmentRepository extends MongoRepository<AppointmentCollection, ObjectId>,
		PagingAndSortingRepository<AppointmentCollection, ObjectId> {

	@Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'time.fromTime': {'$lte': ?2}, 'time.toTime': {'$gt': ?2}, 'fromDate': {'$gte': ?4}, 'toDate': {'$lte': ?5}, 'state': {'$ne' : ?6}},{'doctorId': ?0, 'locationId': ?1, 'time.fromTime': {'$lt': ?3}, 'time.toTime': {'$gte': ?3}, 'fromDate': {'$gte': ?4}, 'toDate': {'$lte': ?5}, 'state': {'$ne' : ?6}}]}")
	AppointmentCollection findAppointmentbyUserLocationIdTimeDate(ObjectId doctorId, ObjectId locationId, int from,
			int to, Date fromDate, Date toDate, String state);

	AppointmentCollection findByAppointmentId(String appointmentId);

	@Query("{'fromDate': {'$gte': ?0}, 'toDate': {'$lte': ?1}, 'state': {'$eq' : 'CONFIRM'}}")
	List<AppointmentCollection> findConfirmAppointments(DateTime start, DateTime end, Sort sort);

	AppointmentCollection findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndAppointmentId(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, ObjectId patientObjectId, String appointmentId);
	
	AppointmentCollection findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndTimeFromTimeAndTimeToTimeAndFromDateAndToDate(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, ObjectId patientObjectId, int from, int to, Date fromDate, Date toDate);

}

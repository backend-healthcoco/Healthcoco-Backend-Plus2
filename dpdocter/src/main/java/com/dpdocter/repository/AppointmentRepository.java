package com.dpdocter.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AppointmentCollection;

public interface AppointmentRepository extends MongoRepository<AppointmentCollection, String>, PagingAndSortingRepository<AppointmentCollection, String> {

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'time.from': {'$lte': ?2}, 'time.to': {'$gt': ?2}, 'fromDate': {'$gte': ?4}, 'toDate': {'$lte': ?5}, 'state': {'$ne' : ?6}},{'doctorId': ?0, 'locationId': ?1, 'time.from': {'$lt': ?3}, 'time.to': {'$gte': ?3}, 'fromDate': {'$gte': ?4}, 'toDate': {'$lte': ?5}, 'state': {'$ne' : ?6}}]}")
    AppointmentCollection findAppointmentbyUserLocationIdTimeDate(String doctorId, String locationId, int from, int to, Date fromDate, Date toDate, String state);

    @Query("{'appointmentId': ?0}")
    AppointmentCollection findByAppointmentId(String appointmentId);

}

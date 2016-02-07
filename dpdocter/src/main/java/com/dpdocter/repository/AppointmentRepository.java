package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.AppointmentCollection;

public interface AppointmentRepository extends MongoRepository<AppointmentCollection, String>, PagingAndSortingRepository<AppointmentCollection, String> {

    @Query("{'$or': [{'doctorId': ?0, 'locationId': ?1, 'time.from': {'$lte': ?2}, 'time.to': {'$gt': ?2}, 'date': ?4, 'state': {'$ne' : ?5}},{'doctorId': ?0, 'locationId': ?1, 'time.from': {'$lt': ?3}, 'time.to': {'$gte': ?3}, 'date': ?4, 'state': {'$ne' : ?5}}]}")
    AppointmentCollection findAppointmentbyUserLocationIdTimeDate(String doctorId, String locationId, int from, int to, Date date, String state);

    @Query("{'appointmentId': ?0}")
    AppointmentCollection findByAppointmentId(String appointmentId);

    @Query("{'locationId': ?0, 'date': {'$gte': ?1, '$lte': ?2}}")
    List<AppointmentCollection> findByLocationId(String locationId, Date from, Date to, Sort sort);

    @Query("{'locationId': ?0, 'patientId': ?1, 'date': {'$gte': ?2, '$lte': ?3}}")
    List<AppointmentCollection> findByLocationIdPatientId(String locationId, String patientId, Date from, Date to, Sort sort);

    @Query("{'locationId': ?0, 'doctorId': {'$in' : ?1}, 'date': {'$gte': ?2, '$lte': ?3}}")
    List<AppointmentCollection> findByLocationIdDoctorId(String locationId, List<String> doctorId, Date from, Date to, Sort sort);

    @Query("{'locationId': ?0, 'doctorId': {'$in' : ?1}, 'patientId': ?2, 'date': {'$gte': ?3, '$lte': ?4}}")
    List<AppointmentCollection> findByLocationIdDoctorIdPatientId(String locationId, List<String> doctorId, String patientId, Date date, Date date2, Sort sort);

}

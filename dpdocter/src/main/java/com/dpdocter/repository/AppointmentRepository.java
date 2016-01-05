package com.dpdocter.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.beans.Timing;
import com.dpdocter.collections.AppointmentCollection;

public interface AppointmentRepository extends MongoRepository<AppointmentCollection, String>, PagingAndSortingRepository<AppointmentCollection, String> {

    @Query("{'time': ?0, 'date': ?1}")
    AppointmentCollection findbyTimeDate(Timing timing, Date date);

    @Query("{'userLocationId': {$in: ?0}}")
    List<AppointmentCollection> findByUserlocationId(Collection<String> userLocationIds, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1}")
    List<AppointmentCollection> findByUserlocationId(Collection<String> userLocationIds, String patientId, Sort sort);
    
    @Query("{'userLocationId': {$in: ?0}, 'month': ?1, 'year': ?2}")
    List<AppointmentCollection> findByUserlocationIdAndMonth(Collection<String> userLocationIds, int month, int year, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'month': ?2, 'year': ?3}")
	List<AppointmentCollection> findByUserlocationIdAndMonth(Collection<String> userLocationIds, String patientId, int month, int year, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'week':?1, 'year': ?2}")
    List<AppointmentCollection> findByUserlocationIdAndWeek(Collection<String> userLocationIds, int week, int year, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'week': ?2, 'year': ?3}")
	List<AppointmentCollection> findByUserlocationIdAndWeek(Collection<String> userLocationIds, String patientId, int week, int year, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'day':?1, 'year': ?2}}")
    List<AppointmentCollection> findByUserlocationIdAndDay(Collection<String> userLocationIds, int day, int year, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'day':?2, 'year': ?3}}")
 	List<AppointmentCollection> findByUserlocationIdAndDay(Collection<String> userLocationIds, String patientId, int day, int year, Sort sort);

    @Query("{'userLocationId': ?0, 'time.from': {'$gte': ?1}, 'time.to': {'$lt': ?2}, 'date': ?3}")
    AppointmentCollection findAppointmentbyUserLocationIdTimeDate(String userLocationId, String from, String to, Date date);

    @Query("{'appointmentId': ?0}")
	AppointmentCollection findByAppointmentId(String appointmentId);

    @Query("{'userLocationId': {$in: ?0}, 'date': {'$gte': ?1, '$lte':?2}}")
	List<AppointmentCollection> findTodaysAppointments(Collection<String> userLocationIds, DateTime start, DateTime end, Pageable pageable);

    @Query("{'userLocationId': {$in: ?0}, 'date': {'$gte': ?1, '$lte':?2}}}")
	List<AppointmentCollection> findTodaysAppointments(Collection<String> userLocationIds, DateTime start, DateTime end, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'date': {'$gte': ?2, '$lte':?3}}}")
	List<AppointmentCollection> findTodaysAppointments(Collection<String> userLocationIds, String patientId, DateTime start, DateTime end, Pageable pageable);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'date': {'$gte': ?2, '$lte':?3}}}")
	List<AppointmentCollection> findTodaysAppointments(Collection<String> userLocationIds, String patientId, DateTime start, DateTime end,	Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'date': {'$gt': ?1}}}")
	List<AppointmentCollection> findFutureAppointments(Collection<String> userLocationIds, DateTime end, Pageable pageable);

    @Query("{'userLocationId': {$in: ?0}, 'date': {'$lt': ?1}}}")
	List<AppointmentCollection> findPastAppointments(Collection<String> userLocationIds, DateTime start,	Pageable pageable);

    @Query("{'userLocationId': {$in: ?0}, 'date': {'$gt': ?1}}}")
	List<AppointmentCollection> findFutureAppointments(Collection<String> userLocationIds, DateTime end, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'date': {'$lt': ?1}}}")
	List<AppointmentCollection> findPastAppointments(Collection<String> userLocationIds, DateTime start, Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'date': {'$gt': ?2}}}")
	List<AppointmentCollection> findFutureAppointments(Collection<String> userLocationIds, String patientId, DateTime end,	Pageable pageable);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'date': {'$lt': ?2}}}")
	List<AppointmentCollection> findPastAppointments(Collection<String> userLocationIds, String patientId, DateTime start, Pageable pageable);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'date': {'$gt': ?2}}}")
	List<AppointmentCollection> findFutureAppointments(Collection<String> userLocationIds, String patientId, DateTime end,	Sort sort);

    @Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'date': {'$lt': ?2}}}")
	List<AppointmentCollection> findPastAppointments(Collection<String> userLocationIds, String patientId, DateTime start, Sort sort);
}

package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
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

	@Query("{'userLocationId': {$in: ?0}, 'updatedTime': {'$gte': ?1}}")
	List<AppointmentCollection> findByUserlocationId(String[] userLocationIds, Date date, Pageable pageable);

	@Query("{'userLocationId': {$in: ?0}, 'month':?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationIdAndMonth(String[] userLocationIds, int month, Date date, Pageable pageable);

	@Query("{'userLocationId': {$in: ?0}, 'week':?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationIdAndWeek(String[] userLocationIds, int week, Date date, Pageable pageable);

	@Query("{'userLocationId': {$in: ?0}, 'day':?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationIdAndDay(String[] userLocationIds, int day, Date date, Pageable pageable);

	@Query("{'userLocationId': {$in: ?0}, 'updatedTime': {'$gte': ?1}}")
	List<AppointmentCollection> findByUserlocationId(String[] userLocationIds, Date date, Sort sort);

	@Query("{'userLocationId': {$in: ?0}, 'month':?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationIdAndMonth(String[] userLocationIds, int month, Date date, Sort sort);

	@Query("{'userLocationId': {$in: ?0}, 'week':?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationIdAndWeek(String[] userLocationIds, int week, Date date, Sort sort);

	@Query("{'userLocationId': {$in: ?0}, 'day':?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationIdAndDay(String[] userLocationIds, int day, Date date, Sort sort);

	@Query("{'userLocationId': ?0, 'time': ?1, 'date': ?2, 'isConfirmed': ?3}")
	AppointmentCollection findConfirmedAppointmentbyUserLocationIdTimeDate(String userLocationId, Timing time, Date date, boolean isConfirmed);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationId(String[] userLocationIds, String patientId, Date date, PageRequest pageRequest);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'month':?2, 'updatedTime': {'$gte': ?3}}")
	List<AppointmentCollection> findByUserlocationIdAndMonth(String[] userLocationIds, String patientId, int month, Date date, PageRequest pageRequest);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'week':?2, 'updatedTime': {'$gte': ?3}}")
	List<AppointmentCollection> findByUserlocationIdAndWeek(String[] userLocationIds, String patientId, int week, Date date, PageRequest pageRequest);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'day':?2, 'updatedTime': {'$gte': ?3}}")
	List<AppointmentCollection> findByUserlocationIdAndDay(String[] userLocationIds, String patientId, int day,	Date date, PageRequest pageRequest);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'updatedTime': {'$gte': ?2}}")
	List<AppointmentCollection> findByUserlocationId(String[] userLocationIds, String patientId, Date date, Sort sort);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'month':?2, 'updatedTime': {'$gte': ?3}}")
	List<AppointmentCollection> findByUserlocationIdAndMonth(String[] userLocationIds, String patientId, int month,	Date date, Sort sort);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'week':?2, 'updatedTime': {'$gte': ?3}}")
	List<AppointmentCollection> findByUserlocationIdAndWeek(String[] userLocationIds, String patientId, int week, Date date, Sort sort);

	@Query("{'userLocationId': {$in: ?0}, 'patientId': ?1, 'day':?2, 'updatedTime': {'$gte': ?3}}")
	List<AppointmentCollection> findByUserlocationIdAndDay(String[] userLocationIds, String patientId, int day, Date date, Sort sort);

}

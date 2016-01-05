package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.EventCollection;

public interface EventRepository extends MongoRepository<EventCollection, String> {

	@Query("{'userLocationId': ?0, 'date': ?1, 'isCalenderBlocked' :?2}")
	List<EventCollection> findByUserLocationId(String userLocationId, Date date, boolean isCalenderBlocked);

}

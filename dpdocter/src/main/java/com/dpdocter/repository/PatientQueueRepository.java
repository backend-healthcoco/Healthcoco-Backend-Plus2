package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PatientQueueCollection;

public interface PatientQueueRepository extends MongoRepository<PatientQueueCollection, ObjectId>, PagingAndSortingRepository<PatientQueueCollection, ObjectId> {

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2, 'date' : {'$gt' : ?3, '$lte' : ?4}, 'discarded':?5}")
    List<PatientQueueCollection> find(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, DateTime start, DateTime end, Boolean discarded, Sort sort);

    @Query("{'appointmentId':?0, 'doctorId':?1,'locationId':?2,'hospitalId':?3, 'date' : {'$gt' : ?4, '$lte' : ?5}, 'discarded':?6}")
    PatientQueueCollection find(String appointmentId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, String patientId, DateTime start, DateTime end,
	    Boolean discarded);

    @Query(value = "{'appointmentId':?0, 'doctorId':?1,'locationId':?2,'hospitalId':?3, 'date' : {'$gt' : ?4, '$lte' : ?5}, 'sequenceNo':?6, 'discarded':?7}", count = true)
    Integer find(String appointmentId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId, DateTime start, DateTime end, Integer sequenceNo, Boolean discarded);

    @Query(value = "{'doctorId':?0,'locationId':?1,'hospitalId':?2, 'date' : {'$gt' : ?3, '$lte' : ?4}, 'sequenceNo': {'$lt' : ?5}, 'discarded':?6}", fields="{'startTime' :1}")
	PatientQueueCollection findById(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, DateTime start, DateTime end, Integer sequenceNo, Boolean discarded);

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2, 'patientId':?3, 'date' : {'$gt' : ?4, '$lte' : ?5}}")
	PatientQueueCollection find(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, ObjectId patientObjectId, DateTime start, DateTime end);

    @Query("{'appointmentId':?0}")
	PatientQueueCollection find(String appointmentId);

}

package com.dpdocter.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.PatientQueueCollection;

public interface PatientQueueRepository extends MongoRepository<PatientQueueCollection, String>, PagingAndSortingRepository<PatientQueueCollection, String> {

    @Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2, 'date' : {'$gt' : ?3, '$lte' : ?4}, 'discarded':?5}")
    List<PatientQueueCollection> find(String doctorId, String locationId, String hospitalId, DateTime start, DateTime end, Boolean discarded, Sort sort);

    @Query("{'appointmentId':?0, 'doctorId':?1,'locationId':?2,'hospitalId':?3, 'date' : {'$gt' : ?4, '$lte' : ?5}, 'discarded':?6}")
    PatientQueueCollection find(String appointmentId, String doctorId, String locationId, String hospitalId, String patientId, DateTime start, DateTime end,
	    Boolean discarded);

    @Query(value = "{'appointmentId':?0, 'doctorId':?1,'locationId':?2,'hospitalId':?3, 'date' : {'$gt' : ?4, '$lte' : ?5}, 'discarded':?6, 'sequenceNo':?7}", count = true)
    Integer find(String appointmentId, String doctorId, String locationId, String hospitalId, String patientId, DateTime start, DateTime end,
	    Integer sequenceNo, Boolean discarded);

}

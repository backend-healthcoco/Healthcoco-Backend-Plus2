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

    List<PatientQueueCollection> findByDoctorIdAndLocationIdAndHospitalIdAndDateBetweenAndDiscarded(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, DateTime start, DateTime end, Boolean discarded, Sort sort);

    @Query(value = "{'appointmentId':?0, 'doctorId':?1,'locationId':?2,'hospitalId':?3, 'date' : {'$gt' : ?4, '$lte' : ?5}, 'sequenceNo':?6, 'discarded':?7}", count = true)
    Integer find(String appointmentId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId, DateTime start, DateTime end, Integer sequenceNo, Boolean discarded);

    PatientQueueCollection findByDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndDateBetween(ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId, ObjectId patientObjectId, DateTime start, DateTime end);

    PatientQueueCollection findByAppointmentId(String appointmentId);

}

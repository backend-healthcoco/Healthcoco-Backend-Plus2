package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientGroupCollection;

@Repository
public interface PatientGroupRepository extends MongoRepository<PatientGroupCollection, String> {
    List<PatientGroupCollection> findByGroupId(String groupId);

    @Query("{'groupId':?0,'patientId':?1}")
    PatientGroupCollection findByGroupIdAndPatientId(String groupId, String patientId);

    @Query("{'patientId':?0}")
    List<PatientGroupCollection> findByPatientId(String patientId);

    @Query("{'patientId': {'$in': ?0}}")
    List<PatientGroupCollection> findByPatientId(List<String> patientIds);

}

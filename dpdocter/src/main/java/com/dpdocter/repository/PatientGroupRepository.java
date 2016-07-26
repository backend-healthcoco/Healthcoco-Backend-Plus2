package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientGroupCollection;

@Repository
public interface PatientGroupRepository extends MongoRepository<PatientGroupCollection, ObjectId> {
    
	@Query("{'groupId':?0}")
	List<PatientGroupCollection> findByGroupId(ObjectId groupId);

    @Query("{'groupId':?0,'patientId':?1, 'discarded':false}")
    PatientGroupCollection findByGroupIdAndPatientId(ObjectId groupId, ObjectId patientId);

    @Query("{'patientId':?0, 'discarded':false}")
    List<PatientGroupCollection> findByPatientId(ObjectId patientId);

    @Query("{'patientId': {'$in': ?0}, 'discarded':false}")
    List<PatientGroupCollection> findByPatientId(List<ObjectId> patientIds);

}

package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PatientGroupCollection;

@Repository
public interface PatientGroupRepository extends MongoRepository<PatientGroupCollection, ObjectId> {
    
	List<PatientGroupCollection> findByGroupId(ObjectId groupId);

    List<PatientGroupCollection> findByPatientIdAndDiscardedIsFalse(ObjectId patientId);

}

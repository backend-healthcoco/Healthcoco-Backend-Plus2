package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.ProvisionalDiagnosisCollection;

public interface ProvisionalDiagnosisRepository extends MongoRepository<ProvisionalDiagnosisCollection, ObjectId>,
		PagingAndSortingRepository<ProvisionalDiagnosisCollection, ObjectId> {

}

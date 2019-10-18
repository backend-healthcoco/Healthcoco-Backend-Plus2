package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.AcadamicClassCollection;

public interface AcadamicClassRepository extends MongoRepository<AcadamicClassCollection, ObjectId> {
	@Query( fields = "{'branchId': ?0 , 'schoolId': ?1}",count = true)
	public Integer countByBranchIdAndSchoolId(ObjectId branchId, ObjectId schoolId);
}

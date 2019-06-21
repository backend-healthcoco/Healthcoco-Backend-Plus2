package com.dpdocter.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorExpenseCollection;

public interface DoctorExpenseRepository extends MongoRepository<DoctorExpenseCollection, ObjectId>,
		PagingAndSortingRepository<DoctorExpenseCollection, ObjectId> {

}

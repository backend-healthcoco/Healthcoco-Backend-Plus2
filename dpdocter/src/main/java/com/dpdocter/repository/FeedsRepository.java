package com.dpdocter.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.FeedsCollection;
import com.dpdocter.collections.ForumResponseCollection;

public interface FeedsRepository extends MongoRepository<FeedsCollection,ObjectId>{

	FeedsCollection findByIdAndPostByDoctorId(ObjectId id,ObjectId doctorId);

}

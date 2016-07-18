package com.dpdocter.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.ProfessionalMembershipCollection;

public interface ProfessionalMembershipRepository extends MongoRepository<ProfessionalMembershipCollection, ObjectId> {

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<ProfessionalMembershipCollection> find(Date date, Pageable pageRequest);

    @Query("{'updatedTime': {'$gt': ?0}}")
    List<ProfessionalMembershipCollection> find(Date date, Sort sort);

}

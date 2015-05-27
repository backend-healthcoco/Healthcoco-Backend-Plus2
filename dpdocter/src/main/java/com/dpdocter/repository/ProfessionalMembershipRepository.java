package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ProfessionalMembershipCollection;

public interface ProfessionalMembershipRepository extends MongoRepository<ProfessionalMembershipCollection, String> {

}

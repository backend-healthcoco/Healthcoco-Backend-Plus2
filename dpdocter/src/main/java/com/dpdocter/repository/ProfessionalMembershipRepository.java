package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.ProfessionalMembershipCollection;

public interface ProfessionalMembershipRepository extends MongoRepository<ProfessionalMembershipCollection, ObjectId> {

	List<ProfessionalMembershipCollection> findByMembershipIn(List<String> membership);

}

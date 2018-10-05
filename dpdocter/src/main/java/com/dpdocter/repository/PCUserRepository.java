package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.PCUserCollection;

/**
 * @author veeraj
 */
@Repository
public interface PCUserRepository extends MongoRepository<PCUserCollection, ObjectId> {
	@Query("{'userName': ?0}")
	public PCUserCollection findByUserName(String userName);

	@Query("{'userName' : ?0,  'role': {$in: ?1}}")
	public PCUserCollection findAdminByUserNameAndRole(String userName, List<String> roles);

	@Query("{'emailAddress' : {$regex : '^?0$', $options : 'i'}}")
	public List<PCUserCollection> findByEmailAddressIgnoreCase(String emailAddress);

	@Query(value = "{'patientId': ?0, 'discarded' : ?1}", count = true)
	Integer getPrescriptionCount(ObjectId patientId, boolean discarded);

	@Query("{'mrCode': ?0}")
	public PCUserCollection findByMRCode(String mrCode);
}

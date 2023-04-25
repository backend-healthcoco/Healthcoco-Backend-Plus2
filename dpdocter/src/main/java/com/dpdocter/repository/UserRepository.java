package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.UserCollection;

/**
 * @author veeraj
 */
@Repository
public interface UserRepository extends MongoRepository<UserCollection, ObjectId> {

	public UserCollection findByUserName(String userName);

	@Query("{'emailAddress' : {$regex : '^?0$', $options : 'i'}}")
	public List<UserCollection> findByEmailAddressIgnoreCase(String emailAddress);

	public UserCollection findByFirstNameAndEmailAddressAndMobileNumberAndUserState(String firstName,
			String emailAddress, String mobileNumber, String userState);

	public List<UserCollection> findByMobileNumberAndUserState(String mobileNumber, String userState);

	public UserCollection findByUserNameAndEmailAddress(String userName, String emailAddress);

	public UserCollection findByIdAndSignedUpNot(ObjectId userId, Boolean signedUp);

}

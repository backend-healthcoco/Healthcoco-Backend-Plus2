package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.UserCollection;
/**
 * @author veeraj
 */
@Repository
public interface UserRepository extends MongoRepository<UserCollection, String>{
	public UserCollection findByUserName(String userName);
	@Query("{'userName':?0,'password':?1}")
	public UserCollection findByUserNameAndPass(String userName,String pasword);
	@Query("{'emailAddress':?0,'password':?1}")
	public UserCollection findByEmailAddressAndPass(String emailAddress,String pasword);
	@Query("{'firstName':?0,'middleName':?1,'lastName':?2,'emailAddress':?3,'phoneNumber':?4}")
	public UserCollection checkPatient(String firstName,String middleName,String lastName,String emailAddress,String phoneNumber);
	public List<UserCollection> findByMobileNumber(String mobileNumber);
	@Query("{'firstName':?0,'lastName':?1,'mobileNumber':?2}")
	public List<UserCollection> findByFirstNameLastNameMobileNumber(String firstName,String lastName,String mobileNumber);
}

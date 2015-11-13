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
public interface UserRepository extends MongoRepository<UserCollection, String> {
    @Query("{'userName': ?0}")
    public UserCollection findByUserName(String userName);


    @Query("{'emailAddress' : {$regex : ?0, $options : 'i'}}")
    public List<UserCollection> findByEmailAddressIgnoreCase(String emailAddress);

    @Query("{'userName':?0,'password':?1}")
    public UserCollection findByUserNameAndPass(String userName, String pasword);


    @Query("{'password' : ?0, 'userName' : {$regex : ?1, $options : 'i'}}")
    public UserCollection findByPasswordAndUserNameIgnoreCase(String password, String userName);

    @Query("{'emailAddress':?0, 'password':?1}")
    public UserCollection findByEmailAddressAndPass(String emailAddress, String pasword);


    @Query("{'password' : ?0, 'emailAddress' : {$regex : ?1, $options : 'i'}}")
    public UserCollection findByPasswordAndEmailAddressIgnoreCase(String pasword, String emailAddress);

    @Query("{'firstName':?0,'middleName':?1,'lastName':?2,'emailAddress':?3,'phoneNumber':?4}")
    public UserCollection checkPatient(String firstName, String middleName, String lastName, String emailAddress, String phoneNumber);

    @Query("{'mobileNumber':?0}")
    public List<UserCollection> findByMobileNumber(String mobileNumber);

    @Query("{'firstName':?0,'lastName':?1,'mobileNumber':?2}")
    public List<UserCollection> findByFirstNameLastNameMobileNumber(String firstName, String lastName, String mobileNumber);

    @Query("{'userName': ?0, 'emailAddress':?1}")
    public UserCollection findByUserNameAndEmailAddress(String userName, String emailAddress);

}

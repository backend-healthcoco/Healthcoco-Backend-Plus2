package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dpdocter.collections.UserCollection;

/**
 * @author veeraj
 */
@Repository
public interface UserRepository extends MongoRepository<UserCollection, ObjectId> {
    @Query("{'userName': ?0}")
    public UserCollection findByUserName(String userName);

    @Query("{'emailAddress' : {$regex : '^?0$', $options : 'i'}}")
    public List<UserCollection> findByEmailAddressIgnoreCase(String emailAddress);

    @Query("{'firstName':?0,'emailAddress':?1,'mobileNumber':?2}")
    public UserCollection checkPatient(String firstName, String emailAddress, String mobileNumber);

    @Query("{'mobileNumber':?0}")
    public List<UserCollection> findByMobileNumber(String mobileNumber);

    @Query("{'userName': ?0, 'emailAddress':?1}")
    public UserCollection findByUserNameAndEmailAddress(String userName, String emailAddress);

    @Query("{'isActive' : ?0}")
	public List<UserCollection> findInactiveDoctors(boolean isActive, Pageable pageRequest);

    @Query("{'isActive' : ?0}")
	public List<UserCollection> findInactiveDoctors(boolean isActive, Sort sort);
    
    @Query("{'mobileNumber' : ?0, 'userState' : ?1}")
	public UserCollection findAdminByMobileNumber(String mobileNumber, String userState);

    @Query("{'id' : ?0, 'signedUp' : ?1}")
	public UserCollection findByIdAndNotSignedUp(ObjectId userId, Boolean signedUp);

}

package com.dpdocter.services;

import com.dpdocter.beans.User;
/**
 * @author veeraj
 */
public interface SignUpService {
	User signUp(User user,String signUpType);
	Boolean activateUser(String userId);
}

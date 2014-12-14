package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.User;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.LoginService;
/**
 * @author veeraj
 */
@Service
public class LoginServiceImpl implements LoginService {
	
	@Autowired
	private UserRepository userRepository;
/**
 * This method is used for login purpose.
 */
	public User login(LoginRequest request) {
		try {
			UserCollection userCollection = userRepository.findByUserNameAndPass(request.getUsername(), request.getPassword());
			if(userCollection == null){
				throw new BusinessException(ServiceError.NotVerified, "Invalid username and Password");
			}
			User user = new User();
			BeanUtil.map(userCollection, user);
			return user;
		} catch(BusinessException be){
			throw be;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while login");
		}
	}

}

package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotPasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailService mailService;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotPasswordSub;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Override
	public void forgotPassword(ForgotPasswordRequest request) {
		try {
			UserCollection userCollection = null;
			if (request.getUsername() != null) {
				userCollection = userRepository.findByUserName(request
						.getUsername());
		    	}
			if (userCollection != null) {
				String body = mailBodyGenerator
						.generateForgotPasswordEmailBody(
								userCollection.getUserName(),
								userCollection.getFirstName(),
								userCollection.getMiddleName(),
								userCollection.getLastName(),
								userCollection.getId());
				/*mailService.sendEmail(userCollection.getEmailAddress(),
						forgotPasswordSub, body, null);*/
			}else{
				throw new BusinessException(ServiceError.Unknown, "User not Found.");
			}

		}catch(BusinessException be){
			throw new BusinessException(ServiceError.Unknown, "User not Found.");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public void resetPassword(ResetPasswordRequest request) {
		try {
			UserCollection userCollection = userRepository.findOne(request.getUserId());
			userCollection.setPassword(request.getPassword());
			userRepository.save(userCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
	}

}

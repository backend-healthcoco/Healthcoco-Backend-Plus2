package com.dpdocter.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotPasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;
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

	public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
		try {
			UserCollection userCollection = null;
			ForgotPasswordResponse response = null;

			if (request.getUsername() != null) {
				userCollection = userRepository.findByUserName(request.getUsername());
			}
			if (userCollection != null) {
				if (userCollection.getEmailAddress().trim().equals(request.getEmailAddress().trim())) {
					String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
							userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId());
					/*mailService.sendEmail(userCollection.getEmailAddress(), forgotPasswordSub, body, null);*/
					response = new ForgotPasswordResponse(userCollection.getUserName(), userCollection.getMobileNumber(), userCollection.getEmailAddress(),
							RoleEnum.DOCTOR);
				} else {
					response = new ForgotPasswordResponse(userCollection.getUserName(), userCollection.getMobileNumber(), userCollection.getEmailAddress(),
							RoleEnum.PATIENT);
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "User not Found.");
			}
			return response;
		} catch (BusinessException be) {
			throw new BusinessException(ServiceError.Unknown, "User not Found.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

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

	public void forgotUsername(ForgotPasswordRequest request) {
		List<UserCollection> userCollection = null;
		try {
			if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
				userCollection = userRepository.findByEmailAddress(request.getEmailAddress());
			} else if (request.getPhoneNumber() != null && !request.getEmailAddress().isEmpty()) {
				userCollection = userRepository.findByMobileNumber(request.getPhoneNumber());
			}

			if (userCollection != null) {
				String body = mailBodyGenerator.generateForgotUsernameEmailBody(userCollection);
			}
		} catch (BusinessException be) {
			throw new BusinessException(ServiceError.Unknown, "User not Found.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

}

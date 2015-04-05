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
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import common.util.web.Response;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailService mailService;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotUsernamePasswordSub;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	public ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request) {
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
					mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);
					response = new ForgotPasswordResponse(userCollection.getUserName(), userCollection.getMobileNumber(), userCollection.getEmailAddress(),
							RoleEnum.DOCTOR);
				} else {
					throw new BusinessException(ServiceError.Unknown, "Email address is empty.");
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

	public Response<Boolean> forgotPasswordForPatient(ForgotUsernamePasswordRequest request) {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(false);
		try {
			UserCollection userCollection = null;

			if (request.getUsername() != null) {
				userCollection = userRepository.findByUserName(request.getUsername());
			}

			if (userCollection != null) {
				if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
					String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
							userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId());
					mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);
					response.setData(true);
				} else if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
					// SMS logic will go here.
					response.setData(true);
				} else {
					throw new BusinessException(ServiceError.Unknown, "Email address or mobile number should be provided");
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "User not Found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	public ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username) {
		try {
			UserCollection userCollection = null;
			ForgotPasswordResponse response = null;

			if (username != null && !username.isEmpty()) {
				userCollection = userRepository.findByUserName(username);
			} else {
				throw new BusinessException(ServiceError.Unknown, "Username cannot be empty");
			}

			if (userCollection != null) {
				response = new ForgotPasswordResponse(username, userCollection.getMobileNumber(), userCollection.getEmailAddress(), RoleEnum.PATIENT);
			} else {
				throw new BusinessException(ServiceError.Unknown, "User not Found.");
			}
			return response;
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

	public Response<Boolean> forgotUsername(ForgotUsernamePasswordRequest request) {
		List<UserCollection> userCollections = null;
		Response<Boolean> response = new Response<Boolean>();
		response.setData(false);
		try {
			if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
				userCollections = userRepository.findByEmailAddress(request.getEmailAddress());
				if (userCollections != null) {
					String body = mailBodyGenerator.generateForgotUsernameEmailBody(userCollections);
					mailService.sendEmail(request.getEmailAddress(), forgotUsernamePasswordSub, body, null);
					response.setData(true);
				}
			} else if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
				userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
				if (userCollections != null) {
					// SMS logic will go here.
					response.setData(true);
				}
			} else {
			}
		} catch (BusinessException be) {
			throw new BusinessException(ServiceError.Unknown, "User not Found.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}

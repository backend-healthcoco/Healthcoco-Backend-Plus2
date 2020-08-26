package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.ConfexUserCollection;
import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;
import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.ConfexUserRepository;
import com.dpdocter.repository.OAuth2AccessTokenRepository;
import com.dpdocter.repository.OAuth2RefreshTokenRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.tokenstore.CustomPasswordEncoder;

import common.util.web.DPDoctorUtils;
import common.util.web.LoginUtils;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

	private static Logger logger = Logger.getLogger(ForgotPasswordServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

	@Autowired
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@Autowired
	private ConfexUserRepository confexUserRepository;

	@Autowired
	private MailService mailService;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotUsernamePasswordSub;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private CustomPasswordEncoder passwordEncoder;

	@Autowired
	private TokenRepository tokenRepository;

	@Value(value = "${forgot.password.valid.time.in.mins}")
	private String forgotPasswordValidTime;

	@Value(value = "${mail.resetPasswordSuccess.subject}")
	private String resetPasswordSub;

	@Value(value = "${ForgotPassword.forgotUsername}")
	private String forgotUsername;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private OTPRepository otpRepository;

	@Value(value = "${forgot.password.link}")
	private String forgotPasswordLink;
	
	@Value(value = "${reset.password.link}")
	private String RESET_PASSWORD_LINK;

	@Override
	@Transactional
	public ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request) {
		try {
			UserCollection userCollection = null;
			ForgotPasswordResponse response = null;

			if (request.getUsername() == null)
				request.setUsername(request.getEmailAddress());

			userCollection = userRepository.findByUserNameAndEmailAddress(request.getUsername(), request.getUsername());

			if (userCollection != null) {
				if (userCollection.getUserState() == UserState.USERSTATECOMPLETE) {
					if (userCollection.getEmailAddress().trim().equals(request.getEmailAddress().trim())) {
						TokenCollection tokenCollection = new TokenCollection();
						tokenCollection.setResourceId(userCollection.getId());
						tokenCollection.setCreatedTime(new Date());
						tokenCollection = tokenRepository.save(tokenCollection);

						String body = mailBodyGenerator.generateForgotPasswordEmailBody(
								userCollection.getTitle() + " " + userCollection.getFirstName(),
								tokenCollection.getId());
						mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);
						
						sendForgotPasswordMessage(userCollection.getMobileNumber(), tokenCollection.getId());
						response = new ForgotPasswordResponse(userCollection.getUserName(),
								userCollection.getMobileNumber(), userCollection.getEmailAddress(), RoleEnum.DOCTOR);
						
					} else {
						logger.warn("Email address is empty."+ request.getEmailAddress());
						throw new BusinessException(ServiceError.InvalidInput, "Email address is empty."+ request.getEmailAddress());
					}
				} else {
					logger.warn("User is not activated"+ request.getEmailAddress());
					throw new BusinessException(ServiceError.Unknown, "User is not activated"+ request.getEmailAddress());
				}
			} else {
				logger.warn("No account present with email address, please sign up" + request.getEmailAddress());
				throw new BusinessException(ServiceError.Unknown,
						"No account present with email address, please sign up"+ request.getEmailAddress());
			}
			return response;
		} catch (BusinessException be) {
			logger.error(be + "No account present with email address, please sign up"+ request.getEmailAddress());
			throw new BusinessException(ServiceError.Unknown, "No account present with email address, please sign up"+ request.getEmailAddress());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request) {
		Boolean flag = false;
		Boolean isPatient = false;
		Boolean isSignedUp = false;
		try {
			List<UserCollection> userCollections = null;

			if (request.getMobileNumber() != null) {
				userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), UserState.USERSTATECOMPLETE.getState());
			}

			if (userCollections != null) {
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						isPatient = true;
						if (userCollection.isSignedUp())
							isSignedUp = userCollection.isSignedUp();
						break;
					}
				}
				if (!isPatient) {
					logger.warn("No account present with mobile number, please sign up");
					throw new BusinessException(ServiceError.Unknown,
							"No account present with mobile number, please sign up");
				}
				if (!isSignedUp) {
					logger.warn("No account present with mobile number, please sign up");
					throw new BusinessException(ServiceError.Unknown,
							"No account present with mobile number, please sign up");
				}
				if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
					String OTP = LoginUtils.generateOTP();
					SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null, OTP
							+ " is your Healthcoco account OTP. Enter this in Healthcoco app to confirm your account.",
							request.getMobileNumber(), "OTPVerification");
					sMSServices.sendSMS(smsTrackDetail, false);

					OTPCollection otpCollection = new OTPCollection();
					otpCollection.setCreatedTime(new Date());
					otpCollection.setOtpNumber(OTP);
					otpCollection.setMobileNumber(request.getMobileNumber());
					otpCollection.setGeneratorId(request.getMobileNumber());
					otpCollection.setCreatedBy(request.getMobileNumber());
					otpCollection = otpRepository.save(otpCollection);

					flag = true;
				} else {
					logger.warn("Email address or mobile number should be provided");
					throw new BusinessException(ServiceError.InvalidInput,
							"Email address or mobile number should be provided");
				}
			} else {
				logger.warn("User not Found");
				throw new BusinessException(ServiceError.Unknown, "User not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return flag;
	}

	// @Override
	@Transactional
	public ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username) {
		try {
			UserCollection userCollection = null;
			ForgotPasswordResponse response = null;

			if (username != null && !username.isEmpty()) {
				userCollection = userRepository.findByUserName(username);
				if (userCollection != null) {
					response = new ForgotPasswordResponse(username, userCollection.getMobileNumber(),
							userCollection.getEmailAddress(), RoleEnum.PATIENT);
				} else {
					logger.warn("User not Found.");
					throw new BusinessException(ServiceError.NoRecord, "User not Found.");
				}
			} else {
				logger.warn("Username cannot be empty");
				throw new BusinessException(ServiceError.InvalidInput, "Username cannot be empty");
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Boolean forgotUsername(ForgotUsernamePasswordRequest request) {
		boolean flag = false;
		try {
			List<UserCollection> userCollections = null;
			if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
				userCollections = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
				if (userCollections != null) {
					String body = mailBodyGenerator.generateForgotUsernameEmailBody(userCollections);
					mailService.sendEmail(request.getEmailAddress(), forgotUsernamePasswordSub, body, null);
					flag = true;
				}
			} else if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
				userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), UserState.USERSTATECOMPLETE.getState());
				if (userCollections != null) {
					// SMS logic will go here.
					flag = true;
				}
			} else {
				logger.warn(forgotUsername);
				throw new BusinessException(ServiceError.InvalidInput, forgotUsername);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return flag;
	}

	@Override
	@Transactional
	public String resetPassword(ResetPasswordRequest request) {
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			if (tokenCollection == null)
			{
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the email that we sent you.";
			
			}else if (tokenCollection.getIsUsed())
				return "Your password has already been reset.";
			else {
				if (!isLinkValid(tokenCollection.getCreatedTime()))
					return "Your reset password link has expired.";
				UserCollection userCollection = userRepository.findById(tokenCollection.getResourceId()).orElse(null);
				if (userCollection == null) {
					return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the email that we sent you.";
				}
				if (!(userCollection.getUserState() == UserState.USERSTATECOMPLETE)
						&& !(userCollection.getUserState() == UserState.NOTACTIVATED) && !(userCollection.getUserState() == UserState.DELIVERY_BOY && !(userCollection.getUserState() == UserState.VENDOR))) {
					return "User is not verified";
				}
				//userCollection.setPassword(request.getPassword());
				userCollection.setIsPasswordSet(true);
				userCollection.setPassword(passwordEncoder.encode(String.valueOf(request.getPassword())).toCharArray());
				userRepository.save(userCollection);

				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);

				String body = mailBodyGenerator.generateResetPasswordSuccessEmailBody(
						userCollection.getTitle() + " " + userCollection.getFirstName());
				mailService.sendEmail(userCollection.getEmailAddress(), resetPasswordSub, body, null);

				return "You have successfully changed your password.";
			}
		} catch (IllegalArgumentException argumentException) {
			argumentException.printStackTrace();
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public String checkLinkIsAlreadyUsed(String userId) {
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(userId)).orElse(null);
			if (tokenCollection == null)
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the email that we sent you.";
			else if (tokenCollection.getIsUsed())
				return "Your password has already been reset.";

			else {
				if (!isLinkValid(tokenCollection.getCreatedTime()))
					return "Your reset password link has expired.";
				UserCollection userCollection = userRepository.findById(tokenCollection.getResourceId()).orElse(null);
				if (userCollection == null) {
					return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the email that we sent you.";
				}
				return "VALID";
			}
		} catch (IllegalArgumentException argumentException) {
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the email that we sent you.";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public boolean isLinkValid(Date createdTime) {
		return Minutes.minutesBetween(new DateTime(createdTime), new DateTime())
				.isLessThan(Minutes.minutes(Integer.parseInt(forgotPasswordValidTime)));
	}

	@Override
	@Transactional
	public Boolean resetPasswordPatient(ResetPasswordRequest request) {
		Boolean response = false;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				char[] salt = DPDoctorUtils.generateSalt();
				char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
				for (int i = 0; i < request.getPassword().length; i++)
					passwordWithSalt[i] = request.getPassword()[i];
				for (int i = 0; i < salt.length; i++)
					passwordWithSalt[i + request.getPassword().length] = salt[i];
				char[] password = DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt);
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						userCollection.setSalt(salt);
						userCollection.setPassword(password);
						userRepository.save(userCollection);
					}
				}
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public String resetPasswordPharmacy(ResetPasswordRequest request) {
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			if (tokenCollection == null)
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the SMS that we sent you.";
			else if (tokenCollection.getIsUsed())
				return "Your password has already been reset.";
			else {
				if (!isLinkValid(tokenCollection.getCreatedTime()))
					return "Your reset password link has expired.";
				UserCollection userCollection = userRepository.findById(tokenCollection.getResourceId()).orElse(null);
				if (userCollection == null) {
					return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the SMS that we sent you.";
				}
				if (!(userCollection.getIsVerified())) {
					return "User is not verified";
				}
				List<OAuth2AuthenticationRefreshTokenCollection> refreshTokenCollections = oAuth2RefreshTokenRepository
						.findByclientIdAndUserName("healthco2business", userCollection.getMobileNumber());
				if (!refreshTokenCollections.isEmpty() && refreshTokenCollections != null) {
					oAuth2RefreshTokenRepository.deleteAll(refreshTokenCollections);
				}

				List<OAuth2AuthenticationAccessTokenCollection> accessTokenCollections = oAuth2AccessTokenRepository
						.findByClientIdAndUserName("healthco2business", userCollection.getMobileNumber());
				if (!accessTokenCollections.isEmpty() && accessTokenCollections != null) {
					oAuth2AccessTokenRepository.deleteAll(accessTokenCollections);
				}
				//userCollection.setPassword(request.getPassword());
				userCollection.setPassword(passwordEncoder.encode(request.getPassword().toString()).toCharArray());

				userRepository.save(userCollection);
				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);

				return "You have successfully changed your password.";
			}
		} catch (IllegalArgumentException argumentException) {
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification SMS that we sent you.";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public String resetPasswordCB(ResetPasswordRequest request) {
		UserCollection userCollection = null;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), UserState.COLLECTION_BOY.getState());
			if(userCollections== null || userCollections.isEmpty()) {
				return "Sorry user not found with this mobile number.";
			}
			userCollection = userCollections.get(0);
			//userCollection.setPassword(request.getPassword());
			userCollection.setPassword(passwordEncoder.encode(request.getPassword().toString()).toCharArray());
			userRepository.save(userCollection);
			return "You have successfully changed your password.";
		} catch (IllegalArgumentException argumentException) {
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification SMS that we sent you.";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public String resetPasswordForConference(ResetPasswordRequest request) {

		try {
			ConfexUserCollection userCollection = null;
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(request.getUserId())).orElse(null);
			if (tokenCollection == null)
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the SMS that we sent you.";
			else if (tokenCollection.getIsUsed())
				return "Your password has already been reset.";
			else {
				if (!isLinkValid(tokenCollection.getCreatedTime()))
					return "Your reset password link has expired.";
				userCollection = confexUserRepository.findById(tokenCollection.getResourceId()).orElse(null);
				if (userCollection == null) {
					return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the SMS that we sent you.";
				}
				if (!(userCollection.getIsVerified())) {
					return "User is not verified";
				}

				List<OAuth2AuthenticationRefreshTokenCollection> refreshTokenCollections = oAuth2RefreshTokenRepository
						.findByclientIdAndUserName("healthco2conference", userCollection.getUserName());
				if (!refreshTokenCollections.isEmpty() && refreshTokenCollections != null) {
					oAuth2RefreshTokenRepository.deleteAll(refreshTokenCollections);
				}

				List<OAuth2AuthenticationAccessTokenCollection> accessTokenCollections = oAuth2AccessTokenRepository
						.findByClientIdAndUserName("healthco2conference", userCollection.getUserName());
				if (!accessTokenCollections.isEmpty() && accessTokenCollections != null) {
					oAuth2AccessTokenRepository.deleteAll(accessTokenCollections);
				}
				if (userCollection != null) {
					//userCollection.setPassword(request.getPassword());
					userCollection.setPassword(passwordEncoder.encode(request.getPassword().toString()).toCharArray());
					confexUserRepository.save(userCollection);
				}
				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);

				return "You have successfully changed your password.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}


    
    private void sendForgotPasswordMessage(String mobileNumber , String tokenId) {
		try {
				String link = RESET_PASSWORD_LINK + "?uid=" + tokenId;
				String shortUrl = DPDoctorUtils.urlShortner(link);
				String message = "Please click on the button below and follow the subsequent instructions to reset your account’s password. "+shortUrl;
			//	System.out.println(message);
				
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("RESET_PASSWORD_SMS");
				SMSDetail smsDetail = new SMSDetail();
				SMS sms = new SMS();
				sms.setSmsText(message);

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(mobileNumber);
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				sMSServices.sendSMS(smsTrackDetail, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

package com.dpdocter.services.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Subscription;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.PackageDetailObjectCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SubscriptionCollection;
import com.dpdocter.collections.SubscriptionDetailCollection;
import com.dpdocter.collections.SubscriptionHistoryCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.PackageDetailObjectRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.SubscriptionHistoryRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SubscriptionService;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static Logger logger = Logger.getLogger(SubscriptionServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private SubscriptionDetailRepository subscriptionDetailRepository;

	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	SubscriptionHistoryRepository subscriptionHistoryRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PackageDetailObjectRepository packageDetailObjectRepository;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	public List<SubscriptionDetail> addsubscriptionData() {
		List<SubscriptionDetail> response = null;
		try {
			RoleCollection superAdminRole = null;
			RoleCollection roleCollection = null;
			UserRoleCollection superAdminRoleCollection = null;
			SubscriptionDetailCollection subscriptionDetailCollection = null;
			Set<ObjectId> locationIdSet = null;
			SubscriptionDetail subscriptionDetail = null;
			Criteria criteria = new Criteria("roles.role").is(RoleEnum.LOCATION_ADMIN.toString());

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("role_cl", "roleId", "_id", "roles"), Aggregation.match(criteria));
			AggregationResults<UserRoleCollection> groupResults = mongoTemplate.aggregate(aggregation,
					UserRoleCollection.class, UserRoleCollection.class);
			List<UserRoleCollection> userRoleList = groupResults.getMappedResults();
			response = new ArrayList<SubscriptionDetail>();
			superAdminRole = roleRepository
					.findByRoleAndLocationIdIsNullAndHospitalIdIsNull(RoleEnum.SUPER_ADMIN.toString());
			if (superAdminRole == null) {
				superAdminRole = new RoleCollection();
				superAdminRole.setCreatedTime(new Date());
				superAdminRole.setRole(RoleEnum.SUPER_ADMIN.toString());
				superAdminRole = roleRepository.save(roleCollection);
			}
			for (UserRoleCollection userRoleCollection : userRoleList) {
				// create SuperAdmin role
				roleCollection = roleRepository.findById(userRoleCollection.getRoleId()).orElse(null);
				// superAdminRole = new RoleCollection();
				// superAdminRole.setCreatedTime(new Date());
				// superAdminRole.setRole(RoleEnum.SUPER_ADMIN.toString());
//				superAdminRole.setHospitalId(roleCollection.getHospitalId());
//				superAdminRole.setLocationId(roleCollection.getLocationId());
				// superAdminRole = roleRepository.save(superAdminRole);
				// create SuperAdmin userRole
				superAdminRoleCollection = new UserRoleCollection();
				superAdminRoleCollection.setRoleId(roleCollection.getId());
				superAdminRoleCollection.setUserId(userRoleCollection.getUserId());
				superAdminRoleCollection.setCreatedTime(new Date());
				superAdminRoleCollection = userRoleRepository.save(superAdminRoleCollection);
				// create subscribtion detail
				subscriptionDetailCollection = new SubscriptionDetailCollection();
				subscriptionDetailCollection.setCreatedTime(new Date());
				subscriptionDetailCollection.setDoctorId(superAdminRoleCollection.getUserId());
				subscriptionDetailCollection.setFromDate(new Date());
				subscriptionDetailCollection.setSmsFromDate(new Date());
				subscriptionDetailCollection.setIsExpired(false);
				locationIdSet = new HashSet<ObjectId>();
				locationIdSet.add(superAdminRole.getLocationId());
				subscriptionDetailCollection.setLocationIds(locationIdSet);
				subscriptionDetailCollection.setNoOfsms(500);
				subscriptionDetailCollection.setToDate(DPDoctorUtils.addmonth(new Date(), 12));
				subscriptionDetailCollection.setSmsToDate(DPDoctorUtils.addmonth(new Date(), 12));
				subscriptionDetailCollection.setIsDemo(true);
				subscriptionDetailCollection = subscriptionDetailRepository.save(subscriptionDetailCollection);
				subscriptionDetail = new SubscriptionDetail();
				BeanUtil.map(subscriptionDetailCollection, subscriptionDetail);
				response.add(subscriptionDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	// new one subscription
	@Override
	public Subscription addEditSubscription(Subscription request) {
		Subscription response = null;
		try {
			SubscriptionCollection subscriptionCollection = null;
			SubscriptionHistoryCollection subscriptionHistoryCollection = null;
			subscriptionHistoryCollection = new SubscriptionHistoryCollection();

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				subscriptionCollection = subscriptionRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (subscriptionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Subscription Not found with Id");
				}
				// to pass one collection data to another

				BeanUtil.map(subscriptionCollection, subscriptionHistoryCollection);
				System.out.println(subscriptionHistoryCollection);

				// get doctor from doctor id;
				UserCollection userCollection = null;
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				System.out.println(userCollection);

				// set values
				PackageType oldPackageName = subscriptionCollection.getPackageName();
				PackageType newPackageName = request.getPackageName();

				System.out.println("oldPackageName" + oldPackageName + "newPackageName" + newPackageName);

				request.setUpdatedTime(new Date());
//				request.setCountryCode(userCollection.getCountryCode());
				request.setCreatedBy(subscriptionCollection.getCreatedBy());
				subscriptionCollection.setMobileNumber(userCollection.getMobileNumber());
				subscriptionCollection.setEmailAddress(userCollection.getEmailAddress());

				// clinic package change
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorId(new ObjectId(request.getDoctorId()));

				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						doctorClinicProfileCollection.setUpdatedTime(new Date());
						doctorClinicProfileCollection.setPackageType(newPackageName.toString());

						doctorClinicProfileRepository.save(doctorClinicProfileCollection);
					}
				}

//				// call sms function  
				System.out.println(userCollection);
				String doctorName = userCollection.getTitle() + userCollection.getFirstName();
				System.out.println(doctorName);
				subscriptionHistoryCollection = subscriptionHistoryRepository.save(subscriptionHistoryCollection);

				sendSMS(doctorName, userCollection.getMobileNumber(), userCollection.getCountryCode(), oldPackageName,
						newPackageName);

				String body = " Your Subscription Plan Changed " + oldPackageName + "to" + newPackageName;
				try {
					Boolean ck = mailService.sendEmail("nikita.patil@healthcoco.com", "Update Packege Detail", body,
							null);
					System.out.println("main send" + ck);
				} catch (MessagingException e) {
					System.out.println("main send err");
					e.printStackTrace();
				}
				pushNotificationServices.notifyUser(userCollection.getId().toString(), "Package updated.",
						ComponentType.PACKAGE_DETAIL.getType(), null, null);

			} else {
//				subscriptionCollection = new SubscriptionCollection();
//				BeanUtil.map(request, subscriptionCollection);
//				subscriptionCollection.setCreatedBy("ADMIN");
//				subscriptionCollection.setUpdatedTime(new Date());
//				subscriptionCollection.setCreatedTime(new Date());

				throw new BusinessException(ServiceError.Unknown, "Id Can not be null");

			}
			subscriptionCollection = subscriptionRepository.save(subscriptionCollection);
			response = new Subscription();

			BeanUtil.map(subscriptionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit Subscription  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit Subscription " + e.getMessage());

		}
		return response;
	}

	public Boolean sendSMS(String doctorName, String mobileNumber, String countryCode, PackageType oldPackageName,
			PackageType newPackageName) {
		Boolean response = false;
		try {
			SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

			smsTrackDetail.setType(ComponentType.PACKAGE_DETAIL.getType());
			SMSDetail smsDetail = new SMSDetail();

			smsDetail.setUserName(doctorName);
			SMS sms = new SMS();

			sms.setSmsText(" Your Subscription Plan Changed " + oldPackageName + " to " + newPackageName
					+ ". Stay Healthy and Happy!");

			SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);
			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);
			Boolean ck = smsServices.sendSMS(smsTrackDetail, false);
			System.out.println("sms send" + ck);

			// save sms in repository write code

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Sending SMS");
			throw new BusinessException(ServiceError.Unknown, "Error While Sending SMS " + e.getMessage());
		}
		return response;
	}

	@Override
	public Subscription getSubscriptionByDoctorId(String doctorId, PackageType packageName) {
		Subscription response = null;
		try {
			SubscriptionCollection subscriptionCollection = subscriptionRepository
					.findByDoctorId(new ObjectId(doctorId));
			if (subscriptionCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

			if (packageName != null) {

				PackageDetailObjectCollection packageBasic = packageDetailObjectRepository
						.findByPackageName(PackageType.BASIC);

				PackageDetailObjectCollection packagePro = packageDetailObjectRepository
						.findByPackageName(PackageType.PRO);

				PackageDetailObjectCollection packageAdvance = packageDetailObjectRepository
						.findByPackageName(PackageType.ADVANCE);

				// for 10th point
				// package price
				double BASIC = packageBasic.getAmount();
				double PRO = packagePro.getAmount();
				double ADVANCE = packageAdvance.getAmount();

				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				LocalDate currentDate = LocalDate.now();
				localCalendar.setTime(subscriptionCollection.getFromDate());
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				LocalDate newDate = LocalDate.of(currentYear, currentMonth, currentDay);
				Period period = Period.between(currentDate, newDate);// get difference bet today & fromdate
				System.out.println(period + "mon" + period.getMonths());
				// pro to adv
				if (subscriptionCollection.getPackageName() == PackageType.PRO && packageName == PackageType.ADVANCE) {
					if (period.getMonths() == 0) {// afetr 1 month
						double k = ADVANCE - (int) (PRO * (90.0f / 100.0f));
						System.out.println(period.getMonths() + k);
						subscriptionCollection.setAmount(k);
					} else if (period.getMonths() == -1) {// after 2 month
						double k = ADVANCE - (int) (PRO * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);
					} else if (period.getMonths() == -2) {// after 3 month
						double k = ADVANCE - (int) (PRO * (70.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -3) {// after 4 month
						// 60 % of 10000 is 2000
						double k = ADVANCE - (int) (PRO * (60.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -4) {// after 5 month
						double k = ADVANCE - (int) (PRO * (50.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -5) {// after 6 month
						double k = ADVANCE - (double) (PRO * (40.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -6) {// after 7 month
						double k = ADVANCE - (int) (PRO * (30.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -7) {// after 8 month
						double k = ADVANCE - (int) (PRO * (20.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -8) {// after 9 month
						// 10 % of 10000 is 2000
						double k = ADVANCE - (int) (PRO * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -9) {// after 9 month
						// 10 % of 10000 is 2000
						double k = ADVANCE - (int) (PRO * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else {// after 10 month
						double k = ADVANCE;
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					}

				} // basic to adv
				else if (subscriptionCollection.getPackageName() == PackageType.BASIC
						&& packageName == PackageType.ADVANCE) {
					if (period.getMonths() == 0) {// afetr 1 month
						double k = ADVANCE - (int) (BASIC * (90.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);
					} else if (period.getMonths() == -1) {// after 2 month
						double k = ADVANCE - (int) (BASIC * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -2) {// after 3 month
						double k = ADVANCE - (int) (BASIC * (70.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -3) {// after 4 month
						// 60 % of 10000 is 2000
						double k = ADVANCE - (int) (BASIC * (60.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -4) {// after 5 month
						double k = ADVANCE - (int) (BASIC * (50.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -5) {// after 6 month
						double k = ADVANCE - (int) (BASIC * (40.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -6) {// after 7 month
						double k = ADVANCE - (int) (BASIC * (30.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -7) {// after 8 month
						double k = ADVANCE - (int) (BASIC * (20.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -8) {// after 9 month
						// 10 % of 10000 is 2000
						double k = ADVANCE - (int) (BASIC * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -9) {// after 9 month
						// 10 % of 10000 is 2000
						double k = ADVANCE - (int) (BASIC * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else {// after 10 month
						double k = ADVANCE;
						System.out.println(k);
						subscriptionCollection.setAmount(k);
						;
					}
				}
				// basic to pro
				else if (subscriptionCollection.getPackageName() == PackageType.BASIC
						&& packageName == PackageType.PRO) {
					if (period.getMonths() == 0) {// afetr 1 month
						double k = PRO - (int) (BASIC * (90.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);
					} else if (period.getMonths() == -1) {// after 2 month
						double k = PRO - (int) (BASIC * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -2) {// after 3 month
						double k = PRO - (int) (BASIC * (70.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -3) {// after 4 month
						// 60 % of 10000 is 2000
						double k = PRO - (int) (BASIC * (60.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -4) {// after 5 month
						double k = PRO - (int) (BASIC * (50.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -5) {// after 6 month
						double k = PRO - (int) (BASIC * (40.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -6) {// after 7 month
						double k = PRO - (int) (BASIC * (30.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -7) {// after 8 month
						double k = PRO - (int) (BASIC * (20.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -8) {// after 9 month
						// 10 % of 10000 is 2000
						double k = PRO - (int) (BASIC * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else if (period.getMonths() == -9) {// after 9 month
						// 10 % of 10000 is 2000
						double k = PRO - (int) (BASIC * (80.0f / 100.0f));
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					} else {// after 10 month
						double k = PRO;
						System.out.println(k);
						subscriptionCollection.setAmount(k);

					}
				} else if (subscriptionCollection.getPackageName() == PackageType.PRO
						&& packageName == PackageType.PRO) {
					double k = PRO;
					System.out.println(k);
					subscriptionCollection.setAmount(k);
				}else if (subscriptionCollection.getPackageName() == PackageType.ADVANCE
						&& packageName == PackageType.ADVANCE) {
					double k = ADVANCE;
					System.out.println(k);
					subscriptionCollection.setAmount(k);
				}else if (subscriptionCollection.getPackageName() == PackageType.BASIC
						&& packageName == PackageType.BASIC) {
					double k = BASIC;
					System.out.println(k);
					subscriptionCollection.setAmount(k);
				}
				
			} // if close
			response = new Subscription();
			BeanUtil.map(subscriptionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public PackageDetailObject getPackageDetailByPackageName(PackageType packageName) {
		PackageDetailObject response = null;
		try {
			PackageDetailObjectCollection packageDetailObjectCollection = packageDetailObjectRepository
					.findByPackageName(packageName);
			if (packageDetailObjectCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such name");
			}
			response = new PackageDetailObject();

			BeanUtil.map(packageDetailObjectCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;

	}

}

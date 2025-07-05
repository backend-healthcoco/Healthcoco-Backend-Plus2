package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Country;
import com.dpdocter.beans.PackageAmountObject;
import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Subscription;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorSubscriptionPaymentCollection;
import com.dpdocter.collections.LocationCollection;
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
import com.dpdocter.repository.DoctorSubscriptionPaymentRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PackageDetailObjectRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.repository.SubscriptionHistoryRepository;
import com.dpdocter.repository.SubscriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.SubscriptionPaymentSignatureRequest;
import com.dpdocter.request.SubscriptionRequest;
import com.dpdocter.response.OrderReponse;
import com.dpdocter.response.SubscriptionResponse;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SubscriptionService;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import common.util.web.DPDoctorUtils;

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
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private SubscriptionHistoryRepository subscriptionHistoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PackageDetailObjectRepository packageDetailObjectRepository;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private DoctorSubscriptionPaymentRepository doctorSubscriptionPaymentRepository;

	@Value(value = "${rayzorpay.api.secret}")
	private String secret;

	@Value(value = "${rayzorpay.api.key}")
	private String keyId;

	@Autowired
	private LocationRepository locationRepository;

	@Override
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

	public String generateId() {
		String id = "";
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(new Date());
		int currentSecond = localCalendar.get(Calendar.SECOND);
		int currentMilliSecond = localCalendar.get(Calendar.MILLISECOND);
		id = currentSecond + "" + currentMilliSecond;
		return id;
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
			smsTrackDetail.setTemplateId("1307162160395400042");
			SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);
			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);
			Boolean ck = smsServices.sendSMS(smsTrackDetail, false);

			// save sms in repository write code

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Sending SMS");
		}
		return response;
	}

	@Override
	public Subscription getSubscriptionByDoctorId(String doctorId, PackageType packageName, int duration,
			int newAmount) {
		Subscription response = null;
		try {
			List<SubscriptionCollection> subscriptionCollections = subscriptionRepository
					.findByDoctorId(new ObjectId(doctorId));
			SubscriptionCollection subscriptionCollection = null;
			if (!DPDoctorUtils.isNullOrEmptyList(subscriptionCollections))
				subscriptionCollection = subscriptionCollections.get(0);
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
				// package price
				List<PackageAmountObject> BASIC = packageBasic.getPackageAmount();
				List<PackageAmountObject> PRO = packagePro.getPackageAmount();
				List<PackageAmountObject> ADVANCE = packageAdvance.getPackageAmount();

				if (subscriptionCollection.getPackageName() != PackageType.FREE) {

					// for trial period condition
					if (subscriptionCollection.getPackageName() == PackageType.BASIC
							&& subscriptionCollection.getAmount() == 0) {
						subscriptionCollection.setAmount(newAmount);
					} else {
						// from date toDate difference
						Calendar fromDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						fromDateConvert.setTime(subscriptionCollection.getFromDate());
						int fromDateConvertDay = fromDateConvert.get(Calendar.DATE);
						int fromDateConvertMonth = fromDateConvert.get(Calendar.MONTH) + 1;
						int fromDateConvertYear = fromDateConvert.get(Calendar.YEAR);
						// to date
						Calendar toDateConvert = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						toDateConvert.setTime(subscriptionCollection.getToDate());
						int toDateConvertDay = toDateConvert.get(Calendar.DATE);
						int toDateConvertMonth = toDateConvert.get(Calendar.MONTH) + 1;
						int toDateConvertYear = toDateConvert.get(Calendar.YEAR);
						LocalDate fromDate = LocalDate.of(fromDateConvertYear, fromDateConvertMonth,
								fromDateConvertDay);
						LocalDate toDate = LocalDate.of(toDateConvertYear, toDateConvertMonth, toDateConvertDay);

						Period diiff = Period.between(fromDate, toDate);// get difference bet today & fromdate

						// for 10th point
						Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						LocalDate currentDate = LocalDate.now();
						localCalendar.setTime(subscriptionCollection.getFromDate());
						int currentDay = localCalendar.get(Calendar.DATE);
						int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
						int currentYear = localCalendar.get(Calendar.YEAR);

						LocalDate newDate = LocalDate.of(currentYear, currentMonth, currentDay);
						Period period = Period.between(currentDate, newDate);// get difference bet today & fromdate
						int usedMonths = -(period.getMonths());
						// pro to adv
//					Cost = new amount - old amount + (per month cost of old amount * months used)
						// find per month cost of packages
						int getMonthsFromYear = diiff.getYears() * 12;// calculate number of months from old duration
						int amountPerMonth = subscriptionCollection.getAmount() / getMonthsFromYear;// to get per month
																									// cost
																									// of old package
						int discountedAmount = newAmount - subscriptionCollection.getAmount()
								+ amountPerMonth * usedMonths;

						subscriptionCollection.setAmount(discountedAmount);
					}

				} // if close of amt
				else {
					// send request amount directly
					subscriptionCollection.setAmount(newAmount);

					// pro to adv
//					if (packageName == PackageType.ADVANCE) {
//						ADVANCE.forEach(x -> {
//							if (duration == x.getDuration()) {
//								subscriptionCollection.setAmount(x.getAmount());
//							}
//						});
//					} // basic to adv
//					else if (packageName == PackageType.BASIC) {
//						BASIC.forEach(x -> {
//							if (duration == x.getDuration()) {
//								subscriptionCollection.setAmount(x.getAmount());
//							}
//						});
//					} // basic to pro
//					if (packageName == PackageType.PRO) {
//						PRO.forEach(x -> {
//							if (duration == x.getDuration()) {
//								subscriptionCollection.setAmount(x.getAmount());
//							}
//						});
//					}
				}
			} // if close of package name
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

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return response;

	}

	@Override
	public List<Country> getCountry(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<Country> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("countryName").regex("^" + searchTerm, "i"),
						new Criteria("countryName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, CountryCollection.class, Country.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Country " + e.getMessage());
			e.printStackTrace();
		}
		return response;

	}

	@Override
	public Integer countCountry(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("countryName").regex("^" + searchTerm, "i"),
					new Criteria("countryName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), CountryCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}

	@Override
	public List<PackageDetailObject> getPackages(int size, int page, Boolean isDiscarded, String searchTerm) {
		List<PackageDetailObject> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, PackageDetailObjectCollection.class, PackageDetailObject.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}

	@Override
	public Integer countPackages(Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded);
			criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
					new Criteria("packageName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), PackageDetailObjectCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;

	}

	@Override
	public List<Subscription> getSubscriptionHistory(String doctorId, int size, int page, Boolean isDiscarded,
			String searchTerm) {
		List<Subscription> response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded).and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, SubscriptionHistoryCollection.class, Subscription.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}

	@Override
	public Integer countSubscriptionHistory(String doctorId, Boolean isDiscarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("isDiscarded").is(isDiscarded).and("doctorId").is(new ObjectId(doctorId));
			criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
					new Criteria("packageName").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), SubscriptionHistoryCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}

	@Override
	public SubscriptionResponse addEditSubscription(SubscriptionRequest request) {
		SubscriptionResponse response = null;
		try {
			JSONObject orderRequest = new JSONObject();
			// get doctor from doctor id;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (userCollection == null) {
				logger.warn("doctor not found");
				throw new BusinessException(ServiceError.NotFound, "Doctor Not present with this id");
			}

			if (request.getPaymentStatus() == true) {
				double amount = (request.getAmount() * 100);
				// amount in paise
				orderRequest.put("amount", (int) amount);
				orderRequest.put("currency", request.getCurrency());
				orderRequest.put("receipt", userCollection.getTitle().substring(0, 2).toUpperCase() + "-RCPT-"
						+ doctorSubscriptionPaymentRepository.countByDoctorId(new ObjectId(request.getDoctorId()))
						+ generateId());
				orderRequest.put("payment_capture", request.getPaymentCapture());

				String url = "https://api.razorpay.com/v1/orders";
				String authStr = keyId + ":" + secret;
				String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				con.setDoOutput(true);

				con.setDoInput(true);
				// optional default is POST
				con.setRequestMethod("POST");
				con.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				con.setRequestProperty("Accept-Charset", "UTF-8");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Authorization", "Basic " + authStringEnc);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(orderRequest.toString());

				wr.flush();
				wr.close();
				con.disconnect();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;

				/* response = new StringBuffer(); */
				StringBuffer output = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {

					output.append(inputLine);
				}

				ObjectMapper mapper = new ObjectMapper();

				OrderReponse list = mapper.readValue(output.toString(), OrderReponse.class);

				if (userCollection != null) {
					DoctorSubscriptionPaymentCollection payment = new DoctorSubscriptionPaymentCollection();
					BeanUtil.map(request, payment);
					payment.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
					payment.setCreatedTime(new Date());
					payment.setOrderId(list.getId().toString());
					payment.setReciept(list.getReceipt().toString());
					payment.setTransactionStatus("PENDING");
					doctorSubscriptionPaymentRepository.save(payment);
					response = new SubscriptionResponse();
					BeanUtil.map(payment, response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return response;
	}

	@Override
	@Transactional
	public Boolean verifySignature(SubscriptionPaymentSignatureRequest request) {
		Boolean response = false;
		SubscriptionHistoryCollection subscriptionHistoryCollection = null;
		subscriptionHistoryCollection = new SubscriptionHistoryCollection();

		// for set two days
		Calendar cal = Calendar.getInstance();

		try {

			// get doctor from doctor id;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (userCollection == null) {
				logger.warn("doctor id not found");
				throw new BusinessException(ServiceError.NotFound, "Doctor Not present with this id");
			}
			JSONObject options = new JSONObject();
			options.put("razorpay_order_id", request.getOrderId());
			options.put("razorpay_payment_id", request.getPaymentId());
			options.put("razorpay_signature", request.getSignature());
			response = Utils.verifyPaymentSignature(options, secret);
			if (response) {
				Criteria criteria = new Criteria("orderId").is(request.getOrderId()).and("doctorId")
						.is(new ObjectId(request.getDoctorId())).and("transactionStatus").is("PENDING");
				DoctorSubscriptionPaymentCollection doctorSubscriptionPaymentCollection = mongoTemplate
						.findOne(new Query(criteria), DoctorSubscriptionPaymentCollection.class);
				doctorSubscriptionPaymentCollection.setTransactionId(request.getPaymentId());
				doctorSubscriptionPaymentCollection.setTransactionStatus("SUCCESS");
				doctorSubscriptionPaymentCollection.setCreatedTime(new Date());
				doctorSubscriptionPaymentCollection.setUpdatedTime(new Date());
				doctorSubscriptionPaymentRepository.save(doctorSubscriptionPaymentCollection);

				if (!DPDoctorUtils.anyStringEmpty(request.getSubscriptionId())) {
					SubscriptionCollection subscriptionCollection = subscriptionRepository
							.findById(new ObjectId(request.getSubscriptionId())).orElse(null);
					if (subscriptionCollection == null) {
						logger.warn("subscription id not found");
						throw new BusinessException(ServiceError.NotFound, "Subscription Not found with Id");
					}
					BeanUtil.map(request, subscriptionCollection);
					subscriptionCollection.setUpdatedTime(new Date());
					subscriptionCollection.setCreatedBy(userCollection.getTitle() + "" + userCollection.getFirstName());
					subscriptionCollection.setMobileNumber(userCollection.getMobileNumber());
					subscriptionCollection.setEmailAddress(userCollection.getEmailAddress());
					subscriptionCollection.setPackageName(doctorSubscriptionPaymentCollection.getPackageName());
					subscriptionCollection.setAmount(doctorSubscriptionPaymentCollection.getAmount());
					subscriptionCollection.setCountryCode(userCollection.getCountryCode());
					subscriptionCollection.setPaymentStatus(true);
					subscriptionCollection.setMode(doctorSubscriptionPaymentCollection.getMode());
					subscriptionCollection
							.setTransactionStatus(doctorSubscriptionPaymentCollection.getTransactionStatus());
					subscriptionCollection.setFromDate(new Date());
					subscriptionCollection.setToDate(DPDoctorUtils.addmonth(new Date(), 12 * request.getDuration()));
					subscriptionRepository.save(subscriptionCollection);

					// save to History
					BeanUtil.map(subscriptionCollection, subscriptionHistoryCollection);
					subscriptionHistoryCollection.setSubscriptionId(subscriptionCollection.getId());
					subscriptionHistoryCollection
							.setCreatedBy(userCollection.getTitle() + "" + userCollection.getFirstName());
					subscriptionHistoryCollection.setDoctorId(subscriptionCollection.getDoctorId());
					subscriptionHistoryCollection.setCreatedTime(new Date());
					subscriptionHistoryRepository.save(subscriptionHistoryCollection);
					// clinic package change
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByDoctorId(new ObjectId(request.getDoctorId()));

					if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							doctorClinicProfileCollection.setUpdatedTime(new Date());
							doctorClinicProfileCollection
									.setPackageType(subscriptionCollection.getPackageName().toString());

							doctorClinicProfileRepository.save(doctorClinicProfileCollection);
						}
					}
				} else {
					SubscriptionCollection subscriptionCollection = new SubscriptionCollection();

					List<SubscriptionCollection> subscriptionCollections = subscriptionRepository
							.findByDoctorId(new ObjectId(request.getDoctorId()));
					SubscriptionCollection subscriptionCkD = null;
					if (!DPDoctorUtils.isNullOrEmptyList(subscriptionCollections))
						subscriptionCkD = subscriptionCollections.get(0);
					if (subscriptionCkD != null) {
						subscriptionCollection.setId(subscriptionCkD.getId());
					}
					BeanUtil.map(request, subscriptionCollection);
					subscriptionCollection.setCreatedTime(new Date());
					subscriptionCollection.setUpdatedTime(new Date());
					subscriptionCollection.setCreatedBy(userCollection.getTitle() + "" + userCollection.getFirstName());
					subscriptionCollection.setMobileNumber(userCollection.getMobileNumber());
					subscriptionCollection.setEmailAddress(userCollection.getEmailAddress());
					subscriptionCollection.setPackageName(doctorSubscriptionPaymentCollection.getPackageName());
					subscriptionCollection.setAmount(doctorSubscriptionPaymentCollection.getAmount());
					subscriptionCollection.setCountryCode(userCollection.getCountryCode());
					subscriptionCollection.setPaymentStatus(true);
					subscriptionCollection.setMode(doctorSubscriptionPaymentCollection.getMode());
					subscriptionCollection
							.setTransactionStatus(doctorSubscriptionPaymentCollection.getTransactionStatus());
					subscriptionCollection.setFromDate(new Date());
					subscriptionCollection.setToDate(DPDoctorUtils.addmonth(new Date(), 12 * request.getDuration()));

					subscriptionRepository.save(subscriptionCollection);
					// save to History
					BeanUtil.map(subscriptionCollection, subscriptionHistoryCollection);
					subscriptionHistoryCollection.setSubscriptionId(subscriptionCollection.getId());
					subscriptionHistoryCollection.setDoctorId(subscriptionCollection.getDoctorId());
					subscriptionHistoryCollection.setCreatedTime(new Date());
					subscriptionHistoryRepository.save(subscriptionHistoryCollection);

					// clinic package change
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByDoctorId(new ObjectId(request.getDoctorId()));

					if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							doctorClinicProfileCollection.setUpdatedTime(new Date());
							doctorClinicProfileCollection
									.setPackageType(subscriptionCollection.getPackageName().toString());

							doctorClinicProfileRepository.save(doctorClinicProfileCollection);
							//active incative sms flag for STANDARD account
							LocationCollection locationCollection = locationRepository
									.findById(doctorClinicProfileCollection.getLocationId()).orElse(null);

							if (locationCollection != null) {
								locationCollection.setSmsAccountActive(true);
								locationRepository.save(locationCollection);
							}
						}
					}
				}

				if (doctorSubscriptionPaymentCollection.getTransactionStatus().equalsIgnoreCase("SUCCESS")) {
					if (userCollection != null) {
						String message = "";
						message = StringEscapeUtils.unescapeJava(message);
						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

						smsTrackDetail.setType("Subscription online Payment");
						smsTrackDetail.setDoctorId(userCollection.getId());
						SMSDetail smsDetail = new SMSDetail();
						smsDetail.setUserId(userCollection.getId());
						SMS sms = new SMS();
						smsDetail.setUserName(userCollection.getFirstName());
						String pattern = "dd/MM/yyyy";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

						sms.setSmsText("Hello " + userCollection.getFirstName()
								+ ", your Payment has been done successfully on Date: "
								+ simpleDateFormat.format(doctorSubscriptionPaymentCollection.getCreatedTime())
								+ ", Mode of Payment:  " + doctorSubscriptionPaymentCollection.getMode()
								+ ", ReceiptId: " + doctorSubscriptionPaymentCollection.getReciept()
								+ ", Total cost Rs.: " + doctorSubscriptionPaymentCollection.getAmount() + ", Plan: "
								+ doctorSubscriptionPaymentCollection.getPackageName() + ", Duration: "
								+ request.getDuration() + " year.-Healthcoco");

						SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(userCollection.getMobileNumber());
						sms.setSmsAddress(smsAddress);
						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setSmsDetails(smsDetails);
						smsTrackDetail.setTemplateId("1307161561866511824");
						smsServices.sendSMS(smsTrackDetail, false);

						String body = mailBodyGenerator.subscriptionPaymentEmailBody(userCollection.getFirstName(),
								simpleDateFormat.format(doctorSubscriptionPaymentCollection.getCreatedTime()),
								doctorSubscriptionPaymentCollection.getTransactionId(),
								doctorSubscriptionPaymentCollection.getReciept(),
								Integer.toString(doctorSubscriptionPaymentCollection.getAmount()),
								doctorSubscriptionPaymentCollection.getPackageName().toString(),
								doctorSubscriptionPaymentCollection.getMode().toString(), request.getDuration(),
								"subscriptionPayment.vm");
						Boolean mail = mailService.sendEmail(userCollection.getEmailAddress(),
								"Subscription Plan Updated on Healthcoco+", body, null);
					}
				}
			}

		} catch (RazorpayException e) {
			// Handle Exception
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return response;
	}
}

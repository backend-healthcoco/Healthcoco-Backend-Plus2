package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.MessageStatus;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.BulkSmsCreditsCollection;
import com.dpdocter.collections.BulkSmsHistoryCollection;
import com.dpdocter.collections.BulkSmsPackageCollection;
import com.dpdocter.collections.BulkSmsPaymentCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.MessageCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.AuditActionType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BulkSmsHistoryRepository;
import com.dpdocter.repository.BulkSmsPackageRepository;
import com.dpdocter.repository.BulkSmsPaymentRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.response.OrderReponse;
import com.dpdocter.services.AuditService;
import com.dpdocter.services.BulkSmsServices;
import com.dpdocter.services.SMSServices;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import common.util.web.DPDoctorUtils;

@Service
public class BulkSmsServiceImpl implements BulkSmsServices {

	private static Logger logger = LogManager.getLogger(BulkSmsServiceImpl.class.getName());

	@Autowired
	private BulkSmsPackageRepository bulkSmsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${rayzorpay.api.secret}")
	private String secret;

	@Autowired
	private SMSServices sMSServices;

	@Value(value = "${rayzorpay.api.key}")
	private String keyId;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BulkSmsPaymentRepository bulkSmsPaymentRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SMSTrackRepository smsTrackRepository;

	@Autowired
	private BulkSmsHistoryRepository bulkSmsHistoryRepository;

	@Value(value = "${SERVICE_ID}")
	private String SID;

	@Value(value = "${API_KEY}")
	private String KEY;

	@Autowired
	private AuditService auditService;

	@Override
	public BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request) {
		BulkSmsPackage response = null;
		try {
			BulkSmsPackageCollection bulkSms = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				bulkSms = bulkSmsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (bulkSms == null) {
					throw new BusinessException(ServiceError.Unknown, "Id not found");
				}
				BeanUtil.map(request, bulkSms);
				bulkSms.setUpdatedTime(new Date());
			} else {
				bulkSms = new BulkSmsPackageCollection();
				BeanUtil.map(request, bulkSms);
				bulkSms.setCreatedTime(new Date());
				bulkSms.setUpdatedTime(new Date());

			}
			bulkSmsRepository.save(bulkSms);
			response = new BulkSmsPackage();
			BeanUtil.map(bulkSms, response);

		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit Bulksms package" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsPackage> getBulkSmsPackage(int page, int size, String searchTerm, Boolean discarded) {
		List<BulkSmsPackage> response = null;
		try {

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			if (discarded != null)
				criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			response = mongoTemplate.aggregate(aggregation, BulkSmsPackageCollection.class, BulkSmsPackage.class)
					.getMappedResults();

		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Bulksms package" + e.getMessage());
		}

		return response;
	}

	@Override
	public Integer CountBulkSmsPackage(String searchTerm, Boolean discarded) {
		Integer count = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));

			count = (int) mongoTemplate.count(new Query(criteria), BulkSmsPackageCollection.class);

		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Bulksms package" + e.getMessage());
		}
		return count;
	}

	@Override
	public List<BulkSmsCredits> getCreditsByDoctorIdAndLocationId(int size, int page, String searchTerm,
			String doctorId, String locationId) {
		List<BulkSmsCredits> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				ObjectId doctorObjectId = new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				ObjectId locationObjectId = new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
						new Criteria("smsPackage.packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}

			response = mongoTemplate.aggregate(aggregation, BulkSmsCreditsCollection.class, BulkSmsCredits.class)
					.getMappedResults();

		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Bulksms credits count" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsCredits> getBulkSmsHistory(int page, int size, String searchTerm, String doctorId,
			String locationId) {
		List<BulkSmsCredits> response = null;
		try {

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				ObjectId doctorObjectId = new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				ObjectId locationObjectId = new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
						new Criteria("smsPackage.packageName").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			response = mongoTemplate.aggregate(aggregation, BulkSmsHistoryCollection.class, BulkSmsCredits.class)
					.getMappedResults();

		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Bulksms package" + e.getMessage());
		}

		return response;
	}

	@Override
	public List<MessageResponse> getSmsReport(int page, int size, String doctorId, String locationId,
			String messageType, String type, String status, String fromDate, String toDate) {
		List<MessageResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				ObjectId doctorObjectId = new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				ObjectId locationObjectId = new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(messageType))
				criteria.and("messageType").is(messageType);

			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status);

			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			}

			if (from != null && to != null) {
				criteria.and("createdTime").gte(from).lte(to);
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			response = mongoTemplate.aggregate(aggregation, MessageCollection.class, MessageResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Bulksms package" + e.getMessage());
		}
		return response;
	}

	@Override
	public BulkSmsPaymentResponse addCredits(OrderRequest request) {
		BulkSmsPaymentResponse response = null;
		try {
			JSONObject orderRequest = new JSONObject();
			BulkSmsPackageCollection bulkPackage = bulkSmsRepository
					.findById(new ObjectId(request.getBulkSmsPackageId())).orElse(null);

			if (bulkPackage == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Sms Package not found");
			}

			UserCollection user = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (user == null) {
				throw new BusinessException(ServiceError.InvalidInput, "user not found");
			}
			double amount = (request.getDiscountAmount() * 100);
			// amount in paise
			orderRequest.put("amount", (int) amount);
			orderRequest.put("currency", request.getCurrency());
			orderRequest.put("receipt", "-RCPT-"
					+ bulkSmsPaymentRepository.countByDoctorId(new ObjectId(request.getDoctorId())) + generateId());
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
			System.out.println(output.toString());
			OrderReponse list = mapper.readValue(output.toString(), OrderReponse.class);

			if (user != null) {
				BulkSmsPaymentCollection collection = new BulkSmsPaymentCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setCreatedBy(user.getTitle() + " " + user.getFirstName());
				collection.setOrderId(list.getId().toString());
				collection.setReciept(list.getReceipt().toString());
				collection.setTransactionStatus("PENDING");
				collection = bulkSmsPaymentRepository.save(collection);
				response = new BulkSmsPaymentResponse();
				BeanUtil.map(collection, response);
			}
			String collectionId = response.getId();
			String orderId = response.getOrderId();

			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					auditService.addAuditData(AuditActionType.BUY_SMS_CREDITS, orderId, collectionId, null,
							request.getDoctorId(), request.getLocationId(), null);

				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
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

	@Override
	public Boolean verifySignature(PaymentSignatureRequest request) {
		Boolean response = false;
		try {
			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);

			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "doctor not found");
			}

			JSONObject options = new JSONObject();
			options.put("razorpay_order_id", request.getOrderId());
			options.put("razorpay_payment_id", request.getPaymentId());
			options.put("razorpay_signature", request.getSignature());
			response = Utils.verifyPaymentSignature(options, secret);
			if (response) {
				Criteria criteria = new Criteria("orderId").is(request.getOrderId()).and("doctorId")
						.is(new ObjectId(request.getDoctorId())).and("transactionStatus").is("PENDING");
				BulkSmsPaymentCollection onlinePaymentCollection = mongoTemplate.findOne(new Query(criteria),
						BulkSmsPaymentCollection.class);
				onlinePaymentCollection.setTransactionId(request.getPaymentId());
				onlinePaymentCollection.setTransactionStatus("SUCCESS");
				onlinePaymentCollection.setCreatedTime(new Date());
				onlinePaymentCollection.setUpdatedTime(new Date());
				bulkSmsPaymentRepository.save(onlinePaymentCollection);

				response = true;
				if (onlinePaymentCollection.getTransactionStatus().equalsIgnoreCase("SUCCESS")) {
					if (doctor != null) {

						BulkSmsHistoryCollection history = new BulkSmsHistoryCollection();
						BulkSmsPackageCollection packageCollection = bulkSmsRepository
								.findById(new ObjectId(request.getBulkSmsPackageId())).orElse(null);

						BulkSmsPackage bulkPackage = new BulkSmsPackage();
						BeanUtil.map(packageCollection, bulkPackage);

						if (packageCollection == null)
							throw new BusinessException(ServiceError.InvalidInput, "Sms Package not found");

						DoctorCollection doctorClinicProfileCollections = null;
						doctorClinicProfileCollections = doctorRepository
								.findByUserId(new ObjectId(request.getDoctorId()));

						Long creditBalance = 0L;
						BulkSmsCredits credit = new BulkSmsCredits();
						if (doctorClinicProfileCollections != null) {
							credit.setSmsPackage(bulkPackage);
							credit.setDateOfTransaction(new Date());
							credit.setPaymentMode(request.getMode());
							credit.setDoctorId(request.getDoctorId());
							credit.setLocationId(request.getLocationId());
							// BeanUtil.map(request,credit);
							if (doctorClinicProfileCollections.getBulkSmsCredit() != null)
								creditBalance = doctorClinicProfileCollections.getBulkSmsCredit().getCreditBalance();

							doctorClinicProfileCollections.setBulkSmsCredit(credit);
							creditBalance = creditBalance + packageCollection.getSmsCredit();
							credit.setCreditBalance(creditBalance);
							doctorClinicProfileCollections.getBulkSmsCredit().setCreditBalance(creditBalance);
							BeanUtil.map(credit, history);
							history.setCreatedTime(new Date());
							history.setUpdatedTime(new Date());
							bulkSmsHistoryRepository.save(history);
							doctorClinicProfileCollections.setUpdatedTime(new Date());
							doctorRepository.save(doctorClinicProfileCollections);

						}

						String message = "";
						message = StringEscapeUtils.unescapeJava(message);

						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

						smsTrackDetail.setType(ComponentType.ONLINE_PAYMENT.getType());
						smsTrackDetail.setDoctorId(doctor.getId());
						SMSDetail smsDetail = new SMSDetail();

						SMS sms = new SMS();

						String pattern = "dd/MM/yyyy";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

						sms.setSmsText(
								"Hello " + doctor.getFirstName() + ", your Payment has been done successfully on Date: "
										+ simpleDateFormat.format(onlinePaymentCollection.getCreatedTime())
										+ ", Mode of Payment: " + onlinePaymentCollection.getMode()
										+ " and your ReceiptId is" + onlinePaymentCollection.getReciept()
										+ " Total Cost :" + onlinePaymentCollection.getDiscountAmount() + ", Plan: "
										+ bulkPackage.getPackageName() + ".");

						SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(doctor.getMobileNumber());
						sms.setSmsAddress(smsAddress);
						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setSmsDetails(smsDetails);
						smsTrackDetail.setTemplateId("1307161561939795948");
						sMSServices.sendSMS(smsTrackDetail, true);
					}
				}
			}

		} catch (RazorpayException e) {
			// Handle Exception
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	// @Scheduled(cron = "0 0 20 * * ?", zone = "IST")
	@Override
	public Boolean bulkSmsCreditCheck() {
		DoctorCollection doctorCollection = null;
		Boolean response = false;

		try {

			List<DoctorCollection> doctorExperiences = doctorRepository.findAll();
			BulkSmsHistoryCollection history = new BulkSmsHistoryCollection();
			for (DoctorCollection doctorEperience : doctorExperiences) {

				doctorCollection = doctorRepository.findByUserId(doctorEperience.getUserId());
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				Date date = new Date();
				Integer totalLength = 160;
				Integer messageLength = null;
				if (doctorCollection != null)
					if (doctorCollection.getBulkSmsCredit() != null) {

						smsTrackDetail = smsTrackRepository.findByDoctorIdAndCreatedTime(doctorEperience.getUserId(),
								date);

						if (smsTrackDetail != null) {
							if (smsTrackDetail.getType() == "BULK_SMS") {

								for (SMSDetail smsDetail : smsTrackDetail.getSmsDetails()) {

									if (smsDetail.getSms() != null && smsDetail.getDeliveryStatus() != null
											&& smsDetail.getSms().getSmsAddress().getRecipient() != null) {
										if (!smsDetail.getDeliveryStatus().equals(SMSStatus.DELIVERED)
												&& !smsDetail.getDeliveryStatus().equals(SMSStatus.IN_PROGRESS)) {

											messageLength = smsDetail.getSms().getSmsText().length();
											long credits = (messageLength / totalLength);
											long temp = messageLength % totalLength;
											if (credits == 0 || temp != 0)
												credits = credits + 1;

											long subCredits = credits * (smsTrackDetail.getSmsDetails().size());
											doctorCollection.getBulkSmsCredit().setCreditBalance(subCredits);
											BeanUtil.map(doctorCollection.getBulkSmsCredit(), history);
											history.setCreatedTime(new Date());
											history.setUpdatedTime(new Date());
											history.setNote("credit refunded");
											doctorRepository.save(doctorCollection);
											bulkSmsHistoryRepository.save(history);
										}
									}
								}
							}
							smsTrackRepository.save(smsTrackDetail);
							response = true;
						}
					}

			}

		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;

	}

	@Override
	public MessageStatus getSmsStatus(String messageId) {

		MessageStatus response = null;
		try {
			List<String> messageStatus = new ArrayList<String>();

			messageStatus.add(messageId);
			String url = null;

			url = "https://api.ap.kaleyra.io/v1/" + SID + "/messages/status?message_ids=" + messageStatus;

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			String numberString = StringUtils.join(messageStatus, ',');
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.get().addParameter("message_ids", numberString)

					.setUri(url).setHeader("api-key", KEY).build();
			org.apache.http.HttpResponse responses = client.execute(httprequest);
			responses.getEntity().writeTo(out);
			ObjectMapper mapper = new ObjectMapper();
			response = mapper.readValue(out.toString(), MessageStatus.class);

		} catch (Exception e) {

			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Sms status");
		}
		return response;

	}

}

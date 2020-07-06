package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
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
import com.dpdocter.beans.BulkSmsReport;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.BulkSmsCreditsCollection;
import com.dpdocter.collections.BulkSmsHistoryCollection;
import com.dpdocter.collections.BulkSmsPackageCollection;
import com.dpdocter.collections.BulkSmsPaymentCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.PaymentMode;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BulkSmsCreditsRepository;
import com.dpdocter.repository.BulkSmsPackageRepository;
import com.dpdocter.repository.BulkSmsPaymentRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;
import com.dpdocter.services.BulkSmsServices;
import com.dpdocter.services.SMSServices;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import common.util.web.DPDoctorUtils;

@Service
public class BulkSmsServiceImpl implements BulkSmsServices{

	private static Logger logger = LogManager.getLogger(BulkSmsServiceImpl.class.getName());
	
	@Autowired
	private BulkSmsPackageRepository bulkSmsRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private BulkSmsCreditsRepository bulkSmsCreditRepository;
	
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


	
	@Override
	public BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			if(!DPDoctorUtils.anyStringEmpty(request.getId())) {
				bulkSms=bulkSmsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if(bulkSms==null) {
					throw new BusinessException(ServiceError.Unknown,"Id not found");
				}
				BeanUtil.map(request, bulkSms);
				bulkSms.setUpdatedTime(new Date());
			}else {
				bulkSms=new BulkSmsPackageCollection();
				BeanUtil.map(request, bulkSms);
				bulkSms.setCreatedTime(new Date());
				bulkSms.setUpdatedTime(new Date());
				
			}
			bulkSmsRepository.save(bulkSms);
			response=new BulkSmsPackage();
			BeanUtil.map(bulkSms, response);
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while addEdit Bulksms package"+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsPackage> getBulkSmsPackage(int page, int size, String searchTerm, Boolean discarded) {
		List<BulkSmsPackage> response=null;
		try {
		
			
			Criteria criteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				}
				response = mongoTemplate.aggregate(aggregation, BulkSmsPackageCollection.class, BulkSmsPackage.class).getMappedResults();
			
			}catch (BusinessException e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
			}
		
		return response;
	}


	@Override
	public Integer CountBulkSmsPackage(String searchTerm, Boolean discarded) {
		Integer count=null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			
			count=(int) mongoTemplate.count(new Query(criteria), BulkSmsPackageCollection.class);
		
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
		return count;
	}


	@Override
	public BulkSmsPackage getBulkSmsPackageByDoctorId(String doctorId,String locationId) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			if(!DPDoctorUtils.anyStringEmpty(doctorId)) {
				bulkSms=bulkSmsRepository.findByDoctorId(new ObjectId(doctorId));
				if(bulkSms==null) {
					throw new BusinessException(ServiceError.Unknown,"Id not found");
				}
				response=new BulkSmsPackage();
				BeanUtil.map(bulkSms, response);
			}
	}
		catch (BusinessException e) {
		e.printStackTrace();
		throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
	return response;
	}
	
	@Override
	public BulkSmsCredits getCreditsByDoctorId(String doctorId) {
		BulkSmsCredits response=null;
		try {
			BulkSmsCreditsCollection bulk=new BulkSmsCreditsCollection();
			bulk=bulkSmsCreditRepository.findByDoctorId(new ObjectId(doctorId));
			
			if(bulk==null)
			{
				throw new BusinessException(ServiceError.Unknown,"DoctorId not found for bulk sms");
			}
			response=new BulkSmsCredits();
			BeanUtil.map(bulk,response);
			
			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms credits count"+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsCredits> getBulkSmsHistory(int page, int size, String searchTerm, String doctorId,String locationId) {
		List<BulkSmsCredits> response=null;
		try {
		
			
			Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				ObjectId doctorObjectId=new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				}
				response = mongoTemplate.aggregate(aggregation, BulkSmsHistoryCollection.class, BulkSmsCredits.class).getMappedResults();
			
			}catch (BusinessException e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
			}
		
			return response;
		}
	
	@Override
	public List<BulkSmsReport> getSmsReport(int page, int size, String searchTerm, String doctorId, String locationId) {
		List<BulkSmsReport> response=null;
		try {
	Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				ObjectId doctorObjectId=new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			
			if(!DPDoctorUtils.anyStringEmpty(locationId))
			{
				ObjectId locationObjectId=new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				}
				response = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class, BulkSmsReport.class).getMappedResults();

			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
		return response;
	}


	@Override
	public BulkSmsPaymentResponse addCredits(OrderRequest request) {
		Order order = null;
		BulkSmsPaymentResponse response = null;
		try {
			RazorpayClient rayzorpayClient = new RazorpayClient(keyId, secret);
			JSONObject orderRequest = new JSONObject();
			
			BulkSmsPackageCollection bulkPackage=bulkSmsRepository.findById(new ObjectId(request.getBulkSmsPackageId())).orElse(null);
			
			if(bulkPackage==null)
			{
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
			orderRequest.put("receipt",  "-RCPT-"
					+ bulkSmsPaymentRepository.countByDoctorId(new ObjectId(request.getDoctorId()))
					+ generateId());
			orderRequest.put("payment_capture", request.getPaymentCapture());

			order = rayzorpayClient.Orders.create(orderRequest);

			if (user != null) {
				BulkSmsPaymentCollection collection = new BulkSmsPaymentCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setCreatedBy(user.getTitle() + " " + user.getFirstName());
				collection.setOrderId(order.get("id").toString());
				collection.setReciept(order.get("receipt").toString());
				collection.setTransactionStatus("PENDING");
				collection = bulkSmsPaymentRepository.save(collection);
				response = new BulkSmsPaymentResponse();
				BeanUtil.map(collection, response);
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
				
			
				response=true;
		//		user.setPaymentStatus(true);
		//		String regNo = user.getRegNo().replace("TEM", "");
	//			user.setRegNo(conferenceCollection.getTitle().substring(0, 2).toUpperCase() + regNo);
				doctor = userRepository.save(doctor);
				if (onlinePaymentCollection.getTransactionStatus().equalsIgnoreCase("SUCCESS")) {
					if (doctor != null) {
						
						BulkSmsCreditsCollection creditCollection=new BulkSmsCreditsCollection();
						BulkSmsPackageCollection packageCollection=bulkSmsRepository.findById(new ObjectId(request.getBulkSmsPackageId())).orElse(null);
					
						if(packageCollection==null)
							throw new BusinessException(ServiceError.InvalidInput, "Sms Package not found");
						
						creditCollection.setDoctorId(doctor.getId());
						creditCollection.setCreditBalance(packageCollection.getSmsCredits());
						creditCollection.setDateOfTransaction(new Date());
						creditCollection.setPaymentMode(request.getMode());
						creditCollection.setCreatedTime(new Date());
						bulkSmsCreditRepository.save(creditCollection);
						
						
						
						String message = "";
						message = StringEscapeUtils.unescapeJava(message);
						 SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							
							smsTrackDetail.setType(ComponentType.ONLINE_PAYMENT.getType());
							SMSDetail smsDetail = new SMSDetail();
							
						
							SMS sms = new SMS();
						
							String pattern = "dd/MM/yyyy";
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
							sms.setSmsText("Hi " + doctor.getFirstName() + ", your Payment has been done successfully on Date: "+simpleDateFormat.format(onlinePaymentCollection.getCreatedTime())
							+ " by "+onlinePaymentCollection.getMode()+" and your transactionId is"+onlinePaymentCollection.getTransactionId()+" for the receipt "+onlinePaymentCollection.getReciept()
							+" and the total cost is "+ onlinePaymentCollection.getDiscountAmount() + ".");
	
								SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(doctor.getMobileNumber());
							sms.setSmsAddress(smsAddress);
							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
							sMSServices.sendSMS(smsTrackDetail, false);
							
						System.out.println("sms sent");
						
						
						
//							pushNotificationServices.notifyUser(doctor.getId().toString(),
//									"You have received a payment from an", ComponentType.APPOINTMENT_REFRESH.getType(), null, null);
						
							
				
					}

				}
			}

		} catch (

		RazorpayException e) {
			// Handle Exception
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	

}

package com.dpdocter.services.impl;

import java.io.BufferedReader;
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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.beans.BulkSmsReport;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.OrderReponse;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSReport;
import com.dpdocter.collections.BulkSmsCreditsCollection;
import com.dpdocter.collections.BulkSmsHistoryCollection;
import com.dpdocter.collections.BulkSmsPackageCollection;
import com.dpdocter.collections.BulkSmsPaymentCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
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
import com.dpdocter.repository.BulkSmsHistoryRepository;
import com.dpdocter.repository.BulkSmsPackageRepository;
import com.dpdocter.repository.BulkSmsPaymentRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.OrderRequest;
import com.dpdocter.request.PaymentSignatureRequest;
import com.dpdocter.response.BulkSmsPaymentResponse;
import com.dpdocter.services.BulkSmsServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.squareup.okhttp.Credentials;

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
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private SMSTrackRepository smsTrackRepository;
	
	@Autowired
	private BulkSmsHistoryRepository bulkSmsHistoryRepository;
	
	
	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;




	
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


//	@Override
//	public BulkSmsPackage getBulkSmsPackageByDoctorId(String doctorId,String locationId) {
//		BulkSmsPackage response=null;
//		try {
//			BulkSmsPackageCollection bulkSms=null;
//			if(!DPDoctorUtils.anyStringEmpty(doctorId)) {
//				bulkSms=bulkSmsRepository.findById(new ObjectId(doctorId)).orElse(null);
//				if(bulkSms==null) {
//					throw new BusinessException(ServiceError.Unknown,"Id not found");
//				}
//				response=new BulkSmsPackage();
//				BeanUtil.map(bulkSms, response);
//			}
//	}
//		catch (BusinessException e) {
//		e.printStackTrace();
//		throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
//		}
//	return response;
//	}
//	
	@Override
	public List<BulkSmsCredits> getCreditsByDoctorIdAndLocationId(int size,int page,String searchTerm,String doctorId,String locationId) {
		List<BulkSmsCredits> response=null;
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
				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
						new Criteria("smsPackage.packageName").regex("^" + searchTerm));
			
			
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
			
			System.out.println("aggregation:"+aggregation);
				response = mongoTemplate.aggregate(aggregation, BulkSmsCreditsCollection.class, BulkSmsCredits.class).getMappedResults();
			
			

			
			
			
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
				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
						new Criteria("smsPackage.packageName").regex("^" + searchTerm));
			
			
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
	public List<BulkSmsReport> getSmsReport(int page, int size, String doctorId, String locationId) {
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
			
			
				criteria.and("type").is("BULK_SMS");
			
			
			
	
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
										
					.append("doctorId", "$doctorId")
					.append("locationId", "$locationId")					
					.append("smsDetails.sms.smsText", "$smsDetail.sms.smsText")
					.append("smsDetails.deliveryStatus", "$smsDetail.deliveryStatus")
					.append("smsDetails.sentTime", "$smsDetail.sentTime")
					.append("type", "$type")					
					.append("responseId", "$responseId")
					.append("delivered", "$delivered")
					.append("undelivered", "$undelivered")	
					.append("totalCreditsSpent", "$totalCreditSpent")));
//			
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("doctorId", new BasicDBObject("$first", "$doctorId"))
					.append("locationId", new BasicDBObject("$first", "$locationId"))
					.append("smsDetails", new BasicDBObject("$first", "$smsDetails"))
						.append("type", new BasicDBObject("$first", "$type"))
						.append("responseId", new BasicDBObject("$first", "$responseId"))
						.append("delivered", new BasicDBObject("$first", "$delivered"))
						.append("undelivered", new BasicDBObject("$first", "$undelivered"))
						.append("totalCreditsSpent", new BasicDBObject("$first", "$totalCreditsSpent"))));

//			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
//				criteria = criteria.orOperator(new Criteria("smsPackage.packageName").regex("^" + searchTerm, "i"),
//						new Criteria("smsPackage.packageName").regex("^" + searchTerm));
			
			
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
			
				System.out.println("Aggregation:"+aggregation);
				response = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class, BulkSmsReport.class).getMappedResults();

//				CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
//						new BasicDBObject("_id", "$_id")
//						.append("smsDetails", new BasicDBObject("$addToSet", "$smsDetails"))
//						));

				
				for(BulkSmsReport credit:response)
				{
					Long total=(long) credit.getSmsDetails().size();
					Integer totalLength=160; 
					String message=credit.getSmsDetails().get(0).getSms().getSmsText();
					  Integer messageLength=message.length();
					  System.out.println("messageLength:"+messageLength);
					  long credits=(messageLength/totalLength);
					  
					  long temp=messageLength%totalLength;
					  if(credits==0 || temp!=0) 
					  credits=credits+1;
					
					  long subCredits=credits*(total);
					
					Long count= mongoTemplate.count(new Query(new Criteria("smsDetails.deliveryStatus").is("DELIVERED").andOperator(criteria)),SMSTrackDetail.class);
					credit.setDelivered(count);
					credit.setUndelivered(total-count);
					credit.setTotalCreditsSpent(subCredits);
				}
//			
			
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
	//		RazorpayClient rayzorpayClient = new RazorpayClient(keyId, secret);
		
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

			String url="https://api.razorpay.com/v1/orders";
			 String authStr=keyId+":"+secret;
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
			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Authorization", "Basic " +  authStringEnc);
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
	 				System.out.println("response:"+output.toString());
	 			}
	 			
	 			  ObjectMapper mapper = new ObjectMapper();
	 			  
	 			 OrderReponse list = mapper.readValue(output.toString(),OrderReponse.class);
	 			//OrderReponse res=list.get(0); 
 			

	//		order = rayzorpayClient.Orders.create(orderRequest);

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
		}
		//	catch (RazorpayException e) {
//			// Handle Exception
//			
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}
			catch (Exception e) {
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
			//	doctor = userRepository.save(doctor);
				if (onlinePaymentCollection.getTransactionStatus().equalsIgnoreCase("SUCCESS")) {
					if (doctor != null) {
						
						BulkSmsHistoryCollection history=new BulkSmsHistoryCollection();
						BulkSmsPackageCollection packageCollection=bulkSmsRepository.findById(new ObjectId(request.getBulkSmsPackageId())).orElse(null);
					
						BulkSmsPackage bulkPackage=new BulkSmsPackage();
						BeanUtil.map(packageCollection,bulkPackage );
						
						if(packageCollection==null)
							throw new BusinessException(ServiceError.InvalidInput, "Sms Package not found");
						
						
						System.out.println("credits:"+packageCollection.getSmsCredit());
						DoctorCollection doctorClinicProfileCollections = null;
						doctorClinicProfileCollections = doctorRepository.findByUserId(
								new ObjectId(request.getDoctorId()));
						
						Long creditBalance=0L;
						BulkSmsCredits credit=new BulkSmsCredits();
						if (doctorClinicProfileCollections != null)
						{
							credit.setSmsPackage(bulkPackage);
							credit.setDateOfTransaction(new Date());
							credit.setPaymentMode(request.getMode());
							credit.setDoctorId(request.getDoctorId());
							credit.setLocationId(request.getLocationId());
							//BeanUtil.map(request,credit);
							if(doctorClinicProfileCollections.getBulkSmsCredit()!=null)
							 creditBalance=doctorClinicProfileCollections.getBulkSmsCredit().getCreditBalance();
							
							doctorClinicProfileCollections.setBulkSmsCredit(credit);
							creditBalance=creditBalance+packageCollection.getSmsCredit();
							credit.setCreditBalance(creditBalance);
							doctorClinicProfileCollections.getBulkSmsCredit().setCreditBalance(creditBalance);
							BeanUtil.map(credit, history);
							history.setCreatedTime(new Date());
							history.setUpdatedTime(new Date());
							bulkSmsHistoryRepository.save(history);
//							doctorClinicProfileCollections.getBulkSmsCredit().setDoctorId(request.getDoctorId());
//							doctorClinicProfileCollections.getBulkSmsCredit().setLocationId(request.getLocationId());
//							doctorClinicProfileCollections.getBulkSmsCredit().setCreditBalance(packageCollection.getSmsCredits());
//							doctorClinicProfileCollections.getBulkSmsCredit().setDateOfTransaction(new Date());
//							doctorClinicProfileCollections.getBulkSmsCredit().setPaymentMode(request.getMode());
//							doctorClinicProfileCollections.getBulkSmsCredit().setSmsPackage(bulkPackage);
							doctorClinicProfileCollections.setUpdatedTime(new Date());
							doctorRepository.save(doctorClinicProfileCollections);
							
						
						}
						
						
//						creditCollection.setDoctorId(doctor.getId());
//						creditCollection.getSmsPackage().setPackageName(packageCollection.getPackageName());
//						creditCollection.getSmsPackage().setSmsCredit(packageCollection.getSmsCredits());
//						creditCollection.getSmsPackage().setPrice(packageCollection.getPrice());
//						creditCollection.getSmsPackage().setId(packageCollection.getId().toString());
//						creditCollection.setCreditBalance(packageCollection.getSmsCredits());
//						creditCollection.setDateOfTransaction(new Date());
//						creditCollection.setPaymentMode(request.getMode());
//						creditCollection.setCreatedTime(new Date());
//						bulkSmsCreditRepository.save(creditCollection);
						
						
						
						String message = "";
						message = StringEscapeUtils.unescapeJava(message);
						 SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							
							smsTrackDetail.setType(ComponentType.ONLINE_PAYMENT.getType());
							smsTrackDetail.setDoctorId(doctor.getId());
							SMSDetail smsDetail = new SMSDetail();
							
						
							SMS sms = new SMS();
						
							String pattern = "dd/MM/yyyy";
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
							sms.setSmsText("Hello " + doctor.getFirstName() + ", your Payment has been done successfully on Date: "+simpleDateFormat.format(onlinePaymentCollection.getCreatedTime())
							+ ", Mode of Payment: "+onlinePaymentCollection.getMode()+" and your ReceiptId is"+onlinePaymentCollection.getReciept()+" Total Cost :"+ onlinePaymentCollection.getDiscountAmount()+", Plan: "+bulkPackage.getPackageName()+ ".");
	
								SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(doctor.getMobileNumber());
							sms.setSmsAddress(smsAddress);
							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
					Boolean res=sMSServices.sendSMS(smsTrackDetail, true);
							
						System.out.println("sms sent"+res);
						
						String paymentDate =simpleDateFormat.format(onlinePaymentCollection.getCreatedTime());
//						String body ="Hi " + doctor.getFirstName() + ", your Payment has been done successfully on Date: "+simpleDateFormat.format(onlinePaymentCollection.getCreatedTime())
//						+ " by "+onlinePaymentCollection.getMode()+" and your transactionId is"+onlinePaymentCollection.getTransactionId()+" for the bulk sms package "+bulkPackage.getPackageName()
//						+" and the total cost is "+ onlinePaymentCollection.getDiscountAmount() + ".";
					
						System.out.println("name"+ doctor.getFirstName());
						System.out.println("mode"+onlinePaymentCollection.getMode().getType());
						System.out.println("receipt"+ onlinePaymentCollection.getReciept());
						System.out.println("discount Amount"+ onlinePaymentCollection.getDiscountAmount());
						System.out.println("Package Name"+  bulkPackage.getPackageName());
						System.out.println("Payment Date"+ paymentDate);
						
						String body	= mailBodyGenerator.generateBulkSmsPayment(
								 doctor.getFirstName(),onlinePaymentCollection.getMode().getType(),onlinePaymentCollection.getReciept(),onlinePaymentCollection.getDiscountAmount(),
								 bulkPackage.getPackageName(),paymentDate
								, "bulkSmsTemplate.vm");
				Boolean mail=	mailService.sendEmail(doctor.getEmailAddress(),"Buy Bulk SMS Plan on Healthcoco+", body, null);
						System.out.println(mail);
						
						System.out.println("mail Status:"+mail); 
						
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

	
	@Scheduled(cron = "0 0 20 * * ?", zone = "IST")
	@Override
	public
	Boolean bulkSmsCreditCheck()
	{
		DoctorCollection doctorCollection = null;
		Boolean response = false;

		try {

			List<DoctorCollection> doctorExperiences = doctorRepository.findAll();
			BulkSmsHistoryCollection history=new BulkSmsHistoryCollection();
			for (DoctorCollection doctorEperience : doctorExperiences) {
					
				doctorCollection = doctorRepository.findByUserId(doctorEperience.getUserId());
				 SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				 Date date=new Date();
				 Integer totalLength=160; 
				 Integer messageLength=null;
				if (doctorCollection != null) 
					if(doctorCollection.getBulkSmsCredit()!=null) {
						
						smsTrackDetail=smsTrackRepository.findByDoctorIdAndCreatedTime(doctorEperience.getUserId(),date);
						
					if(smsTrackDetail !=null)
					{
						if(smsTrackDetail.getType()=="BULK_SMS")
						{
						
								for (SMSDetail smsDetail : smsTrackDetail.getSmsDetails()) {
					
										if (smsDetail.getSms() != null && smsDetail.getDeliveryStatus() != null
												&& smsDetail.getSms().getSmsAddress().getRecipient() != null) {
											if (smsDetail.getDeliveryStatus().equals(SMSStatus.REJECTED) || smsDetail.getDeliveryStatus().equals(SMSStatus.FAILED) ) {
												
												messageLength =smsDetail.getSms().getSmsText().length();
												  long credits=(messageLength/totalLength);
												  long temp=messageLength%totalLength;
												  if(credits==0 || temp!=0) 
												  credits=credits+1;
												  
												  long subCredits=credits*(smsTrackDetail.getSmsDetails().size());
												  doctorCollection.getBulkSmsCredit().setCreditBalance(subCredits);
												  BeanUtil.map(doctorCollection.getBulkSmsCredit(),history);
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

	

}

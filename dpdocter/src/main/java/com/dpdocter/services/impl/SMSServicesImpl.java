package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import com.dpdocter.beans.Message;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSReport;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.beans.UserMobileNumbers;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DoctorSMSResponse;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.services.SMSServices;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;

import common.util.web.DPDoctorUtils;

@Service
public class SMSServicesImpl implements SMSServices {
    private static Logger logger = Logger.getLogger(SMSServicesImpl.class);

    @Autowired
    private SMSTrackRepository smsTrackRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Value(value = "${AUTH_KEY}")
    private String AUTH_KEY;

    @Value(value = "${SENDER_ID}")
    private String SENDER_ID;

    @Value(value = "${DEFAULT_ROUTE}")
    private String ROUTE;

    @Value(value = "${DEFAULT_COUNTRY}")
    private String COUNTRY_CODE;

    @Value(value = "${SMS_POST_URL}")
    private String SMS_POST_URL;

    @Value("${is.env.production}")
    private Boolean isEnvProduction;

    @Value("${send.patient.otp.sms.from.twilio}")
    private Boolean sendSmsFromTwilio;

    @Value(value = "${mobile.numbers.resource}")
    private String MOBILE_NUMBERS_RESOURCE;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMSFormatRepository sMSFormatRepository;

    @Value(value = "${sms.twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value(value = "${sms.twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value(value = "${sms.twilio.country.code}")
    private String TWILIO_COUNTRY_CODE;

    @Value(value = "${sms.twilio.from.number}")
    private String TWILIO_FROM_NUMBER;

 	@Override
    @Transactional
    public Boolean sendSMS(SMSTrackDetail smsTrackDetail, Boolean save) {
    	Boolean response = false;
    	String responseId = null;
	try {
	    Message message = new Message();
	    List<SMS> smsList = new ArrayList<SMS>();
	    message.setAuthKey(AUTH_KEY);
	    message.setCountryCode(COUNTRY_CODE);
	    message.setRoute(ROUTE);
	    message.setSenderId(SENDER_ID);
	    
	    UserMobileNumbers userNumber = null;

	    if (!isEnvProduction) {
		FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		userNumber = (UserMobileNumbers) in.readObject();
		in.close();
		fileIn.close();
	    }
	    for (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) {
		if (!isEnvProduction) {
		    if (userNumber != null && smsDetails.getSms() != null && smsDetails.getSms().getSmsAddress() != null) {
			String recipient = smsDetails.getSms().getSmsAddress().getRecipient();
			if (userNumber.mobileNumber.contains(recipient)) {
			    smsDetails.getSms().getSmsAddress().setRecipient(COUNTRY_CODE + recipient);
			    SMS sms = new SMS();
			    BeanUtil.map(smsDetails.getSms(), sms);
			    if(sms.getSmsText() != null)sms.setSmsText(UriUtils.encode(sms.getSmsText(),"UTF-8"));
			    smsList.add(sms);
			    message.setSms(smsList);
			    String xmlSMSData = createXMLData(message);
			    responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
			    smsTrackDetail.setResponseId(responseId);
			}
		    }
		} else {
		    SMS sms = new SMS();
		    BeanUtil.map(smsDetails.getSms(), sms);
		    if(sms.getSmsText() != null)sms.setSmsText(UriUtils.encode(sms.getSmsText(),"UTF-8"));
		    smsList.add(sms);
		    message.setSms(smsList);
		    String xmlSMSData = createXMLData(message);
		    responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
		    smsTrackDetail.setResponseId(responseId);
		}
	    }

	    if (save)smsTrackRepository.save(smsTrackDetail);
	    if(!DPDoctorUtils.anyStringEmpty(responseId))response = true;
	} catch (Exception e) {
	    logger.error("Error : " + e.getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }

    private static String hitSMSUrl(final String SMS_URL, String xmlSMSData) {
	StringBuffer response = null;
	try {
	    logger.info("Sending SMS...!");
	    URL url = new URL(SMS_URL);
	    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setDoOutput(true);
	    httpConnection.setDoInput(true);
	    httpConnection.setRequestMethod("POST");

	    DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
	    wr.writeBytes(xmlSMSData);
	    wr.flush();
	    wr.close();
	    httpConnection.disconnect();

	    BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
	    String inputLine;
	    response = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	    }
	    logger.info("SMS Sent...!");
	    logger.info("Response : " + response.toString());

	    in.close();

	} catch (Exception e) {
	    logger.error("Error : " + e.getMessage());
	    response = new StringBuffer();
	    response.append("-2");
	}

	return response.toString();
    }

    private String createXMLData(Message message) {
	String xmlData = null;
	try {
	    JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

	    StringWriter xmlStringWriter = new StringWriter();

	    jaxbMarshaller.marshal(message, xmlStringWriter);

	    xmlData = "data=" + xmlStringWriter.toString();
	} catch (JAXBException e) {
	    logger.error("Error : " + e.getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return xmlData;
    }

    @Override
    @Transactional
    public SMSResponse getSMS(int page, int size, String doctorId, String locationId, String hospitalId) {
	SMSResponse response = null;
	List<SMSTrackDetail> smsTrackDetails = null;
	try {
		ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
		String[] type = {"APPOINTMENT", "PRESCRIPTION", "VISITS"};
	    if (doctorObjectId == null) {
		if (size > 0) smsTrackDetails = smsTrackRepository.findByLocationHospitalId(locationObjectId, hospitalObjectId, type, new PageRequest(page, size, Direction.DESC, "createdTime"));
		else smsTrackDetails = smsTrackRepository.findByLocationHospitalId(locationObjectId, hospitalObjectId, type, new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		if (size > 0)smsTrackDetails = smsTrackRepository.findByDoctorLocationHospitalId(doctorObjectId, locationObjectId, hospitalObjectId, type, new PageRequest(page, size, Direction.DESC, "createdTime"));
		else smsTrackDetails = smsTrackRepository.findByDoctorLocationHospitalId(doctorObjectId, locationObjectId, hospitalObjectId, type, new Sort(Sort.Direction.DESC, "createdTime"));
	    }

	    @SuppressWarnings("unchecked")
	    Collection<ObjectId> doctorIds = CollectionUtils.collect(smsTrackDetails, new BeanToPropertyValueTransformer("doctorId"));
	    if (doctorIds != null && !doctorIds.isEmpty()) {
		response = new SMSResponse();
		List<DoctorSMSResponse> doctors = getSpecifiedDoctors(doctorIds, locationObjectId, hospitalObjectId);
		response.setDoctors(doctors);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting SMS");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting SMS");
	}
	return response;
    }

    private List<DoctorSMSResponse> getSpecifiedDoctors(Collection<ObjectId> doctorIds, ObjectId locationId, ObjectId hospitalId) {
	List<DoctorSMSResponse> doctors = new ArrayList<DoctorSMSResponse>();
	for (ObjectId doctorId : doctorIds) {
	    DoctorSMSResponse doctorSMSResponse = new DoctorSMSResponse();
	    int count = smsTrackRepository.getDoctorsSMSCount(doctorId, locationId, hospitalId);
	    UserCollection user = userRepository.findOne(doctorId);
	    doctorSMSResponse.setDoctorId(doctorId.toString());
	    if (user != null)
		doctorSMSResponse.setDoctorName(user.getFirstName());
	    doctorSMSResponse.setMsgSentCount(count + "");
	    doctors.add(doctorSMSResponse);
	}
	return doctors;
    }

    @Override
    @Transactional
    public List<SMSTrack> getSMSDetails(int page, int size, String patientId, String doctorId, String locationId, String hospitalId) {
	List<SMSTrack> response = null;
	try {
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
    	Criteria criteria = new Criteria("type").in("APPOINTMENT", "PRESCRIPTION", "VISITS");
		
	    if (doctorObjectId == null) {
		if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId)) {
		    criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("smsDetails.userId").is(patientObjectId);
		}
	    } else {
			if (DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))criteria.and("doctorId").is(doctorObjectId).and("smsDetails.userId").is(patientObjectId);
			else criteria.and("doctorId").is(doctorObjectId).and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("smsDetails.userId").is(patientObjectId);
	    }
	    Aggregation aggregation = null;
	    if(size > 0){
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "smsDetails.sentTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
		}else{
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "smsDetails.sentTime")));
		}
	    AggregationResults<SMSTrack> aggregationResults = mongoTemplate.aggregate(aggregation, SMSTrackDetail.class, SMSTrack.class);
	    response = aggregationResults.getMappedResults();
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;

    }

    @Override
    @Transactional
    public void updateDeliveryReports(List<SMSDeliveryReports> request) {
	try {
	    for (SMSDeliveryReports smsDeliveryReport : request) {
		SMSTrackDetail smsTrackDetail = smsTrackRepository.findByResponseId(smsDeliveryReport.getRequestId());
		if (smsTrackDetail != null) {
		    for (SMSDetail smsDetail : smsTrackDetail.getSmsDetails()) {
			for (SMSReport report : smsDeliveryReport.getReport()) {
			    if (smsDetail.getSms() != null && smsDetail.getSms().getSmsAddress() != null
				    && smsDetail.getSms().getSmsAddress().getRecipient() != null) {
				if (smsDetail.getSms().getSmsAddress().getRecipient().equals(report.getNumber())) {
				    smsDetail.setDeliveredTime(report.getDate());
				    smsDetail.setDeliveryStatus(SMSStatus.valueOf(report.getDesc()));
				}
			    }
			}
		    }
		    smsTrackRepository.save(smsTrackDetail);
		}

	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public void addNumber(String mobileNumber) {
	try {
	    if (!isEnvProduction) {
		FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		UserMobileNumbers userNumber = (UserMobileNumbers) in.readObject();
		in.close();
		fileIn.close();

		if (!userNumber.mobileNumber.contains(mobileNumber))
		    userNumber.mobileNumber.add(mobileNumber);

		FileOutputStream fileOut = new FileOutputStream(MOBILE_NUMBERS_RESOURCE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(userNumber);
		out.close();
		fileOut.close();
		System.out.println(userNumber.mobileNumber);
	    }
	} catch (BusinessException | IOException | ClassNotFoundException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public void deleteNumber(String mobileNumber) {
	try {
	    if (!isEnvProduction) {
		FileInputStream fileIn = new FileInputStream(MOBILE_NUMBERS_RESOURCE);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		UserMobileNumbers userNumber = (UserMobileNumbers) in.readObject();
		in.close();
		fileIn.close();

		if (userNumber.mobileNumber.contains(mobileNumber))
		    userNumber.mobileNumber.remove(mobileNumber);

		FileOutputStream fileOut = new FileOutputStream(MOBILE_NUMBERS_RESOURCE);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(userNumber);
		out.close();
		fileOut.close();
	    }
	} catch (BusinessException | IOException | ClassNotFoundException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    @Transactional
    public SMSTrackDetail createSMSTrackDetail(String doctorId, String locationId, String hospitalId, String patientId, String patientName, String message,
	    String mobileNumber, String type) {
	SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
	try {
	    smsTrackDetail.setDoctorId(!DPDoctorUtils.anyStringEmpty(doctorId) ? new ObjectId(doctorId) : null);
	    smsTrackDetail.setHospitalId(!DPDoctorUtils.anyStringEmpty(locationId) ? new ObjectId(hospitalId) : null);
	    smsTrackDetail.setLocationId(!DPDoctorUtils.anyStringEmpty(hospitalId) ? new ObjectId(locationId) : null);
	    smsTrackDetail.setType(type);
	    SMSDetail smsDetail = new SMSDetail();
	    smsDetail.setUserId(!DPDoctorUtils.anyStringEmpty(patientId) ? new ObjectId(patientId) : null);
	    smsDetail.setUserName(patientName);
	    SMS sms = new SMS();
	    sms.setSmsText(message);

	    SMSAddress smsAddress = new SMSAddress();
	    smsAddress.setRecipient(mobileNumber);
	    sms.setSmsAddress(smsAddress);

	    smsDetail.setSms(sms);
	    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
	    smsDetails.add(smsDetail);
	    smsTrackDetail.setSmsDetails(smsDetails);

	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return smsTrackDetail;
    }

    @Override
    @Transactional
    public SMSFormat addSmsFormat(SMSFormat request) {
	SMSFormat response = null;
	SMSFormatCollection smsFormatCollection = null;
	try {
		ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))doctorObjectId = new ObjectId(request.getDoctorId());
    	if(!DPDoctorUtils.anyStringEmpty(request.getLocationId()))locationObjectId = new ObjectId(request.getLocationId());
    	if(!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))hospitalObjectId = new ObjectId(request.getHospitalId());
    	
	    smsFormatCollection = sMSFormatRepository.find(doctorObjectId, locationObjectId, hospitalObjectId, request.getType().getType());
	    if (smsFormatCollection == null) {
			smsFormatCollection = new SMSFormatCollection();
			BeanUtil.map(request, smsFormatCollection);
			smsFormatCollection.setCreatedTime(new Date());
	    } else {
			smsFormatCollection.setContent(request.getContent());
			smsFormatCollection.setUpdatedTime(new Date());
	    }
	    smsFormatCollection = sMSFormatRepository.save(smsFormatCollection);
	    response = new SMSFormat();
	    BeanUtil.map(smsFormatCollection, response);
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error while Adding/Editing Sms Format");
	}
	return response;
    }

    @Override
    @Transactional
    public List<SMSFormat> getSmsFormat(String doctorId, String locationId, String hospitalId, String type) {
	List<SMSFormat> response = null;
	List<SMSFormatCollection> smsFormatCollections = null;
	try {
		ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    if (type != null) {
		SMSFormatCollection smsFormatCollection = sMSFormatRepository.find(doctorObjectId, locationObjectId, hospitalObjectId, type);
		if (smsFormatCollection != null) {
		    smsFormatCollections = new ArrayList<SMSFormatCollection>();
		    smsFormatCollections.add(smsFormatCollection);
		}
	    } else
		smsFormatCollections = sMSFormatRepository.find(doctorObjectId, locationObjectId, hospitalObjectId);
	    if (smsFormatCollections != null) {
			response = new ArrayList<SMSFormat>();
			for(SMSFormatCollection smsFormatCollection : smsFormatCollections){
				SMSFormat smsFormat = new SMSFormat();
				BeanUtil.map(smsFormatCollection, smsFormat);
				response.add(smsFormat);
			}
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error while Sending Sms Format");
	}
	return response;
    }

	@Override
	public Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, Boolean save) throws TwilioRestException {
		Boolean response = false;
		String responseId = null;
		try{
			if (!isEnvProduction) {
				response = sendSMS(smsTrackDetail, save);
			}
			else{
				if(sendSmsFromTwilio){
					for (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) {
					    if (smsDetails.getSms() != null && smsDetails.getSms().getSmsAddress() != null) {
						String recipient = TWILIO_COUNTRY_CODE + smsDetails.getSms().getSmsAddress().getRecipient();
						smsDetails.getSms().getSmsAddress().setRecipient(recipient);
						SMS sms = new SMS();
						BeanUtil.map(smsDetails.getSms(), sms);
						TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
				    	params.add(new BasicNameValuePair("To", recipient));
				    	params.add(new BasicNameValuePair("From", TWILIO_FROM_NUMBER));//15005550006
				    	params.add(new BasicNameValuePair("Body", sms.getSmsText()));

				    	MessageFactory messageFactory = client.getAccount().getMessageFactory();
				        com.twilio.sdk.resource.instance.Message message = messageFactory.create(params);
				        responseId = message.getSid();
				        smsTrackDetail.setResponseId(responseId);
						}
				    }
				if(!DPDoctorUtils.anyStringEmpty(responseId))response = true;
				}else{
					response = sendSMS(smsTrackDetail, save);
				}
			}

			if (save)smsTrackRepository.save(smsTrackDetail);
			    			
		}catch (BusinessException e) {
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, "Error while sendind Sms");
		}
		return response;
	}
}

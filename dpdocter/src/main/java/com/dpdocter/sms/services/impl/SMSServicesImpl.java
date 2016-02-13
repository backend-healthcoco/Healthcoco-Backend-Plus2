package com.dpdocter.sms.services.impl;

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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

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
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.DoctorSMSResponse;
import com.dpdocter.response.SMSResponse;
import com.dpdocter.sms.repository.SMSTrackRepository;
import com.dpdocter.sms.services.SMSServices;

@Service
public class SMSServicesImpl implements SMSServices {
    private static Logger logger = Logger.getLogger(SMSServicesImpl.class);

    @Autowired
    private SMSTrackRepository smsTrackRepository;

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

    @Value("${IS_ENV_PRODUCTION}")
    private Boolean isEnvProduction;

    @Value(value = "${MOBILE_NUMBERS_RESOURCE}")
    private String MOBILE_NUMBERS_RESOURCE;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMSFormatRepository sMSFormatRepository;

    @Override
    public void sendSMS(SMSTrackDetail smsTrackDetail, Boolean save) {
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
			    smsList.add(sms);
			    message.setSms(smsList);
			    String xmlSMSData = createXMLData(message);
			    String responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
			    smsTrackDetail.setResponseId(responseId);
			}
		    }
		} else {
		    SMS sms = new SMS();
		    BeanUtil.map(smsDetails.getSms(), sms);
		    smsList.add(sms);
		    message.setSms(smsList);
		    String xmlSMSData = createXMLData(message);
		    String responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
		    smsTrackDetail.setResponseId(responseId);
		}
	    }

	    if (save)
		smsTrackRepository.save(smsTrackDetail);
	} catch (Exception e) {
	    logger.error("Error : " + e.getMessage());
	    throw new BusinessException(ServiceError.Forbidden, "Error : " + e.getMessage());
	}

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
	    throw new BusinessException(ServiceError.Forbidden, "Error : " + e.getMessage());
	}
	return xmlData;
    }

    @Override
    public SMSResponse getSMS(int page, int size, String doctorId, String locationId, String hospitalId) {
	SMSResponse response = null;
	List<SMSTrackDetail> smsTrackDetails = null;
	try {
	    if (doctorId == null) {
		if (size > 0)
		    smsTrackDetails = smsTrackRepository.findAll(locationId, hospitalId, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    smsTrackDetails = smsTrackRepository.findAll(locationId, hospitalId, new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    smsTrackDetails = smsTrackRepository.findAll(doctorId, locationId, hospitalId, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    smsTrackDetails = smsTrackRepository.findAll(doctorId, locationId, hospitalId, new Sort(Sort.Direction.DESC, "updatedTime"));
	    }

	    @SuppressWarnings("unchecked")
	    Collection<String> doctorIds = CollectionUtils.collect(smsTrackDetails, new BeanToPropertyValueTransformer("doctorId"));
	    if (doctorIds != null && !doctorIds.isEmpty()) {
		response = new SMSResponse();
		List<DoctorSMSResponse> doctors = getSpecifiedDoctors(doctorIds, locationId, hospitalId);
		response.setDoctors(doctors);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting SMS");
	    throw new BusinessException(ServiceError.Forbidden, "Error Occurred While Getting SMS");
	}
	return response;
    }

    private List<DoctorSMSResponse> getSpecifiedDoctors(Collection<String> doctorIds, String locationId, String hospitalId) {
	List<DoctorSMSResponse> doctors = new ArrayList<DoctorSMSResponse>();
	for (String doctorId : doctorIds) {
	    DoctorSMSResponse doctorSMSResponse = new DoctorSMSResponse();
	    int count = smsTrackRepository.getDoctorsSMSCount(doctorId, locationId, hospitalId);
	    UserCollection user = userRepository.findOne(doctorId);
	    doctorSMSResponse.setDoctorId(doctorId);
	    if (user != null)
		doctorSMSResponse.setDoctorName(user.getFirstName());
	    doctorSMSResponse.setMsgSentCount(count + "");
	    doctors.add(doctorSMSResponse);
	}
	return doctors;
    }

    @Override
    public List<SMSTrack> getSMSDetails(int page, int size, String patientId, String doctorId, String locationId, String hospitalId) {
	List<SMSTrack> response = null;
	List<SMSTrackDetail> smsTrackCollections = null;
	try {
	    if (doctorId == null) {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			smsTrackCollections = smsTrackRepository.findAll(new PageRequest(page, size, Direction.DESC, "sentTime")).getContent();
		    else
			smsTrackCollections = smsTrackRepository.findAll(new Sort(Sort.Direction.DESC, "sentTime"));
		} else {
		    if (size > 0)
			smsTrackCollections = smsTrackRepository.findByLocationHospitalPatientId(locationId, hospitalId, patientId,
				new PageRequest(page, size, Direction.DESC, "sentTime"));
		    else
			smsTrackCollections = smsTrackRepository.findByLocationHospitalPatientId(locationId, hospitalId, patientId,
				new Sort(Sort.Direction.DESC, "sentTime"));
		}
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			smsTrackCollections = smsTrackRepository.findAll(doctorId, patientId, new PageRequest(page, size, Direction.DESC, "sentTime"));
		    else
			smsTrackCollections = smsTrackRepository.findAll(doctorId, patientId, new Sort(Sort.Direction.DESC, "sentTime"));
		} else {
		    if (size > 0)
			smsTrackCollections = smsTrackRepository.findAll(doctorId, locationId, hospitalId, patientId,
				new PageRequest(page, size, Direction.DESC, "sentTime"));
		    else
			smsTrackCollections = smsTrackRepository.findAll(doctorId, locationId, hospitalId, patientId,
				new Sort(Sort.Direction.DESC, "sentTime"));
		}
	    }

	    if (smsTrackCollections != null) {
		response = new ArrayList<SMSTrack>();
		BeanUtil.map(smsTrackCollections, response);
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;

    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

    @Override
    public SMSTrackDetail createSMSTrackDetail(String doctorId, String locationId, String hospitalId, String patientId, String patientName, String message,
	    String mobileNumber, String type) {
	SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
	try {
	    smsTrackDetail.setDoctorId(doctorId);
	    smsTrackDetail.setHospitalId(hospitalId);
	    smsTrackDetail.setLocationId(locationId);
	    smsTrackDetail.setType(type);
	    SMSDetail smsDetail = new SMSDetail();
	    smsDetail.setUserId(patientId);
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return smsTrackDetail;
    }

    @Override
    public SMSFormat addSmsFormat(SMSFormat request) {
	SMSFormat response = null;
	SMSFormatCollection smsFormatCollection = null;
	try {
	    smsFormatCollection = sMSFormatRepository.find(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
		    request.getType().getType());
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
	    throw new BusinessException(ServiceError.Forbidden, "Error while Adding/Editing Sms Format");
	}
	return response;
    }

	@Override
	public List<SMSFormat> getSmsFormat(String doctorId, String locationId, String hospitalId, String type) {
		List<SMSFormat> response = null;
		List<SMSFormatCollection> smsFormatCollections = null;
		try {
			if(type != null){
				SMSFormatCollection smsFormatCollection = sMSFormatRepository.find(doctorId, locationId, hospitalId, type);
				if(smsFormatCollection != null){
					smsFormatCollections = new ArrayList<SMSFormatCollection>();
					smsFormatCollections.add(smsFormatCollection);
				}
			}
			else smsFormatCollections = sMSFormatRepository.find(doctorId, locationId, hospitalId);
			if(smsFormatCollections != null){
			    response = new ArrayList<SMSFormat>();
				BeanUtil.map(smsFormatCollections, response);
			}
		} catch (BusinessException e) {
		    logger.error(e);
		    throw new BusinessException(ServiceError.Forbidden, "Error while Adding/Editing Sms Format");
		}
	return response;
    }
}

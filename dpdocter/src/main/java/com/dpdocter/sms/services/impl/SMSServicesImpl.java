package com.dpdocter.sms.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Message;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
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

    @Override
    public void sendSMS(SMSTrackDetail smsTrackDetail) {
	try {
	    Message message = new Message();
	    List<SMS> smsList = new ArrayList<SMS>();
	    message.setAuthKey(AUTH_KEY);
	    message.setCountryCode(COUNTRY_CODE);
	    message.setRoute(ROUTE);
	    message.setSenderId(SENDER_ID);
	    for (SMSDetail smsDetails : smsTrackDetail.getSmsDetails()) {
		SMS sms = new SMS();
		BeanUtil.map(smsDetails.getSms(), sms);
		smsList.add(sms);
	    }
	    message.setSms(smsList);
	    String xmlSMSData = createXMLData(message);
	    String responseId = hitSMSUrl(SMS_POST_URL, xmlSMSData);
	    smsTrackDetail.setResponseId(responseId);
	    smsTrackRepository.save(smsTrackDetail);
	} catch (Exception e) {
	    logger.error("Error : " + e.getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
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
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return xmlData;
    }

}

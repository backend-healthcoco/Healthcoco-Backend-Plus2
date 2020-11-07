package com.dpdocter.services.impl;

import java.io.ByteArrayOutputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.MessageResponse;
import com.dpdocter.services.NDHMservices;
import com.dpdocter.webservices.LoginApi;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NDHMserviceImpl implements NDHMservices{

	private static Logger logger = LogManager.getLogger(NDHMserviceImpl.class.getName());
	
	@Value(value = "${ndhm.clientId}")
	private String NDHM_CLIENTID;
	
	@Value(value = "${ndhm.clientSecret}")
	private String NDHM_CLIENT_SECRET;
	
	
	@Override
	public NdhmOauthResponse session() {
		NdhmOauthResponse response=null;
		try {
			String url="https://dev.ndhm.gov.in/gateway/v0.5/sessions";
			ObjectMapper mapper = new ObjectMapper();
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
			HttpClient client = HttpClients.custom().build();
			HttpUriRequest httprequest = RequestBuilder.post().addParameter("clientId",NDHM_CLIENTID)
					.addParameter("clientSecret", NDHM_CLIENT_SECRET)
					
			  .setUri(url)
			  .setHeader( "Content-Type","application/json")
			  .build();
			//System.out.println("response"+client.execute(httprequest));
			
			 org.apache.http.HttpResponse responses = client.execute(httprequest);
			 responses.getEntity().writeTo(out);
			 response = mapper.readValue(out.toString(),NdhmOauthResponse.class);
			System.out.println("response"+out.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
		
	}

	
}

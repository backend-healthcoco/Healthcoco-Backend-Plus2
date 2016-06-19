package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESProfessionDocument;
import com.dpdocter.elasticsearch.repository.ESProfessionRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.Response;

@Component
@Path("testing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestExceptionAPI {

	@Autowired
    ESProfessionRepository esProfessionRepository;
	
    @GET
    @Path("/exception/{id}")
    public String exceptionTest(@PathParam("id") String id) throws BusinessException {
	throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
    }
    
    @GET
    @Path("/get")
    public Response<ESProfessionDocument> get(@PathParam("id") String id) {
	
    	ESProfessionDocument documents = esProfessionRepository.findOne("55f40520e4b0cb1d08c1700d");
    	
    	Response<ESProfessionDocument> response = new Response<ESProfessionDocument>();
    	response.setData(documents);
    	return response;
    }
}

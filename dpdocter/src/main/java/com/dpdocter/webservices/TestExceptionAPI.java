package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

@Component
@Path("testing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestExceptionAPI {

    @GET
    @Path("/exception/{id}")
    public String exceptionTest(@PathParam("id") String id) throws BusinessException {
	throw new BusinessException(ServiceError.InvalidInput, "Request send  is NULL");
    }
}

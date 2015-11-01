package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.User;
import com.dpdocter.repository.TRepository;
import common.util.web.Response;

@Component
@Path(PathProxy.USER_SERVICES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserServicesAPI {
    /*@Autowired
    private TestRepository testRepository;*/

    @Autowired
    private TRepository testAdvancedRepository;

    /* @Path(value = PathProxy.UserServiceUrls.GET_USER)
     @GET
     public Response<User> getUser(@PathParam(value = "username") String username) {
    User user = testRepository.findByUsername(username);
    Response<User> response = new Response<User>();
    response.setData(user);
    return response;
     }*/

    @Path(value = PathProxy.UserServiceUrls.GET_USER_BY_ID)
    @GET
    public Response<User> getUserById(@PathParam(value = "id") String id) {
	User user = testAdvancedRepository.findById(id);
	/*BeanUtil.map(userCollection, user);*/
	Response<User> response = new Response<User>();
	response.setData(user);
	return response;
    }
}

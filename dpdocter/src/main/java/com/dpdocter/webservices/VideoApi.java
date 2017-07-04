package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;

@Component
@Path(PathProxy.VIDEO_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.VIDEO_BASE_URL, description = "Endpoint for videos")
public class VideoApi {

}

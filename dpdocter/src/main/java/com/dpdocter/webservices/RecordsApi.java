package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ChangeRecordLabelRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.services.RecordsService;

import common.util.web.Response;

@Component
@Path(PathProxy.RECORDS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecordsApi {
	@Autowired
	private RecordsService recordsService;
	
	@POST
	@Path(value=PathProxy.RecordsUrls.ADD_RECORDS)
	//public Response<Records> addRecords(RecordsAddRequest request,@FormDataParam("file") InputStream fileInputStream,@FormDataParam("file") FormDataContentDisposition contentDispositionHeader){
	public Response<Records> addRecords(RecordsAddRequest request){
			if(request == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Records records = recordsService.addRecord(request);
		Response<Records> response = new Response<Records>();
		response.setData(records);
		return null;
	}
	
	@Path(value=PathProxy.RecordsUrls.TAG_RECORD)
	@POST
	public Response<Boolean> tagRecord(TagRecordRequest request){
		if(request == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		recordsService.tagRecord(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.RecordsUrls.SEARCH_RECORD)
	@POST
	public Response<Records> searchRecords(RecordsSearchRequest request){
		if(request == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<Records> records = recordsService.searchRecords(request);
		Response<Records> response = new Response<Records>();
		response.setDataList(records);
		return response;
	}
	
	@Path(value=PathProxy.RecordsUrls.CHANGE_LABEL_RECORD)
	@POST
	public Response<Boolean> changeLabel(ChangeRecordLabelRequest request){
		if(request == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		recordsService.changeReportLabel(request.getRecordId(), request.getLabel());
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	@Path(value=PathProxy.RecordsUrls.GET_ALL_TAGS)
	@GET
	public Response<Tags> getAllTags(
			@PathParam("doctorId")String doctorId,
			@PathParam("locationId")String locationId,
			@PathParam("hospitalId")String hospitalId){
		List<Tags> tags = recordsService.getAllTags(doctorId, locationId, hospitalId);
		Response<Tags> response = new Response<Tags>();
		response.setDataList(tags);
		return response;
	}
	
	@Path(value=PathProxy.RecordsUrls.CREATE_TAG)
	@POST
	public Response<Tags> createTag(Tags tags){
		if(tags == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		tags = recordsService.addEditTag(tags);
		Response<Tags> response = new Response<Tags>();
		response.setData(tags);
		return response;
	}
	
	@Path(value=PathProxy.RecordsUrls.GET_PATIENT_EMAIL_ADD)
	@GET
	public Response<String> getPatientEmailId(
			@PathParam("patientId")String patientId){
		String emailAdd = recordsService.getPatientEmailAddress(patientId);
		Response<String> response = new Response<String>();
		response.setData(emailAdd);
		return response;
	}
	

}

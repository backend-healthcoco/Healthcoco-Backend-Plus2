package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Records;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.ChangeRecordLabelRequest;
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
	
	/*@POST
	@Path(value=PathProxy.RecordsUrls.ADD_RECORDS)
	@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
	public Response<Records> addRecords(RecordsAddRequest request,@FormDataParam("file") InputStream fileInputStream,@FormDataParam("file") FormDataContentDisposition contentDispositionHeader){
		if(request == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		
		Records records = recordsService.addRecord(request, fileInputStream,contentDispositionHeader.getFileName());
		Response<Records> response = new Response<Records>();
		response.setData(records);
		return response;
	}*/
	
	@Path(value=PathProxy.RecordsUrls.TAG_RECORD)
	@POST
	public Response<Boolean> tagRecord(TagRecordRequest request){
		if(request == null){
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		recordsService.tagRecord(request.getTags(), request.getRecordId());
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
	

}

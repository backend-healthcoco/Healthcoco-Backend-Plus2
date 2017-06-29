package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;
import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;
import com.dpdocter.elasticsearch.services.ESDischargeSummaryService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_DISCHARGE_SUMMARY_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_DISCHARGE_SUMMARY_BASE_URL)
public class ESDischargeSummaryAPI {

	private Logger logger = Logger.getLogger(ESDischargeSummaryAPI.class);

	@Autowired
	private ESDischargeSummaryService esDischargeSummaryService;

	@Path(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_LABOUR_NOTES)
	@GET
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_LABOUR_NOTES, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_LABOUR_NOTES)
	public Response<EsLabourNoteDocument> searchLabourNotes(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<EsLabourNoteDocument> esLabourNoteDocuments = esDischargeSummaryService.searchLabourNotes(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<EsLabourNoteDocument> response = new Response<EsLabourNoteDocument>();
		response.setDataList(esLabourNoteDocuments);
		return response;
	}
	
	@Path(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_BABY_NOTES)
	@GET
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_BABY_NOTES, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_BABY_NOTES)
	public Response<ESBabyNoteDocument> searchBabyNotes(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESBabyNoteDocument> esBabyNoteDocuments = esDischargeSummaryService.searchBabyNotes(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESBabyNoteDocument> response = new Response<ESBabyNoteDocument>();
		response.setDataList(esBabyNoteDocuments);
		return response;
	}
	
	@Path(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_OPERATION_NOTES)
	@GET
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_OPERATION_NOTES, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_OPERATION_NOTES)
	public Response<ESOperationNoteDocument> searchNotes(@PathParam("range") String range,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded,
			@QueryParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESOperationNoteDocument> esOperationNoteDocuments = esDischargeSummaryService.searchOperationNotes(range, page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESOperationNoteDocument> response = new Response<ESOperationNoteDocument>();
		response.setDataList(esOperationNoteDocuments);
		return response;
	}


}

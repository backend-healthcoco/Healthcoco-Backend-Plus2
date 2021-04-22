package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;
import com.dpdocter.elasticsearch.document.ESCementDocument;
import com.dpdocter.elasticsearch.document.ESImplantDocument;
import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;
import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;
import com.dpdocter.elasticsearch.services.ESDischargeSummaryService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_DISCHARGE_SUMMARY_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_DISCHARGE_SUMMARY_BASE_URL)
public class ESDischargeSummaryAPI {

	private Logger logger = LogManager.getLogger(ESDischargeSummaryAPI.class);

	@Autowired
	private ESDischargeSummaryService esDischargeSummaryService;

	
	@GetMapping(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_LABOUR_NOTES)
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_LABOUR_NOTES, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_LABOUR_NOTES)
	public Response<EsLabourNoteDocument> searchLabourNotes(@PathVariable("range") String range,
			@RequestParam("page") long page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<EsLabourNoteDocument> esLabourNoteDocuments = esDischargeSummaryService.searchLabourNotes(range, page,
				size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<EsLabourNoteDocument> response = new Response<EsLabourNoteDocument>();
		response.setDataList(esLabourNoteDocuments);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_BABY_NOTES)
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_BABY_NOTES, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_BABY_NOTES)
	public Response<ESBabyNoteDocument> searchBabyNotes(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESBabyNoteDocument> esBabyNoteDocuments = esDischargeSummaryService.searchBabyNotes(range, page, size,
				doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESBabyNoteDocument> response = new Response<ESBabyNoteDocument>();
		response.setDataList(esBabyNoteDocuments);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_OPERATION_NOTES)
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_OPERATION_NOTES, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_OPERATION_NOTES)
	public Response<ESOperationNoteDocument> searchNotes(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESOperationNoteDocument> esOperationNoteDocuments = esDischargeSummaryService.searchOperationNotes(range,
				page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESOperationNoteDocument> response = new Response<ESOperationNoteDocument>();
		response.setDataList(esOperationNoteDocuments);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_CEMENT)
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_CEMENT, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_CEMENT)
	public Response<ESCementDocument> searchCement(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESCementDocument> esCementDocuments = esDischargeSummaryService.searchCement(range, page, size, doctorId,
				locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESCementDocument> response = new Response<ESCementDocument>();
		response.setDataList(esCementDocuments);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_IMPLANT)
	@ApiOperation(value = PathProxy.SolrDischargeSummaryUrls.SEARCH_IMPLANT, notes = PathProxy.SolrDischargeSummaryUrls.SEARCH_IMPLANT)
	public Response<ESImplantDocument> searchImplant(@PathVariable("range") String range, @RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@RequestParam(value = "searchTerm") String searchTerm) {
		if (DPDoctorUtils.anyStringEmpty(range, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESImplantDocument> esImplantDocuments = esDischargeSummaryService.searchImplant(range, page, size,
				doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
		Response<ESImplantDocument> response = new Response<ESImplantDocument>();
		response.setDataList(esImplantDocuments);
		return response;
	}

}

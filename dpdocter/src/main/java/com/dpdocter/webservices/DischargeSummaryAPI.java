package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.BabyNote;
import com.dpdocter.beans.Cement;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Implant;
import com.dpdocter.beans.LabourNote;
import com.dpdocter.beans.OperationNote;
import com.dpdocter.elasticsearch.document.ESBabyNoteDocument;
import com.dpdocter.elasticsearch.document.ESCementDocument;
import com.dpdocter.elasticsearch.document.ESImplantDocument;
import com.dpdocter.elasticsearch.document.ESOperationNoteDocument;
import com.dpdocter.elasticsearch.document.EsLabourNoteDocument;
import com.dpdocter.elasticsearch.services.ESDischargeSummaryService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditFlowSheetRequest;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.response.FlowsheetResponse;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.TransactionalManagementService;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DISCHARGE_SUMMARY_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DISCHARGE_SUMMARY_BASE_URL)
public class DischargeSummaryAPI {

	private Logger logger = LogManager.getLogger(DischargeSummaryAPI.class);

	@Autowired
	DischargeSummaryService dischargeSummaryService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESDischargeSummaryService esDischargeSummaryService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_DISCHARGE_SUMMARY)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.ADD_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> addEditDischargeSummary(DischargeSummaryRequest request) {
		Response<DischargeSummaryResponse> response = null;
		DischargeSummaryResponse dischargeSummary = null;
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(),request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput, "doctorId,patientId,locationId and hospitalId should not be null");
		}
		dischargeSummary = dischargeSummaryService.addEditDischargeSummary(request);
		if (dischargeSummary != null) {
			response = new Response<DischargeSummaryResponse>();
			response.setData(dischargeSummary);
		}

		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> getDischargeSummary(@QueryParam(value = "page") long page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime) {
		Response<DischargeSummaryResponse> response = null;
		List<DischargeSummaryResponse> dischargeSummaries = null;

		if (DPDoctorUtils.anyStringEmpty(patientId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor or patient id or locationId or hospitalId is null");
		}
		dischargeSummaries = dischargeSummaryService.getDischargeSummary(doctorId, locationId, hospitalId, patientId,
				page, size, updatedTime);
		response = new Response<DischargeSummaryResponse>();
		response.setDataList(dischargeSummaries);

		return response;

	}

	@Path(value = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.VIEW_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> viewDischargeSummary(
			@PathParam("dischargeSummeryId") String dischargeSummeryId) {
		Response<DischargeSummaryResponse> response = null;
		DischargeSummaryResponse dischargeSummary = null;

		if (dischargeSummeryId == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
		}
		// dischargeSummary = new DischargeSummary();
		dischargeSummary = dischargeSummaryService.viewDischargeSummary(dischargeSummeryId);
		if (dischargeSummary != null) {
			response = new Response<DischargeSummaryResponse>();
			response.setData(dischargeSummary);

		}
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_DISCHARGE_SUMMARY)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.DELETE_DISCHARGE_SUMMARY)
	public Response<DischargeSummaryResponse> deleteDischargeSummary(
			@PathParam(value = "dischargeSummeryId") String dischargeSummeryId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (StringUtils.isEmpty(dischargeSummeryId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("Discharge Summery  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Discharge Summery  Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		DischargeSummaryResponse dischargeSummaryResponse = dischargeSummaryService
				.deleteDischargeSummary(dischargeSummeryId, doctorId, hospitalId, locationId, discarded);
		Response<DischargeSummaryResponse> response = new Response<DischargeSummaryResponse>();
		response.setData(dischargeSummaryResponse);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.DOWNLOAD_DISCHARGE_SUMMARY)
	public Response<String> downloadDischargeSummary(@PathParam("dischargeSummeryId") String dischargeSummeryId) {
		Response<String> response = new Response<String>();
		response.setData(dischargeSummaryService.downloadDischargeSummary(dischargeSummeryId));
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY, notes = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY)
	public Response<Boolean> emailDischargeSummary(@PathParam(value = "dischargeSummeryId") String dischargeSummeryId,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(dischargeSummeryId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. dischargeSummeryId , Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. dischargeSummeryId, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		dischargeSummaryService.emailDischargeSummary(dischargeSummeryId, doctorId, locationId, hospitalId,
				emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY_WEB)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY_WEB, notes = PathProxy.DischargeSummaryUrls.EMAIL_DISCHARGE_SUMMARY_WEB)
	public Response<Boolean> emailDischargeSummaryForWeb(
			@PathParam(value = "dischargeSummeryId") String dischargeSummeryId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "hospitalId") String hospitalId,
			@PathParam(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(dischargeSummeryId, emailAddress)) {
			logger.warn(
					"Invalid Input. dischargeSummeryId , Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. dischargeSummeryId, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		dischargeSummaryService.emailDischargeSummaryForWeb(dischargeSummeryId, doctorId, locationId, hospitalId,
				emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_BY_VISIT)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_BY_VISIT, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_BY_VISIT)
	public Response<DischargeSummaryResponse> addMultiVisit(@MatrixParam("visitIds") List<String> visitIds) {

		if (visitIds == null || visitIds.isEmpty()) {
			logger.warn("Invalid Input Visit Ids  Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input Visit Ids  Cannot Be Empty");

		}
		Response<DischargeSummaryResponse> response = new Response<DischargeSummaryResponse>();
		response.setData(dischargeSummaryService.addMultiVisit(visitIds));
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.UPDATE_DISCHARGE_SUMMARY_DATA)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.UPDATE_DISCHARGE_SUMMARY_DATA, notes = PathProxy.DischargeSummaryUrls.UPDATE_DISCHARGE_SUMMARY_DATA)
	public Response<Integer> updateData() {

		Response<Integer> response = new Response<Integer>();
		response.setData(dischargeSummaryService.upadateDischargeSummaryData());

		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_LABOUR_NOTES)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_LABOUR_NOTES, notes = PathProxy.DischargeSummaryUrls.ADD_LABOUR_NOTES)
	public Response<LabourNote> addLabourNote(LabourNote request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getLabourNotes())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		LabourNote labourNote = dischargeSummaryService.addEditLabourNote(request);

		transactionalManagementService.addResource(new ObjectId(labourNote.getId()), Resource.LABOUR_NOTES, false);
		EsLabourNoteDocument esLabourNoteDocument = new EsLabourNoteDocument();
		BeanUtil.map(labourNote, esLabourNoteDocument);
		esDischargeSummaryService.addLabourNotes(esLabourNoteDocument);
		Response<LabourNote> response = new Response<LabourNote>();
		response.setData(labourNote);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_BABY_NOTES)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_BABY_NOTES, notes = PathProxy.DischargeSummaryUrls.ADD_BABY_NOTES)
	public Response<BabyNote> addBabyNote(BabyNote request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getBabyNotes())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		BabyNote babyNote = dischargeSummaryService.addEditBabyNote(request);
		transactionalManagementService.addResource(new ObjectId(babyNote.getId()), Resource.BABY_NOTES, false);
		ESBabyNoteDocument esBabyNoteDocument = new ESBabyNoteDocument();
		BeanUtil.map(babyNote, esBabyNoteDocument);
		esDischargeSummaryService.addBabyNote(esBabyNoteDocument);
		Response<BabyNote> response = new Response<BabyNote>();
		response.setData(babyNote);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_OPERATION_NOTES)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_OPERATION_NOTES, notes = PathProxy.DischargeSummaryUrls.ADD_OPERATION_NOTES)
	public Response<OperationNote> addOperationNote(OperationNote request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getOperationNotes())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		OperationNote operationNote = dischargeSummaryService.addEditOperationNote(request);
		transactionalManagementService.addResource(new ObjectId(operationNote.getId()), Resource.OPERATION_NOTES,
				false);
		ESOperationNoteDocument esOperationNoteDocument = new ESOperationNoteDocument();
		BeanUtil.map(operationNote, esOperationNoteDocument);
		esDischargeSummaryService.addOperationNote(esOperationNoteDocument);
		Response<OperationNote> response = new Response<OperationNote>();
		response.setData(operationNote);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_BABY_NOTES)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_BABY_NOTES, notes = PathProxy.DischargeSummaryUrls.DELETE_BABY_NOTES)
	public Response<BabyNote> deleteBabyNote(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Baby Notes Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Baby Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		BabyNote babyNote = dischargeSummaryService.deleteBabyNote(id, doctorId, locationId, hospitalId, discarded);
		if (babyNote != null) {
			transactionalManagementService.addResource(new ObjectId(babyNote.getId()), Resource.BABY_NOTES, false);
			ESBabyNoteDocument esBabyNoteDocument = new ESBabyNoteDocument();
			BeanUtil.map(babyNote, esBabyNoteDocument);
			esDischargeSummaryService.addBabyNote(esBabyNoteDocument);
		}
		Response<BabyNote> response = new Response<BabyNote>();
		response.setData(babyNote);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_LABOUR_NOTES)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_LABOUR_NOTES, notes = PathProxy.DischargeSummaryUrls.DELETE_LABOUR_NOTES)
	public Response<LabourNote> deleteLabourNote(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Labour Notes Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Labour Notes Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		LabourNote labourNote = dischargeSummaryService.deleteLabourNote(id, doctorId, locationId, hospitalId,
				discarded);
		if (labourNote != null) {
			transactionalManagementService.addResource(new ObjectId(labourNote.getId()), Resource.LABOUR_NOTES, false);
			EsLabourNoteDocument esLabourNoteDocument = new EsLabourNoteDocument();
			BeanUtil.map(labourNote, esLabourNoteDocument);
			esDischargeSummaryService.addLabourNotes(esLabourNoteDocument);
		}
		Response<LabourNote> response = new Response<LabourNote>();
		response.setData(labourNote);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_OPERAION_NOTES)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_OPERAION_NOTES, notes = PathProxy.DischargeSummaryUrls.DELETE_OPERAION_NOTES)
	public Response<OperationNote> deleteOperationNote(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("OperationNote Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Diagnosis Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		OperationNote operationNote = dischargeSummaryService.deleteOperationNote(id, doctorId, locationId, hospitalId,
				discarded);
		if (operationNote != null) {
			transactionalManagementService.addResource(new ObjectId(operationNote.getId()), Resource.OPERATION_NOTES,
					false);
			ESOperationNoteDocument esOperationNoteDocument = new ESOperationNoteDocument();
			BeanUtil.map(operationNote, esOperationNoteDocument);
			esDischargeSummaryService.addOperationNote(esOperationNoteDocument);
		}
		Response<OperationNote> response = new Response<OperationNote>();
		response.setData(operationNote);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_CEMENT)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_CEMENT, notes = PathProxy.DischargeSummaryUrls.ADD_CEMENT)
	public Response<Cement> addCement(Cement request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getCement())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Cement cement = dischargeSummaryService.addEditCement(request);
		transactionalManagementService.addResource(new ObjectId(cement.getId()), Resource.CEMENT, false);
		ESCementDocument esCementDocument = new ESCementDocument();
		BeanUtil.map(cement, esCementDocument);
		esDischargeSummaryService.addCement(esCementDocument);
		Response<Cement> response = new Response<Cement>();
		response.setData(cement);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_CEMENT)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_CEMENT, notes = PathProxy.DischargeSummaryUrls.DELETE_CEMENT)
	public Response<Cement> deleteCement(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Cement Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Cement Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Cement cement = dischargeSummaryService.deleteCement(id, doctorId, locationId, hospitalId, discarded);
		if (cement != null) {
			transactionalManagementService.addResource(new ObjectId(cement.getId()), Resource.CEMENT, false);
			ESCementDocument esCementDocument = new ESCementDocument();
			BeanUtil.map(cement, esCementDocument);
			esDischargeSummaryService.addCement(esCementDocument);
		}
		Response<Cement> response = new Response<Cement>();
		response.setData(cement);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_IMPLANT)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_IMPLANT, notes = PathProxy.DischargeSummaryUrls.ADD_IMPLANT)
	public Response<Implant> addImplant(Implant request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getImplant())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Implant implant = dischargeSummaryService.addEditImplant(request);
		transactionalManagementService.addResource(new ObjectId(implant.getId()), Resource.IMPLANT, false);
		ESImplantDocument esImplantDocument = new ESImplantDocument();
		BeanUtil.map(implant, esImplantDocument);
		esDischargeSummaryService.addImplant(esImplantDocument);
		Response<Implant> response = new Response<Implant>();
		response.setData(implant);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DELETE_IMPLANT)
	@DELETE
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DELETE_IMPLANT, notes = PathProxy.DischargeSummaryUrls.DELETE_IMPLANT)
	public Response<Implant> deleteImplant(@PathParam(value = "id") String id,
			@PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("implant Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"implant Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Implant implant = dischargeSummaryService.deleteImplant(id, doctorId, locationId, hospitalId, discarded);
		if (implant != null) {
			transactionalManagementService.addResource(new ObjectId(implant.getId()), Resource.IMPLANT, false);
			ESImplantDocument esImplantDocument = new ESImplantDocument();
			BeanUtil.map(implant, esImplantDocument);
			esDischargeSummaryService.addImplant(esImplantDocument);
		}
		Response<Implant> response = new Response<Implant>();
		response.setData(implant);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_ITEMS)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_ITEMS, notes = PathProxy.DischargeSummaryUrls.GET_DISCHARGE_SUMMARY_ITEMS)
	public Response<Object> getDischargeSummaryItems(@PathParam("type") String type, @PathParam("range") String range,
			@QueryParam("page") long page, @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
			@DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(type, range, doctorId)) {
			logger.warn("Invalid Input.");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
		}
		List<?> items = dischargeSummaryService.getDischargeSummaryItems(type, range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, null);

		Response<Object> response = new Response<Object>();
		response.setDataList(items);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.ADD_EDIT_FLOWSHEETS)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_EDIT_FLOWSHEETS, notes = PathProxy.DischargeSummaryUrls.ADD_EDIT_FLOWSHEETS)
	public Response<FlowsheetResponse> addEditFlowsheets(AddEditFlowSheetRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		FlowsheetResponse flowsheetResponse = dischargeSummaryService.addEditFlowSheets(request);

		Response<FlowsheetResponse> response = new Response<FlowsheetResponse>();
		response.setData(flowsheetResponse);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_FLOWSHEETS)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_FLOWSHEETS, notes = PathProxy.DischargeSummaryUrls.DOWNLOAD_FLOWSHEETS)
	public Response<String> downloadFlowSheet(@PathParam("id") String id) {
		Response<String> response = new Response<String>();
		response.setData(dischargeSummaryService.downloadFlowSheet(id, true));
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_FLOWSHEETS_BY_DISCHARGE_SUMMARY_ID)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.DOWNLOAD_FLOWSHEETS_BY_DISCHARGE_SUMMARY_ID, notes = PathProxy.DischargeSummaryUrls.DOWNLOAD_FLOWSHEETS_BY_DISCHARGE_SUMMARY_ID)
	public Response<String> downloadFlowSheetByDischargeSummaryId(@PathParam("dischargeSummaryId") String id) {
		Response<String> response = new Response<String>();
		response.setData(dischargeSummaryService.downloadFlowSheet(id, false));
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_FLOWSHEETS)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_FLOWSHEETS, notes = PathProxy.DischargeSummaryUrls.GET_FLOWSHEETS)
	public Response<FlowsheetResponse> getFlowSheets(@QueryParam(value = "page") int page,
			@QueryParam(value = "size") int size, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		Response<FlowsheetResponse> response = null;
		List<FlowsheetResponse> flowsheetResponses = null;

		if (DPDoctorUtils.anyStringEmpty(patientId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"Doctor or patient id or locationId or hospitalId is null");
		}
		flowsheetResponses = dischargeSummaryService.getFlowSheets(doctorId, locationId, hospitalId, patientId, page,
				size, updatedTime, discarded);
		response = new Response<FlowsheetResponse>();
		response.setDataList(flowsheetResponses);

		return response;

	}

	@Path(value = PathProxy.DischargeSummaryUrls.GET_FLOWSHEET_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.GET_FLOWSHEET_BY_ID, notes = PathProxy.DischargeSummaryUrls.GET_FLOWSHEET_BY_ID)
	public Response<FlowsheetResponse> getFlowSheetById(@PathParam("id") String id) {
		Response<FlowsheetResponse> response = null;
		FlowsheetResponse flowsheetResponses = null;

		if (DPDoctorUtils.anyStringEmpty(id)) {
			throw new BusinessException(ServiceError.InvalidInput, "Id is null");
		}
		flowsheetResponses = dischargeSummaryService.getFlowSheetsById(id);
		response = new Response<FlowsheetResponse>();
		response.setData(flowsheetResponses);

		return response;
	}


	@Path(value = PathProxy.DischargeSummaryUrls.ADD_DIAGRAM)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.ADD_DIAGRAM, notes = PathProxy.DischargeSummaryUrls.ADD_DIAGRAM)
	public Response<Diagram> addDiagram(Diagram request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())
				|| request.getDiagram() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Diagram diagram = dischargeSummaryService.addEditDiagram(request);

		if (diagram.getDiagramUrl() != null) {
			diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
		}

		Response<Diagram> response = new Response<Diagram>();
		response.setData(diagram);
		return response;
	}

	@Path(value = PathProxy.DischargeSummaryUrls.UPLOAD_DIAGRAM)
	@POST
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.UPLOAD_DIAGRAM, notes = PathProxy.DischargeSummaryUrls.UPLOAD_DIAGRAM)
	public Response<String> uploadDiagram(DoctorLabReportUploadRequest request) {
		if (request == null || request.getFileDetails() == null
				|| DPDoctorUtils.anyStringEmpty(request.getFileDetails().getFileEncoded())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		String diagram = dischargeSummaryService.uploadDischargeDiagram(request);

		Response<String> response = new Response<String>();
		response.setData(diagram);
		return response;
	}

	@POST
	@Path(value = PathProxy.DischargeSummaryUrls.UPLOAD_MULTIPART_DIAGRAM)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.DischargeSummaryUrls.UPLOAD_MULTIPART_DIAGRAM, notes = PathProxy.DischargeSummaryUrls.UPLOAD_MULTIPART_DIAGRAM)
	public Response<String> uploadDoctorLabReportMultipart(@FormDataParam("file") FormDataBodyPart file) {

		if (file == null || DPDoctorUtils.anyStringEmpty(file.getContentDisposition().getFileName())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		String recordsFile = dischargeSummaryService.uploadDischargeSummaryMultipart(file);
		Response<String> response = new Response<String>();
		response.setData(recordsFile);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}

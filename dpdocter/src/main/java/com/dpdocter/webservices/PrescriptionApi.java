package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.Prescription;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugStrengthAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugStrengthAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.services.SolrPrescriptionService;
import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Component
@Path(PathProxy.PRESCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrescriptionApi {

    private static Logger logger = Logger.getLogger(PrescriptionApi.class.getName());

    @Autowired
    private PrescriptionServices prescriptionServices;

    @Autowired
    private SolrPrescriptionService solrPrescriptionService;

    @Autowired
    private PatientVisitService patientTrackService;

    @Path(value = PathProxy.PrescriptionUrls.ADD_DRUG)
    @POST
    public Response<DrugAddEditResponse> addDrug(DrugAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugAddEditResponse drugAddEditResponse = prescriptionServices.addDrug(request);

	// Below service call will add the drug in solr index.
	SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
	BeanUtil.map(drugAddEditResponse, solrDrugDocument);
	solrPrescriptionService.addDrug(solrDrugDocument);
	Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
	response.setData(drugAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG)
    @PUT
    public Response<DrugAddEditResponse> editDrug(@PathParam(value = "drugId") String drugId, DrugAddEditRequest request) {
	if (StringUtils.isEmpty(drugId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugId);
	DrugAddEditResponse drugAddEditResponse = prescriptionServices.editDrug(request);

	// Below service call will add the drug in solr index.
	SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
	BeanUtil.map(drugAddEditResponse, solrDrugDocument);
	solrPrescriptionService.editDrug(solrDrugDocument);

	Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
	response.setData(drugAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG)
    @DELETE
    public Response<Boolean> deleteDrug(@PathParam(value = "drugId") String drugId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    logger.warn("Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	Boolean drugDeleteResponse = prescriptionServices.deleteDrug(drugId, doctorId, hospitalId, locationId, discarded);

	// Below service call will delete the drug in solr index.
	solrPrescriptionService.deleteDrug(drugId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_GLOBAL_DRUG)
    @DELETE
    public Response<Boolean> deleteDrug(@PathParam(value = "drugId") String drugId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugId)) {
	    logger.warn("Drug Id, Doctor Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Id, Doctor Id Cannot Be Empty");
	}
	Boolean drugDeleteResponse = prescriptionServices.deleteDrug(drugId, discarded);

	// Below service call will delete the drug in solr index.
	solrPrescriptionService.deleteDrug(drugId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.GET_DRUG_ID)
    @GET
    public Response<DrugAddEditResponse> getDrugDetails(@PathParam("drugId") String drugId) {
	if (drugId == null) {
	    logger.error("DrugId Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "DrugId Is NULL");
	}
	DrugAddEditResponse drugAddEditResponse = prescriptionServices.getDrugById(drugId);
	Response<DrugAddEditResponse> response = new Response<DrugAddEditResponse>();
	response.setData(drugAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_LAB_TEST)
    @POST
    public Response<LabTest> addLabTest(LabTest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	LabTest labTestResponse = prescriptionServices.addLabTest(request);

	SolrLabTestDocument solrLabTestDocument = new SolrLabTestDocument();
	BeanUtil.map(labTestResponse, solrLabTestDocument);
	solrPrescriptionService.addLabTest(solrLabTestDocument);
	Response<LabTest> response = new Response<LabTest>();
	response.setData(labTestResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_LAB_TEST)
    @PUT
    public Response<LabTest> editLabTest(@PathParam(value = "labTestId") String labTestId, LabTest request) {
	if (StringUtils.isEmpty(labTestId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(labTestId);
	LabTest labTestResponse = prescriptionServices.editLabTest(request);

	SolrLabTestDocument solrLabTestDocument = new SolrLabTestDocument();
	BeanUtil.map(labTestResponse, solrLabTestDocument);
	solrPrescriptionService.editLabTest(solrLabTestDocument);

	Response<LabTest> response = new Response<LabTest>();
	response.setData(labTestResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_LAB_TEST)
    @DELETE
    public Response<Boolean> deleteLabTest(@PathParam(value = "labTestId") String labTestId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(labTestId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    logger.warn("Lab Test Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Lab Test Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	Boolean labTestDeleteResponse = prescriptionServices.deleteLabTest(labTestId, doctorId, hospitalId, locationId, discarded);

	solrPrescriptionService.deleteLabTest(labTestId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(labTestDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_GLOBAL_LAB_TEST)
    @DELETE
    public Response<Boolean> deleteLabTest(@PathParam(value = "labTestId") String labTestId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(labTestId)) {
	    logger.warn("Lab Test Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Lab Test Id Cannot Be Empty");
	}
	Boolean labTestDeleteResponse = prescriptionServices.deleteLabTest(labTestId, discarded);

	solrPrescriptionService.deleteDrug(labTestId);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(labTestDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.GET_LAB_TEST_BY_ID)
    @GET
    public Response<LabTest> getLabTestDetails(@PathParam("labTestId") String labTestId) {
	if (labTestId == null) {
	    logger.error("Lab Test Id Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Lab Test Id Is NULL");
	}
	LabTest labTestResponse = prescriptionServices.getLabTestById(labTestId);
	Response<LabTest> response = new Response<LabTest>();
	response.setData(labTestResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE)
    @POST
    public Response<TemplateAddEditResponse> addTemplate(TemplateAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	TemplateAddEditResponse templateAddEditResponse = prescriptionServices.addTemplate(request);
	Response<TemplateAddEditResponse> response = new Response<TemplateAddEditResponse>();
	response.setData(templateAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE_HANDHELD)
    @POST
    public Response<TemplateAddEditResponseDetails> addTemplateHandheld(TemplateAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	TemplateAddEditResponseDetails templateAddEditResponse = prescriptionServices.addTemplateHandheld(request);
	Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
	response.setData(templateAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_TEMPLATE)
    @PUT
    public Response<TemplateAddEditResponseDetails> editTemplate(@PathParam(value = "templateId") String templateId, TemplateAddEditRequest request) {
	if (StringUtils.isEmpty(templateId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(templateId);
	TemplateAddEditResponseDetails templateAddEditResponse = prescriptionServices.editTemplate(request);
	Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
	response.setData(templateAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_TEMPLATE)
    @DELETE
    public Response<Boolean> deleteTemplate(@PathParam(value = "templateId") String templateId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(templateId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    logger.warn("Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	Boolean templateDeleteResponse = prescriptionServices.deleteTemplate(templateId, doctorId, hospitalId, locationId, discarded);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(templateDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_TEMPLATE_ID)
    @GET
    public Response<TemplateAddEditResponseDetails> getTemplate(@PathParam(value = "templateId") String templateId,
	    @PathParam(value = "doctorId") String doctorId, @PathParam(value = "locationId") String locationId,
	    @PathParam(value = "hospitalId") String hospitalId) {
	if (DPDoctorUtils.anyStringEmpty(templateId, doctorId, hospitalId, locationId)) {
	    logger.warn("Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	TemplateAddEditResponseDetails templateGetResponse = prescriptionServices.getTemplate(templateId, doctorId, hospitalId, locationId);
	Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
	response.setData(templateGetResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.GET_TEMPLATE)
    @GET
    public Response<TemplateAddEditResponseDetails> getAllTemplates(@QueryParam("page") int page, @QueryParam("size") int size,
	    @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
	    @DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	return getTemplates(page, size, doctorId, hospitalId, locationId, updatedTime, discarded != null ? discarded : true);
    }

    private Response<TemplateAddEditResponseDetails> getTemplates(int page, int size, String doctorId, String hospitalId, String locationId,
	    String updatedTime, boolean discarded) {
	List<TemplateAddEditResponseDetails> templates = prescriptionServices
		.getTemplates(page, size, doctorId, hospitalId, locationId, updatedTime, discarded);
	Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
	response.setDataList(templates);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION)
    @POST
    public Response<PrescriptionAddEditResponse> addPrescription(PrescriptionAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	PrescriptionAddEditResponse prescriptionAddEditResponse = prescriptionServices.addPrescription(request);

	// patient track
	if (prescriptionAddEditResponse != null) {
	    String visitId = patientTrackService.addRecord(prescriptionAddEditResponse, VisitedFor.PRESCRIPTION, prescriptionAddEditResponse.getVisitId());
	    prescriptionAddEditResponse.setVisitId(visitId);
	}

	Response<PrescriptionAddEditResponse> response = new Response<PrescriptionAddEditResponse>();
	response.setData(prescriptionAddEditResponse);
	return response;

    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION_HANDHELD)
    @POST
    public Response<PrescriptionAddEditResponseDetails> addPrescriptionHandheld(PrescriptionAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	PrescriptionAddEditResponseDetails prescriptionAddEditResponse = prescriptionServices.addPrescriptionHandheld(request);

	Response<PrescriptionAddEditResponseDetails> response = new Response<PrescriptionAddEditResponseDetails>();
	response.setData(prescriptionAddEditResponse);
	return response;

    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_PRESCRIPTION)
    @PUT
    public Response<PrescriptionAddEditResponseDetails> editPrescription(@PathParam(value = "prescriptionId") String prescriptionId,
	    PrescriptionAddEditRequest request) {
	if (StringUtils.isEmpty(prescriptionId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(prescriptionId);
	PrescriptionAddEditResponseDetails prescriptionAddEditResponse = prescriptionServices.editPrescription(request);

	Response<PrescriptionAddEditResponseDetails> response = new Response<PrescriptionAddEditResponseDetails>();
	response.setData(prescriptionAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION)
    @DELETE
    public Response<Boolean> deletePrescription(@PathParam(value = "prescriptionId") String prescriptionId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "patientId") String patientId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(prescriptionId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId) || StringUtils.isEmpty(locationId)) {
	    logger.warn("Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
	}
	Boolean prescriptionDeleteResponse = prescriptionServices.deletePrescription(prescriptionId, doctorId, hospitalId, locationId, patientId, discarded);
	Response<Boolean> response = new Response<Boolean>();
	response.setData(prescriptionDeleteResponse);
	return response;
    }

    @GET
    public Response<Prescription> getPrescription(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
	    @QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId, @QueryParam("patientId") String patientId,
	    @DefaultValue("false") @QueryParam("isOTPVerified") Boolean isOTPVerified, @DefaultValue("0") @QueryParam("updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam("discarded") Boolean discarded) {

	List<Prescription> prescriptions = null;

	prescriptions = prescriptionServices.getPrescriptions(page, size, doctorId, hospitalId, locationId, patientId, updatedTime, isOTPVerified, discarded);

	Response<Prescription> response = new Response<Prescription>();
	response.setDataList(prescriptions);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_COUNT)
    @GET
    public Response<Integer> getPrescriptionCount(@PathParam(value = "doctorId") String doctorId, @PathParam(value = "patientId") String patientId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId) {
	Integer prescriptionCount = prescriptionServices.getPrescriptionCount(doctorId, patientId, locationId, hospitalId);
	Response<Integer> response = new Response<Integer>();
	response.setData(prescriptionCount);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_DRUG_TYPE)
    @POST
    public Response<DrugTypeAddEditResponse> addDrugType(DrugTypeAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugTypeAddEditResponse drugTypeAddEditResponse = prescriptionServices.addDrugType(request);

	Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
	response.setData(drugTypeAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG_TYPE)
    @PUT
    public Response<DrugTypeAddEditResponse> editDrugType(@PathParam(value = "drugTypeId") String drugTypeId, DrugTypeAddEditRequest request) {
	if (StringUtils.isEmpty(drugTypeId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugTypeId);
	DrugTypeAddEditResponse drugTypeAddEditResponse = prescriptionServices.editDrugType(request);

	Response<DrugTypeAddEditResponse> response = new Response<DrugTypeAddEditResponse>();
	response.setData(drugTypeAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG_TYPE)
    @DELETE
    public Response<Boolean> deleteDrugType(@PathParam(value = "drugTypeId") String drugTypeId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugTypeId)) {
	    logger.warn("Drug Type Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Type Id Cannot Be Empty");
	}
	Boolean drugTypeDeleteResponse = prescriptionServices.deleteDrugType(drugTypeId, discarded);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugTypeDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_DRUG_STRENGTH)
    @POST
    public Response<DrugStrengthAddEditResponse> addDrugStrength(DrugStrengthAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugStrengthAddEditResponse drugStrengthAddEditResponse = prescriptionServices.addDrugStrength(request);

	Response<DrugStrengthAddEditResponse> response = new Response<DrugStrengthAddEditResponse>();
	response.setData(drugStrengthAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG_STRENGTH)
    @PUT
    public Response<DrugStrengthAddEditResponse> editDrugStrength(@PathParam(value = "drugStrengthId") String drugStrengthId, DrugStrengthAddEditRequest request) {
	if (StringUtils.isEmpty(drugStrengthId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugStrengthId);
	DrugStrengthAddEditResponse drugStrengthAddEditResponse = prescriptionServices.editDrugStrength(request);

	Response<DrugStrengthAddEditResponse> response = new Response<DrugStrengthAddEditResponse>();
	response.setData(drugStrengthAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG_STRENGTH)
    @DELETE
    public Response<Boolean> deleteDrugStrength(@PathParam(value = "drugStrengthId") String drugStrengthId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugStrengthId)) {
	    logger.warn("Drug Strength Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Strength Id Cannot Be Empty");
	}
	Boolean drugStrengthDeleteResponse = prescriptionServices.deleteDrugStrength(drugStrengthId, discarded);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugStrengthDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_DRUG_DOSAGE)
    @POST
    public Response<DrugDosageAddEditResponse> addDrugDosage(DrugDosageAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.addDrugDosage(request);

	Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
	response.setData(drugDosageAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DOSAGE)
    @PUT
    public Response<DrugDosageAddEditResponse> editDrugDosage(@PathParam(value = "drugDosageId") String drugDosageId, DrugDosageAddEditRequest request) {
	if (StringUtils.isEmpty(drugDosageId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugDosageId);
	DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.editDrugDosage(request);

	Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
	response.setData(drugDosageAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DOSAGE)
    @DELETE
    public Response<Boolean> deleteDrugDosage(@PathParam(value = "drugDosageId") String drugDosageId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugDosageId)) {
	    logger.warn("Drug Dosage Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Dosage Id Cannot Be Empty");
	}
	Boolean drugDosageDeleteResponse = prescriptionServices.deleteDrugDosage(drugDosageId, discarded);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugDosageDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_DRUG_DIRECTION)
    @POST
    public Response<DrugDirectionAddEditResponse> addDrugDirection(DrugDirectionAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.addDrugDirection(request);

	Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
	response.setData(drugDirectionAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DIRECTION)
    @PUT
    public Response<DrugDirectionAddEditResponse> editDrugDirection(@PathParam(value = "drugDirectionId") String drugDirectionId,
	    DrugDirectionAddEditRequest request) {
	if (StringUtils.isEmpty(drugDirectionId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugDirectionId);
	DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.editDrugDirection(request);

	Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
	response.setData(drugDirectionAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DIRECTION)
    @DELETE
    public Response<Boolean> deleteDrugDirection(@PathParam(value = "drugDirectionId") String drugDirectionId, @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugDirectionId)) {
	    logger.warn("Drug Direction Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Direction Id Cannot Be Empty");
	}
	Boolean drugDirectionDeleteResponse = prescriptionServices.deleteDrugDirection(drugDirectionId, discarded);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugDirectionDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.ADD_DRUG_DURATION_UNIT)
    @POST
    public Response<DrugDurationUnitAddEditResponse> addDrugDurationUnit(DrugDurationUnitAddEditRequest request) {
	if (request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices.addDrugDurationUnit(request);

	Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
	response.setData(drugDurationUnitAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DURATION_UNIT)
    @PUT
    public Response<DrugDurationUnitAddEditResponse> editDrugDurationUnit(@PathParam(value = "drugDurationUnitId") String drugDurationUnitId,
	    DrugDurationUnitAddEditRequest request) {
	if (StringUtils.isEmpty(drugDurationUnitId) || request == null) {
	    logger.warn("Request Sent Is NULL");
	    throw new BusinessException(ServiceError.InvalidInput, "Request Sent Is NULL");
	}
	request.setId(drugDurationUnitId);
	DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices.editDrugDurationUnit(request);

	Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
	response.setData(drugDurationUnitAddEditResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DURATION_UNIT)
    @DELETE
    public Response<Boolean> deleteDrugDurationUnit(@PathParam(value = "drugDurationUnitId") String drugDurationUnitId,
	    @QueryParam("discarded") Boolean discarded) {
	if (StringUtils.isEmpty(drugDurationUnitId)) {
	    logger.warn("Drug Duration Unit Id Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Drug Duration Unit Id Cannot Be Empty");
	}
	Boolean drugDurationUnitDeleteResponse = prescriptionServices.deleteDrugDurationUnit(drugDurationUnitId, discarded);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(drugDurationUnitDeleteResponse);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_ITEMS)
    @GET
    public Response<Object> getPrescriptionItems(@PathParam("type") String type, @PathParam("range") String range, @QueryParam("page") int page,
	    @QueryParam("size") int size, @QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "locationId") String locationId,
	    @QueryParam(value = "hospitalId") String hospitalId, @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime,
	    @DefaultValue("true") @QueryParam(value = "discarded") Boolean discarded) {

	if (DPDoctorUtils.anyStringEmpty(type, range)) {
	    logger.warn("Invalid Input. Type or Range Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput, "Invalid Input. Type or Range Cannot Be Empty");
	}
	List<Object> clinicalItems = prescriptionServices.getPrescriptionItems(type, range, page, size, doctorId, locationId, hospitalId, updatedTime,
		discarded);

	Response<Object> response = new Response<Object>();
	response.setDataList(clinicalItems);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION)
    @GET
    public Response<Boolean> emailPrescription(@PathParam(value = "prescriptionId") String prescriptionId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "emailAddress") String emailAddress) {

	if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, locationId, hospitalId, emailAddress)) {
	    logger.warn("Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput,
		    "Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
	}
	prescriptionServices.emailPrescription(prescriptionId, doctorId, locationId, hospitalId, emailAddress);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION)
    @GET
    public Response<Boolean> smsPrescription(@PathParam(value = "prescriptionId") String prescriptionId, @PathParam(value = "doctorId") String doctorId,
	    @PathParam(value = "locationId") String locationId, @PathParam(value = "hospitalId") String hospitalId,
	    @PathParam(value = "mobileNumber") String mobileNumber) {

	if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, locationId, hospitalId, mobileNumber)) {
	    logger.warn("Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
	    throw new BusinessException(ServiceError.InvalidInput,
		    "Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
	}
	prescriptionServices.smsPrescription(prescriptionId, doctorId, locationId, hospitalId, mobileNumber);

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.PrescriptionUrls.IMPORT_DRUG)
    @GET
    public Response<Boolean> importDrug() {
	prescriptionServices.importDrug();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

}

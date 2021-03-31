package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Advice;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.Instructions;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.NutritionReferral;
import com.dpdocter.beans.Prescription;
import com.dpdocter.elasticsearch.document.ESAdvicesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.NutritionReferralRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugInteractionResposne;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.PRESCRIPTION_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.PRESCRIPTION_BASE_URL, description = "Endpoint for prescription")
public class PrescriptionApi {

	private static Logger logger = LogManager.getLogger(PrescriptionApi.class.getName());
	
	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private PatientVisitService patientTrackService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private OTPService otpService;

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_DRUG)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_DRUG, notes = PathProxy.PrescriptionUrls.ADD_DRUG)
	public Response<Drug> addDrug(DrugAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getDrugName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Drug drugAddEditResponse = prescriptionServices.addDrug(request);

		transnationalService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
		if (drugAddEditResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugAddEditResponse, esDrugDocument);
			if (drugAddEditResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}

		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_DRUG)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_DRUG, notes = PathProxy.PrescriptionUrls.EDIT_DRUG)
	public Response<Drug> editDrug(@PathVariable(value = "drugId") String drugId, DrugAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(drugId, request.getDoctorId(), request.getHospitalId(),
				request.getLocationId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(drugId);
		Drug drugAddEditResponse = prescriptionServices.editDrug(request);

		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_DRUG)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_DRUG, notes = PathProxy.PrescriptionUrls.DELETE_DRUG)
	public Response<Drug> deleteDrug(@PathVariable(value = "drugId") String drugId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (StringUtils.isEmpty(drugId) || StringUtils.isEmpty(doctorId) || StringUtils.isEmpty(hospitalId)
				|| StringUtils.isEmpty(locationId)) {
			logger.warn("Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Drug Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Drug drugDeleteResponse = prescriptionServices.deleteDrug(drugId, doctorId, hospitalId, locationId, discarded);

		transnationalService.addResource(new ObjectId(drugId), Resource.DRUG, false);
		if (drugDeleteResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugDeleteResponse, esDrugDocument);
			if (drugDeleteResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugDeleteResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugDeleteResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}
		Response<Drug> response = new Response<Drug>();
		response.setData(drugDeleteResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_DRUG_ID)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_DRUG_ID, notes = PathProxy.PrescriptionUrls.GET_DRUG_ID)
	public Response<Drug> getDrugDetails(@PathVariable("drugId") String drugId) {
		if (drugId == null) {
			logger.error("DrugId Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "DrugId Is NULL");
		}
		Drug drugAddEditResponse = prescriptionServices.getDrugById(drugId);
		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_LAB_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_LAB_TEST, notes = PathProxy.PrescriptionUrls.ADD_LAB_TEST)
	public Response<LabTest> addLabTest(LabTest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getHospitalId())
				|| request.getTest() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		LabTest labTestResponse = prescriptionServices.addLabTest(request);
		transnationalService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestResponse, esLabTestDocument);
		if (labTestResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_LAB_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_LAB_TEST, notes = PathProxy.PrescriptionUrls.EDIT_LAB_TEST)
	public Response<LabTest> editLabTest(@PathVariable(value = "labTestId") String labTestId, LabTest request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(labTestId, request.getLocationId(), request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(labTestId);
		LabTest labTestResponse = prescriptionServices.editLabTest(request);
		transnationalService.addResource(new ObjectId(labTestResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestResponse, esLabTestDocument);
		if (labTestResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestResponse.getTest().getId());
		esPrescriptionService.addLabTest(esLabTestDocument);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_LAB_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_LAB_TEST, notes = PathProxy.PrescriptionUrls.DELETE_LAB_TEST)
	public Response<LabTest> deleteLabTest(@PathVariable(value = "labTestId") String labTestId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(labTestId, hospitalId, locationId)) {
			logger.warn("Lab Test Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Lab Test Id, Hospital Id, Location Id Cannot Be Empty");
		}
		LabTest labTestDeleteResponse = prescriptionServices.deleteLabTest(labTestId, hospitalId, locationId,
				discarded);
		transnationalService.addResource(new ObjectId(labTestDeleteResponse.getId()), Resource.LABTEST, false);
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestDeleteResponse, esLabTestDocument);
		if (labTestDeleteResponse.getTest() != null)
			esLabTestDocument.setTestId(labTestDeleteResponse.getTest().getId());
		if (esLabTestDocument.getId() != null)
			transnationalService.checkLabTest(new ObjectId(esLabTestDocument.getId()));
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestDeleteResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_LAB_TEST_BY_ID)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_LAB_TEST_BY_ID, notes = PathProxy.PrescriptionUrls.GET_LAB_TEST_BY_ID)
	public Response<LabTest> getLabTestDetails(@PathVariable("labTestId") String labTestId) {
		if (DPDoctorUtils.anyStringEmpty(labTestId)) {
			logger.error("Lab Test Id Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "Lab Test Id Is NULL");
		}
		LabTest labTestResponse = prescriptionServices.getLabTestById(labTestId);
		Response<LabTest> response = new Response<LabTest>();
		response.setData(labTestResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE, notes = PathProxy.PrescriptionUrls.ADD_TEMPLATE)
	public Response<TemplateAddEditResponse> addTemplate(TemplateAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getName()) || request.getItems() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		TemplateAddEditResponse templateAddEditResponse = prescriptionServices.addTemplate(request);
		Response<TemplateAddEditResponse> response = new Response<TemplateAddEditResponse>();
		response.setData(templateAddEditResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE_HANDHELD)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_TEMPLATE_HANDHELD, notes = PathProxy.PrescriptionUrls.ADD_TEMPLATE_HANDHELD)
	public Response<TemplateAddEditResponseDetails> addTemplateHandheld(TemplateAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getName()) || request.getItems() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		TemplateAddEditResponseDetails templateAddEditResponse = prescriptionServices.addTemplateHandheld(request);
		Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
		response.setData(templateAddEditResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_TEMPLATE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_TEMPLATE, notes = PathProxy.PrescriptionUrls.EDIT_TEMPLATE)
	public Response<TemplateAddEditResponseDetails> editTemplate(@PathVariable(value = "templateId") String templateId,
			TemplateAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(templateId, request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getName()) || request.getItems() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(templateId);
		TemplateAddEditResponseDetails templateAddEditResponse = prescriptionServices.editTemplate(request);
		Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
		response.setData(templateAddEditResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_TEMPLATE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_TEMPLATE, notes = PathProxy.PrescriptionUrls.DELETE_TEMPLATE)
	public Response<TemplateAddEditResponseDetails> deleteTemplate(@PathVariable(value = "templateId") String templateId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(templateId, doctorId, locationId, hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		TemplateAddEditResponseDetails templateDeleteResponse = prescriptionServices.deleteTemplate(templateId,
				doctorId, hospitalId, locationId, discarded);
		Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
		response.setData(templateDeleteResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_TEMPLATE_ID)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_TEMPLATE_TEMPLATE_ID, notes = PathProxy.PrescriptionUrls.GET_TEMPLATE_TEMPLATE_ID)
	public Response<TemplateAddEditResponseDetails> getTemplate(@PathVariable(value = "templateId") String templateId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(templateId, doctorId, hospitalId, locationId)) {
			logger.warn("Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Template Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		TemplateAddEditResponseDetails templateGetResponse = prescriptionServices.getTemplate(templateId, doctorId,
				hospitalId, locationId);
		Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
		response.setData(templateGetResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_TEMPLATE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_TEMPLATE, notes = PathProxy.PrescriptionUrls.GET_TEMPLATE)
	public Response<TemplateAddEditResponseDetails> getAllTemplates(@RequestParam("page") int page,
			@RequestParam("size") int size, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<TemplateAddEditResponseDetails> templates = prescriptionServices.getTemplates(page, size, doctorId,
				hospitalId, locationId, updatedTime, discarded);
		Response<TemplateAddEditResponseDetails> response = new Response<TemplateAddEditResponseDetails>();
		response.setDataList(templates);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION)
	public Response<PrescriptionAddEditResponse> addPrescription(PrescriptionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if ((request.getItems() == null || request.getItems().isEmpty())
				&& (request.getDiagnosticTests() == null || request.getDiagnosticTests().isEmpty()) && DPDoctorUtils.anyStringEmpty(request.getAdvice())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PrescriptionAddEditResponse prescriptionAddEditResponse = prescriptionServices.addPrescription(request, true, null, null);

		 //patient track
		if (prescriptionAddEditResponse != null) {
			String visitId = patientTrackService.addRecord(prescriptionAddEditResponse, VisitedFor.PRESCRIPTION,
					prescriptionAddEditResponse.getVisitId());
			prescriptionAddEditResponse.setVisitId(visitId);
		}

		Response<PrescriptionAddEditResponse> response = new Response<PrescriptionAddEditResponse>();
		response.setData(prescriptionAddEditResponse);
		return response;

	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION_HANDHELD)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION_HANDHELD, notes = PathProxy.PrescriptionUrls.ADD_PRESCRIPTION_HANDHELD)
	public Response<PrescriptionAddEditResponseDetails> addPrescriptionHandheld(PrescriptionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if ((request.getItems() == null || request.getItems().isEmpty())
				&& (request.getDiagnosticTests() == null || request.getDiagnosticTests().isEmpty()) && DPDoctorUtils.anyStringEmpty(request.getAdvice())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PrescriptionAddEditResponseDetails prescriptionAddEditResponse = prescriptionServices
				.addPrescriptionHandheld(request);
		if (prescriptionAddEditResponse != null) {
			String visitId = patientTrackService.addRecord(prescriptionAddEditResponse, VisitedFor.PRESCRIPTION,
					prescriptionAddEditResponse.getVisitId());
			prescriptionAddEditResponse.setVisitId(visitId);
		}

		Response<PrescriptionAddEditResponseDetails> response = new Response<PrescriptionAddEditResponseDetails>();
		response.setData(prescriptionAddEditResponse);
		return response;

	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.EDIT_PRESCRIPTION)
	public Response<PrescriptionAddEditResponseDetails> editPrescription(
			@PathVariable(value = "prescriptionId") String prescriptionId, PrescriptionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(prescriptionId, request.getDoctorId(),
				request.getLocationId(), request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else if ((request.getItems() == null || request.getItems().isEmpty())
				&& (request.getDiagnosticTests() == null || request.getDiagnosticTests().isEmpty()) && DPDoctorUtils.anyStringEmpty(request.getAdvice())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(prescriptionId);
		PrescriptionAddEditResponseDetails prescriptionAddEditResponse = prescriptionServices.editPrescription(request);
		if (prescriptionAddEditResponse != null) {
			String visitId = patientTrackService.editRecord(prescriptionAddEditResponse.getId(),
					VisitedFor.PRESCRIPTION);
			prescriptionAddEditResponse.setVisitId(visitId);
		}

		Response<PrescriptionAddEditResponseDetails> response = new Response<PrescriptionAddEditResponseDetails>();
		response.setData(prescriptionAddEditResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION)
	public Response<Prescription> deletePrescription(@PathVariable(value = "prescriptionId") String prescriptionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId, @PathVariable(value = "patientId") String patientId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, hospitalId, locationId)) {
			logger.warn("Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Prescription prescriptionDeleteResponse = prescriptionServices.deletePrescription(prescriptionId, doctorId,
				hospitalId, locationId, patientId, discarded);
		Response<Prescription> response = new Response<Prescription>();
		response.setData(prescriptionDeleteResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.GET_PRESCRIPTION)
	public Response<Prescription> getPrescription(@PathVariable(value = "prescriptionId") String prescriptionId) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Prescription or Patient Id Cannot Be Empty");
		}

		Prescription prescription = prescriptionServices.getPrescriptionById(prescriptionId);

		Response<Prescription> response = new Response<Prescription>();
		response.setData(prescription);
		return response;
	}

	@GetMapping
	@ApiOperation(value = "GET_PRESCRIPTIONS", notes = "GET_PRESCRIPTIONS")
	public Response<Prescription> getPrescription(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@RequestParam("hospitalId") String hospitalId, @RequestParam("patientId") String patientId,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		List<Prescription> prescriptions = null;

		prescriptions = prescriptionServices.getPrescriptions(page, size, doctorId, hospitalId, locationId, patientId,
				updatedTime, otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId), discarded,
				false);

		Response<Prescription> response = new Response<Prescription>();
		response.setDataList(prescriptions);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_PATIENT_ID)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_PATIENT_ID, notes = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_PATIENT_ID)
	public Response<Object> getPrescriptionByPatientId(@PathVariable("patientId") String patientId,
			@RequestParam("page") int page, @RequestParam("size") int size,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(patientId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Patient Id Cannot Be Empty");
		}
		Response<Object> response = prescriptionServices.getPrescriptions(patientId, page, size, updatedTime, discarded);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_COUNT)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_COUNT, notes = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_COUNT)
	public Response<Integer> getPrescriptionCount(@PathVariable(value = "doctorId") String doctorId,
			@PathVariable(value = "patientId") String patientId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId) {

		if (DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId, patientId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Integer prescriptionCount = prescriptionServices.getPrescriptionCount(new ObjectId(doctorId),
				new ObjectId(patientId), new ObjectId(locationId), new ObjectId(hospitalId),
				otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId));
		Response<Integer> response = new Response<Integer>();
		response.setData(prescriptionCount);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_DRUG_DOSAGE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_DRUG_DOSAGE, notes = PathProxy.PrescriptionUrls.ADD_DRUG_DOSAGE)
	public Response<DrugDosageAddEditResponse> addDrugDosage(DrugDosageAddEditRequest request) {
		if (request == null || request.getDosage() == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(),
				request.getHospitalId(), request.getLocationId(), request.getDosage())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.addDrugDosage(request);

		Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
		response.setData(drugDosageAddEditResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DOSAGE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DOSAGE, notes = PathProxy.PrescriptionUrls.EDIT_DRUG_DOSAGE)
	public Response<DrugDosageAddEditResponse> editDrugDosage(@PathVariable(value = "drugDosageId") String drugDosageId,
			DrugDosageAddEditRequest request) {
		if (request == null || request.getDosage() == null || DPDoctorUtils.anyStringEmpty(drugDosageId,
				request.getDoctorId(), request.getHospitalId(), request.getLocationId(), request.getDosage())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(drugDosageId);
		DrugDosageAddEditResponse drugDosageAddEditResponse = prescriptionServices.editDrugDosage(request);

		Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
		response.setData(drugDosageAddEditResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DOSAGE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DOSAGE, notes = PathProxy.PrescriptionUrls.DELETE_DRUG_DOSAGE)
	public Response<DrugDosageAddEditResponse> deleteDrugDosage(@PathVariable(value = "drugDosageId") String drugDosageId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(drugDosageId, doctorId, hospitalId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugDosageAddEditResponse drugDosageDeleteResponse = prescriptionServices.deleteDrugDosage(drugDosageId,
				discarded);

		Response<DrugDosageAddEditResponse> response = new Response<DrugDosageAddEditResponse>();
		response.setData(drugDosageDeleteResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_DRUG_DIRECTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_DRUG_DIRECTION, notes = PathProxy.PrescriptionUrls.ADD_DRUG_DIRECTION)
	public Response<DrugDirectionAddEditResponse> addDrugDirection(DrugDirectionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getDirection())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.addDrugDirection(request);

		Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
		response.setData(drugDirectionAddEditResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DIRECTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DIRECTION, notes = PathProxy.PrescriptionUrls.EDIT_DRUG_DIRECTION)
	public Response<DrugDirectionAddEditResponse> editDrugDirection(
			@PathVariable(value = "drugDirectionId") String drugDirectionId, DrugDirectionAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getDirection())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(drugDirectionId);
		DrugDirectionAddEditResponse drugDirectionAddEditResponse = prescriptionServices.editDrugDirection(request);

		Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
		response.setData(drugDirectionAddEditResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DIRECTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DIRECTION, notes = PathProxy.PrescriptionUrls.DELETE_DRUG_DIRECTION)
	public Response<DrugDirectionAddEditResponse> deleteDrugDirection(
			@PathVariable(value = "drugDirectionId") String drugDirectionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(drugDirectionId, doctorId, hospitalId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugDirectionAddEditResponse drugDirectionDeleteResponse = prescriptionServices
				.deleteDrugDirection(drugDirectionId, discarded);

		Response<DrugDirectionAddEditResponse> response = new Response<DrugDirectionAddEditResponse>();
		response.setData(drugDirectionDeleteResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_DRUG_DURATION_UNIT)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_DRUG_DURATION_UNIT, notes = PathProxy.PrescriptionUrls.ADD_DRUG_DURATION_UNIT)
	public Response<DrugDurationUnitAddEditResponse> addDrugDurationUnit(DrugDurationUnitAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getUnit())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices
				.addDrugDurationUnit(request);

		Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
		response.setData(drugDurationUnitAddEditResponse);
		return response;
	}

	
	@PutMapping(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DURATION_UNIT)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EDIT_DRUG_DURATION_UNIT, notes = PathProxy.PrescriptionUrls.EDIT_DRUG_DURATION_UNIT)
	public Response<DrugDurationUnitAddEditResponse> editDrugDurationUnit(
			@PathVariable(value = "drugDurationUnitId") String drugDurationUnitId,
			DrugDurationUnitAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getUnit())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		request.setId(drugDurationUnitId);
		DrugDurationUnitAddEditResponse drugDurationUnitAddEditResponse = prescriptionServices
				.editDrugDurationUnit(request);

		Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
		response.setData(drugDurationUnitAddEditResponse);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DURATION_UNIT)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_DRUG_DURATION_UNIT, notes = PathProxy.PrescriptionUrls.DELETE_DRUG_DURATION_UNIT)
	public Response<DrugDurationUnitAddEditResponse> deleteDrugDurationUnit(
			@PathVariable(value = "drugDurationUnitId") String drugDurationUnitId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(drugDurationUnitId, doctorId, hospitalId, locationId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DrugDurationUnitAddEditResponse drugDurationUnitDeleteResponse = prescriptionServices
				.deleteDrugDurationUnit(drugDurationUnitId, discarded);

		Response<DrugDurationUnitAddEditResponse> response = new Response<DrugDurationUnitAddEditResponse>();
		response.setData(drugDurationUnitDeleteResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_ITEMS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_ITEMS, notes = PathProxy.PrescriptionUrls.GET_PRESCRIPTION_ITEMS)
	public Response<Object> getPrescriptionItems(@PathVariable("type") String type, @PathVariable("range") String range,
			@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "doctorId") String doctorId,
			@RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			@RequestParam(value = "disease") String disease,
			  @RequestParam(value = "discarded") Boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(type, range)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} else {
			if (type.equalsIgnoreCase(PrescriptionItems.LABTEST.getItem())
					|| type.equalsIgnoreCase(PrescriptionItems.DIAGNOSTICTEST.getItem())) {
				if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					logger.warn("Invalid Input");
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
				}
			} else {
				if (DPDoctorUtils.anyStringEmpty(doctorId)) {
					logger.warn("Invalid Input");
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
				}
			}
		}
		Response<Object> response = prescriptionServices.getPrescriptionItems(type, range, page, size, doctorId, locationId,
				hospitalId, updatedTime, discarded, false, disease, null);

		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION)
	public Response<Boolean> emailPrescription(@PathVariable(value = "prescriptionId") String prescriptionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		prescriptionServices.emailPrescription(prescriptionId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION_WEB)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION_WEB, notes = PathProxy.PrescriptionUrls.EMAIL_PRESCRIPTION_WEB)
	public Response<Boolean> emailPrescriptionForWeb(@PathVariable(value = "prescriptionId") String prescriptionId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, emailAddress)) {
			logger.warn(
					"Invalid Input. Prescription Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, EmailAddress Cannot Be Empty");
		}
		prescriptionServices.emailPrescription(prescriptionId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION)
	public Response<Boolean> smsPrescription(@PathVariable(value = "prescriptionId") String prescriptionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, locationId, hospitalId, mobileNumber)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionServices.smsPrescription(prescriptionId, doctorId, locationId, hospitalId,
				mobileNumber, "PRESCRIPTION"));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION_WEB)
	@ApiOperation(value = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION_WEB, notes = PathProxy.PrescriptionUrls.SMS_PRESCRIPTION_WEB)
	public Response<Boolean> smsPrescriptionForWeb(@PathVariable(value = "prescriptionId") String prescriptionId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, mobileNumber)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionServices.smsPrescriptionforWeb(prescriptionId, doctorId, locationId, hospitalId,
				mobileNumber, "PRESCRIPTION"));
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_EDIT_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_EDIT_DIAGNOSTIC_TEST, notes = PathProxy.PrescriptionUrls.ADD_EDIT_DIAGNOSTIC_TEST)
	public Response<DiagnosticTest> addEditDiagnosticTest(DiagnosticTest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getHospitalId(),
				request.getTestName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DiagnosticTest diagnosticTest = prescriptionServices.addEditDiagnosticTest(request);
		transnationalService.addResource(new ObjectId(diagnosticTest.getId()), Resource.DIAGNOSTICTEST, false);

		ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
		BeanUtil.map(diagnosticTest, esDiagnosticTestDocument);
		esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
		Response<DiagnosticTest> response = new Response<DiagnosticTest>();
		response.setData(diagnosticTest);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_DIAGNOSTIC_TEST)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_DIAGNOSTIC_TEST, notes = PathProxy.PrescriptionUrls.DELETE_DIAGNOSTIC_TEST)
	public Response<DiagnosticTest> deleteDiagnosticTest(@PathVariable(value = "diagnosticTestId") String diagnosticTestId,
			@PathVariable(value = "locationId") String locationId, @PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(diagnosticTestId, hospitalId, locationId)) {
			logger.warn("Diagnostic Test Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Diagnostic Test Id, Hospital Id, Location Id Cannot Be Empty");
		}
		DiagnosticTest testDeleteResponse = prescriptionServices.deleteDiagnosticTest(diagnosticTestId, hospitalId,
				locationId, discarded);
		ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
		BeanUtil.map(testDeleteResponse, esDiagnosticTestDocument);
		esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
		Response<DiagnosticTest> response = new Response<DiagnosticTest>();
		response.setData(testDeleteResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_DIAGNOSTIC_TEST_BY_ID)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_DIAGNOSTIC_TEST_BY_ID, notes = PathProxy.PrescriptionUrls.GET_DIAGNOSTIC_TEST_BY_ID)
	public Response<DiagnosticTest> getDiagnosticTest(@PathVariable("diagnosticTestId") String diagnosticTestId) {
		if (DPDoctorUtils.anyStringEmpty(diagnosticTestId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		DiagnosticTest diagnosticTest = prescriptionServices.getDiagnosticTest(diagnosticTestId);
		Response<DiagnosticTest> response = new Response<DiagnosticTest>();
		response.setData(diagnosticTest);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.CHECK_PRESCRIPTION_EXISTS_FOR_PATIENT)
	@ApiOperation(value = PathProxy.PrescriptionUrls.CHECK_PRESCRIPTION_EXISTS_FOR_PATIENT, notes = PathProxy.PrescriptionUrls.CHECK_PRESCRIPTION_EXISTS_FOR_PATIENT)
	public Response<PrescriptionTestAndRecord> checkPrescriptionExists(@PathVariable("uniqueEmrId") String uniqueEmrId,
			@RequestParam("patientId") String patientId, @RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(uniqueEmrId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		PrescriptionTestAndRecord dataResponse = prescriptionServices.checkPrescriptionExists(uniqueEmrId, patientId, locationId, hospitalId);

		Response<PrescriptionTestAndRecord> response = new Response<PrescriptionTestAndRecord>();
		response.setData(dataResponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.DOWNLOAD_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DOWNLOAD_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.DOWNLOAD_PRESCRIPTION)
	public Response<String> downloadPrescription(@PathVariable("prescriptionId") String prescriptionId, @DefaultValue("false") @RequestParam("showPH") Boolean showPH,
    		@DefaultValue("false") @RequestParam("showPLH") Boolean showPLH, @DefaultValue("false") @RequestParam("showFH") Boolean showFH, 
    		@DefaultValue("false") @RequestParam("showDA") Boolean showDA, @DefaultValue("false") @RequestParam("isLabPrint") Boolean isLabPrint) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(prescriptionServices.getPrescriptionFile(prescriptionId, showPH, showPLH, showFH, showDA, isLabPrint));
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.ADD_DRUG_TO_DOCTOR)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_DRUG_TO_DOCTOR, notes = PathProxy.PrescriptionUrls.ADD_DRUG_TO_DOCTOR)
	public Response<Drug> makeDrugFavourite(@PathVariable("drugId") String drugId, @PathVariable("doctorId") String doctorId,
			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
		if (DPDoctorUtils.anyStringEmpty(drugId, doctorId, locationId, hospitalId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Drug drugAddEditResponse = prescriptionServices.makeDrugFavourite(drugId, doctorId, locationId, hospitalId);
		transnationalService.addResource(new ObjectId(drugAddEditResponse.getId()), Resource.DRUG, false);
		if (drugAddEditResponse != null) {
			ESDrugDocument esDrugDocument = new ESDrugDocument();
			BeanUtil.map(drugAddEditResponse, esDrugDocument);
			if (drugAddEditResponse.getDrugType() != null) {
				esDrugDocument.setDrugTypeId(drugAddEditResponse.getDrugType().getId());
				esDrugDocument.setDrugType(drugAddEditResponse.getDrugType().getType());
			}
			esPrescriptionService.addDrug(esDrugDocument);
		}
		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_ADVICE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_ADVICE, notes = PathProxy.PrescriptionUrls.ADD_ADVICE)
	public Response<Advice> addAdvice(Advice request) {
		if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Advice advice = prescriptionServices.addAdvice(request);
		transnationalService.addResource(new ObjectId(advice.getId()), Resource.ADVICE, false);
		ESAdvicesDocument esAdvices = new ESAdvicesDocument();
		BeanUtil.map(advice, esAdvices);
		esPrescriptionService.addAdvices(esAdvices);
		Response<Advice> response = new Response<Advice>();
		response.setData(advice);
		return response;
	}

	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_ADVICE)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_ADVICE, notes = PathProxy.PrescriptionUrls.DELETE_ADVICE)
	public Response<Advice> deleteAdvice(@PathVariable("adviceId") String adviceId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {

		Response<Advice> response = new Response<Advice>();
		response.setData(prescriptionServices.deleteAdvice(adviceId, doctorId, locationId, hospitalId, discarded));
		return response;
	}

	/*
	 * Don't not use it. To add existing custom drugs in my fav..fr updating
	 * mongodb data
	 */
	// (value = PathProxy.PrescriptionUrls.ADD_CUSTOM_DRUG_TO_FAV)
	// @GetMapping
	// public Response<Boolean> makeCustomDrugFavourite() {
	//
	// Response<Boolean> response = new Response<Boolean>();
	// response.setData(prescriptionServices.makeCustomDrugFavourite());
	// return response;
	// }

	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_FAVOURITE_DRUG)
	@ApiOperation(value = PathProxy.PrescriptionUrls.ADD_FAVOURITE_DRUG, notes = PathProxy.PrescriptionUrls.ADD_FAVOURITE_DRUG)
	public Response<Drug> addFavouriteDrug(DrugAddEditRequest request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
				request.getLocationId(), request.getDrugName())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Drug drugAddEditResponse = prescriptionServices.addFavouriteDrug(request, null, null);

		Response<Drug> response = new Response<Drug>();
		response.setData(drugAddEditResponse);
		return response;
	}

	
	@PostMapping(value = PathProxy.PrescriptionUrls.DRUGS_INTERACTION)
	public Response<DrugInteractionResposne> drugInteraction(List<Drug> request, @RequestParam("patientId") String patientId) { 

		List<DrugInteractionResposne> drugInteractionResposnes = prescriptionServices.drugInteraction(request, patientId);
		Response<DrugInteractionResposne> response = new Response<DrugInteractionResposne>();
		response.setDataList(drugInteractionResposnes);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.ADD_GENERIC_CODES_WITH_REACTION)
	public Response<Boolean> addGenericsWithReaction() {

		Response<Boolean> response = new Response<Boolean>();
		//response.setData(prescriptionServices.addGenericsWithReaction());
		response.setData(prescriptionServices.transferGenericDrugs());
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.ADD_FAVOURITES_TO_DRUGS)
	public Response<Boolean> addFavouritesToDrug() {

		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionServices.addFavouritesToDrug());
		return response;
	}

//	(value = PathProxy.PrescriptionUrls.CHECK_PATIENT_EXISTS_FOR_LAB_WITH_PRESCRIPTIONID)
//	@GetMapping
//	@ApiOperation(value = PathProxy.PrescriptionUrls.CHECK_PATIENT_EXISTS_FOR_LAB_WITH_PRESCRIPTIONID, notes = PathProxy.PrescriptionUrls.CHECK_PATIENT_EXISTS_FOR_LAB_WITH_PRESCRIPTIONID)
//	public Response<String> checkPrescriptionExists(@PathVariable("uniqueEmrId") String uniqueEmrId,
//			@PathVariable("locationId") String locationId, @PathVariable("hospitalId") String hospitalId) {
//		if (DPDoctorUtils.anyStringEmpty(uniqueEmrId, locationId, hospitalId)) {
//			logger.error("Invalid Input");
//			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
//		}
//		String patientId = prescriptionServices.checkPrescriptionExists(uniqueEmrId, locationId, hospitalId);
//
//		Response<String> response = new Response<String>();
//		response.setData(dataResponse);
//		return response;
//	}
	
	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_EYE_PRESCRPTION)
	public Response<EyePrescription> addEyePrescription( EyePrescription request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		EyePrescription eyePrescription = prescriptionServices.addEditEyePrescription(request, true);

		// patient track
		if (eyePrescription != null) {
			String visitId = patientTrackService.addRecord(eyePrescription, VisitedFor.EYE_PRESCRIPTION,
					eyePrescription.getVisitId());
			eyePrescription.setVisitId(visitId);
			prescriptionServices.updateEyePrescriptionVisitId(eyePrescription.getId(), visitId);
		}

		Response<EyePrescription> response = new Response<EyePrescription>();
		response.setData(eyePrescription);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.PrescriptionUrls.EDIT_EYE_PRESCRPTION)
	public Response<EyePrescription> editEyePrescription( EyePrescription request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), request.getPatientId() , request.getId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		EyePrescription eyePrescription = prescriptionServices.editEyePrescription(request);
		// patient track
		/*if (eyePrescription != null) {
			String visitId = patientTrackService.addRecord(eyePrescription, VisitedFor.EYE_PRESCRIPTION,
					eyePrescription.getVisitId());
			eyePrescription.setVisitId(visitId);
		}*/

		Response<EyePrescription> response = new Response<EyePrescription>();
		response.setData(eyePrescription);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_EYE_PRESCRPTION_BY_ID)
	public Response<EyePrescription> getEyePrescription( @PathVariable("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		EyePrescription eyePrescription = prescriptionServices.getEyePrescription(id);
		Response<EyePrescription> response = new Response<EyePrescription>();
		response.setData(eyePrescription);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_EYE_PRESCRPTIONS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_EYE_PRESCRPTIONS, notes = PathProxy.PrescriptionUrls.GET_EYE_PRESCRPTIONS)
	public Response<EyePrescription> getEyePrescriptions(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "patientId") String patientId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded,
			@DefaultValue("false") @RequestParam(value = "isOTPVerified") Boolean isOTPVerified) {
		List<EyePrescription> eyePrescriptions = null;
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Invalid Input.");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input.");
		}
		eyePrescriptions = prescriptionServices.getEyePrescriptions(page, size, doctorId, locationId, hospitalId, patientId, updatedTime, discarded, isOTPVerified);
		Response<EyePrescription> response = new Response<>();
		response.setDataList(eyePrescriptions);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.DOWNLOAD_EYE_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DOWNLOAD_EYE_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.DOWNLOAD_EYE_PRESCRIPTION)
	public Response<String> downloadEyePrescription(@PathVariable("prescriptionId") String prescriptionId) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(prescriptionServices.downloadEyePrescription(prescriptionId));
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.EMAIL_EYE_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EMAIL_EYE_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.EMAIL_EYE_PRESCRIPTION)
	public Response<Boolean> emailEyePrescription(@PathVariable(value = "prescriptionId") String prescriptionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		prescriptionServices.emailEyePrescription(prescriptionId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.EMAIL_EYE_PRESCRIPTION_WEB)
	@ApiOperation(value = PathProxy.PrescriptionUrls.EMAIL_EYE_PRESCRIPTION_WEB, notes = PathProxy.PrescriptionUrls.EMAIL_EYE_PRESCRIPTION_WEB)
	public Response<Boolean> emailEyePrescriptionForWeb(@PathVariable(value = "prescriptionId") String prescriptionId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "emailAddress") String emailAddress) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, locationId, hospitalId, emailAddress)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, EmailAddress Cannot Be Empty");
		}
		prescriptionServices.emailEyePrescriptionForWeb(prescriptionId, doctorId, locationId, hospitalId, emailAddress);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.SMS_EYE_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.SMS_EYE_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.SMS_EYE_PRESCRIPTION)
	public Response<Boolean> smsEyePrescription(@PathVariable(value = "prescriptionId") String prescriptionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			@PathVariable(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, mobileNumber)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionServices.smsEyePrescription(prescriptionId, doctorId, locationId, hospitalId, mobileNumber, "EYE_PRESCRIPTION"));
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.SMS_EYE_PRESCRIPTION_WEB)
	@ApiOperation(value = PathProxy.PrescriptionUrls.SMS_EYE_PRESCRIPTION_WEB, notes = PathProxy.PrescriptionUrls.SMS_EYE_PRESCRIPTION_WEB)
	public Response<Boolean> smsEyePrescriptionForWeb(@PathVariable(value = "prescriptionId") String prescriptionId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@PathVariable(value = "mobileNumber") String mobileNumber) {

		if (DPDoctorUtils.anyStringEmpty(prescriptionId, mobileNumber)) {
			logger.warn(
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Invalid Input. Prescription Id, Doctor Id, Location Id, Hospital Id, Mobile Number Cannot Be Empty");
		}

		Response<Boolean> response = new Response<Boolean>();
		response.setData(prescriptionServices.smsEyePrescriptionForWeb(prescriptionId, doctorId, locationId, hospitalId, mobileNumber, "EYE_PRESCRIPTION"));
		return response;
	}
	
	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_EYE_PRESCRIPTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_EYE_PRESCRIPTION, notes = PathProxy.PrescriptionUrls.DELETE_EYE_PRESCRIPTION)
	public Response<EyePrescription> deleteEyePrescription(@PathVariable(value = "prescriptionId") String prescriptionId,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId, @PathVariable(value = "patientId") String patientId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, hospitalId, locationId)) {
			logger.warn("Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		EyePrescription prescriptionDeleteResponse = prescriptionServices.deleteEyePrescription(prescriptionId, doctorId, hospitalId, locationId, patientId, discarded);
		Response<EyePrescription> response = new Response<EyePrescription>();
		response.setData(prescriptionDeleteResponse);
		return response;
	}
	

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_CUSTOM_DRUGS)
	public Response<Drug> getCustomDrugs() {
		List<Drug> drugs = prescriptionServices.getAllCustomDrug();
		Response<Drug> response = new Response<Drug>();
		response.setDataList(drugs);
		return response;
	}
	
	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_EDIT_INSTRUCTIONS)
	public Response<Instructions> addEditInstruction( Instructions request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Instructions instructions = prescriptionServices.addEditInstructions(request);

		Response<Instructions> response = new Response<Instructions>();
		response.setData(instructions);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_INSTRUCTIONS)
	public Response<Instructions> getInstructions(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId,
			@DefaultValue("0") @RequestParam(value = "updatedTime") String updatedTime,
			  @RequestParam(value = "discarded") Boolean discarded) {

		if ( DPDoctorUtils.anyStringEmpty(doctorId,locationId,hospitalId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		Response<Instructions> response = prescriptionServices.getInstructions(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
		return response;
	}
	
	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_INSTRUCTIONS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_INSTRUCTIONS, notes = PathProxy.PrescriptionUrls.DELETE_INSTRUCTIONS)
	public Response<Instructions> deleteInstructions(@PathVariable(value = "id") String id,
			@PathVariable(value = "doctorId") String doctorId, @PathVariable(value = "locationId") String locationId,
			@PathVariable(value = "hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(id, doctorId, hospitalId, locationId)) {
			logger.warn("Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Instructions instruction = prescriptionServices.deleteInstructions(id, doctorId, locationId, hospitalId, discarded);
		Response<Instructions> response = new Response<Instructions>();
		response.setData(instruction);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPDATE_GENERIC_CODES)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPDATE_GENERIC_CODES, notes = PathProxy.PrescriptionUrls.UPDATE_GENERIC_CODES)
	public Response<Boolean> updateGenericCodes() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.updateGenericCodes();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}

	
	@GetMapping(value = PathProxy.PrescriptionUrls.GET_DRUG_SUBSTITUTES)
	@ApiOperation(value = PathProxy.PrescriptionUrls.GET_DRUG_SUBSTITUTES, notes = PathProxy.PrescriptionUrls.GET_DRUG_SUBSTITUTES)
	public Response<List<Drug>> getDrugSubstitues(@PathVariable("drugId") String drugId) {
		if (drugId == null) {
			logger.error("DrugId Is NULL");
			throw new BusinessException(ServiceError.InvalidInput, "DrugId Is NULL");
		}
		List<Drug> drugs = prescriptionServices.getDrugSubstitutes(drugId);
		Response<List<Drug>> response = new Response<List<Drug>>();
		response.setDataList(drugs);
		return response;
	}
		
	
	@PostMapping(value = PathProxy.PrescriptionUrls.ADD_NUTRITION_REFERRAL)
	public Response<NutritionReferral> addNutritionReferral( NutritionReferralRequest request) {

		if (request == null || DPDoctorUtils.anyStringEmpty(request.getPatientId() , request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		} 
		NutritionReferral nutritionReferral = prescriptionServices.addNutritionReferral(request);
		Response<NutritionReferral> response = new Response<NutritionReferral>();
		response.setData(nutritionReferral);
		return response;
	}
	
	
	@DeleteMapping(value = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION_WEB)
	@ApiOperation(value = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION_WEB, notes = PathProxy.PrescriptionUrls.DELETE_PRESCRIPTION_WEB)
	public Response<Prescription> deletePrescriptionForWeb(@PathVariable(value = "prescriptionId") String prescriptionId,
			@RequestParam(value = "doctorId") String doctorId, @RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "hospitalId") String hospitalId, @RequestParam(value = "patientId") String patientId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(prescriptionId, doctorId, hospitalId, locationId)) {
			logger.warn("Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"Prescription Id, Doctor Id, Hospital Id, Location Id Cannot Be Empty");
		}
		Prescription prescriptionDeleteResponse = prescriptionServices.deletePrescriptionForWeb(prescriptionId, doctorId, hospitalId, locationId, patientId, discarded);
		Response<Prescription> response = new Response<Prescription>();
		response.setData(prescriptionDeleteResponse);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPDATE_DRUG_RANKING_ON_BASIS_OF_RANKING)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPDATE_DRUG_RANKING_ON_BASIS_OF_RANKING, notes = PathProxy.PrescriptionUrls.UPDATE_DRUG_RANKING_ON_BASIS_OF_RANKING)
	public Response<Boolean> updateDrugRankingOnBasisOfRanking() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.updateDrugRankingOnBasisOfRanking();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPLOAD_DRUGS)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPLOAD_DRUGS, notes = PathProxy.PrescriptionUrls.UPLOAD_DRUGS)
	public Response<Boolean> uploadDrugs() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.uploadDrugs();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPDATE_DRUG_INTERACTION)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPDATE_DRUG_INTERACTION, notes = PathProxy.PrescriptionUrls.UPDATE_DRUG_INTERACTION)
	public Response<Boolean> updateDrugInteraction() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.updateDrugInteraction();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.PrescriptionUrls.UPDATE_PRESCRIPTION_DRUG)
	@ApiOperation(value = PathProxy.PrescriptionUrls.UPDATE_PRESCRIPTION_DRUG, notes = PathProxy.PrescriptionUrls.UPDATE_PRESCRIPTION_DRUG)
	public Response<Boolean> updatePrescriptionDrugType() {
		
		Boolean removeDuplicateDrugs = prescriptionServices.updatePrescriptionDrugType();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(removeDuplicateDrugs);
		return response;
	}
}

package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ConsentForm;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.CertificatesServices;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.CERTIFICATE_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.CERTIFICATE_BASE_URL, description = "")
public class CertificatesAPI {

	private static Logger logger = LogManager.getLogger(CertificatesAPI.class.getName());

	@Autowired
	private CertificatesServices certificatesServices;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@PostMapping(value = PathProxy.CertificateTemplatesUrls.ADD_CERTIFICATE_TEMPLATES)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.ADD_CERTIFICATE_TEMPLATES, notes = PathProxy.CertificateTemplatesUrls.ADD_CERTIFICATE_TEMPLATES)
	public Response<Boolean> addCertificateTemplates(@RequestBody CertificateTemplate request) {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(certificatesServices.addCertificateTemplates(request));
		return response;
	}

	@GetMapping(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATE_BY_ID)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATE_BY_ID, notes = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATE_BY_ID)
	public Response<CertificateTemplate> getCertificateTemplateById(@RequestParam("templateId") String templateId) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CertificateTemplate> response = new Response<CertificateTemplate>();
		response.setData(certificatesServices.getCertificateTemplateById(templateId));
		return response;
	}
	
	@GetMapping(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATES)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATES, notes = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATES)
	public Response<CertificateTemplate> getCertificateTemplates(@RequestParam("page") long page,
			@RequestParam("size") int size, @RequestParam("doctorId") String doctorId, @RequestParam("locationId") String locationId,
			@DefaultValue("false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@MatrixParam("speciality") List<String> specialities, @RequestParam("type") String type) {
		Response<CertificateTemplate> response = new Response<CertificateTemplate>();
		response.setDataList(certificatesServices.getCertificateTemplates(page, size, doctorId, locationId, discarded, specialities, type));
		return response;
	}

	@DeleteMapping(value = PathProxy.CertificateTemplatesUrls.DELETE_CERTIFICATE_TEMPLATES)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.DELETE_CERTIFICATE_TEMPLATES, notes = PathProxy.CertificateTemplatesUrls.DELETE_CERTIFICATE_TEMPLATES)
	public Response<Boolean> discardCertificateTemplates(@PathVariable("templateId") String templateId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(certificatesServices.discardCertificateTemplates(templateId, discarded));
		return response;
	}
	
	@PostMapping(value = PathProxy.CertificateTemplatesUrls.ADD_PATIENT_CERTIFICATE)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.ADD_PATIENT_CERTIFICATE, notes = PathProxy.CertificateTemplatesUrls.ADD_PATIENT_CERTIFICATE)
	public Response<ConsentForm> addPatientCertificate(@RequestBody ConsentForm request) {
		if (request == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		if (DPDoctorUtils.anyStringEmpty(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ConsentForm consentForm = certificatesServices.addPatientCertificate(request);
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(consentForm);
		return response;
	}

	@GetMapping(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATE_BY_ID)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATE_BY_ID, notes = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATE_BY_ID)
	public Response<ConsentForm> getPatientCertificateById(@PathVariable("certificateId") String certificateId) {
		if (DPDoctorUtils.anyStringEmpty(certificateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(certificatesServices.getPatientCertificateById(certificateId));
		return response;
	}
	
	@GetMapping(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATES)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATES, notes = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATES)
	public Response<ConsentForm> getPatientCertificates(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("patientId") String patientId, @RequestParam("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded,
			@DefaultValue("0") @RequestParam("updatedTime") String updatedTime, @RequestParam("type") String type) {

		Response<ConsentForm> response = new Response<ConsentForm>();
		List<ConsentForm> consentForms = certificatesServices.getPatientCertificates(page, size, patientId, doctorId, locationId,
				hospitalId, discarded, updatedTime, type);
		response.setDataList(consentForms);
		return response;
	}

	@DeleteMapping(value = PathProxy.CertificateTemplatesUrls.DELETE_PATIENT_CERTIFICATE)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.DELETE_PATIENT_CERTIFICATE, notes = PathProxy.CertificateTemplatesUrls.DELETE_PATIENT_CERTIFICATE)
	public Response<ConsentForm> deletePatientCertificate(@PathVariable("certificateId") String certificateId,
			  @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(certificateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(certificatesServices.deletePatientCertificate(certificateId, discarded));
		return response;
	}

	@GetMapping(value = PathProxy.CertificateTemplatesUrls.DOWNLOAD_PATIENT_CERTIFICATE)
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.DOWNLOAD_PATIENT_CERTIFICATE, notes = PathProxy.CertificateTemplatesUrls.DOWNLOAD_PATIENT_CERTIFICATE)
	public Response<String> downloadPatientCertificate(@PathVariable("certificateId") String certificateId) {
		if (DPDoctorUtils.anyStringEmpty(certificateId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(certificatesServices.downloadPatientCertificate(certificateId));
		return response;
	}

	@PostMapping(value = PathProxy.CertificateTemplatesUrls.SAVE_CERTIFICATE_SIGN_IMAGE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA_VALUE})
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.SAVE_CERTIFICATE_SIGN_IMAGE, notes = PathProxy.CertificateTemplatesUrls.SAVE_CERTIFICATE_SIGN_IMAGE)
	public Response<String> saveCertificateSignImage(@RequestParam("file") MultipartFile file,
			@PathVariable("certificateId") String certificateId) {
		
		String imageURL = certificatesServices.saveCertificateSignImage(file, certificateId);
		Response<String> response = new Response<String>();
		response.setData(imageURL);
		return response;
	}
}

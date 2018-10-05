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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

@Component
@Path(PathProxy.CERTIFICATE_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CERTIFICATE_BASE_URL, description = "")
public class CertificatesAPI {

	private static Logger logger = Logger.getLogger(CertificatesAPI.class.getName());

	@Autowired
	private CertificatesServices certificatesServices;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@Path(value = PathProxy.CertificateTemplatesUrls.ADD_CERTIFICATE_TEMPLATES)
	@POST
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.ADD_CERTIFICATE_TEMPLATES, notes = PathProxy.CertificateTemplatesUrls.ADD_CERTIFICATE_TEMPLATES)
	public Response<Boolean> addCertificateTemplates(CertificateTemplate request) {
		Response<Boolean> response = new Response<Boolean>();
		response.setData(certificatesServices.addCertificateTemplates(request));
		return response;
	}

	@Path(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATE_BY_ID, notes = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATE_BY_ID)
	public Response<CertificateTemplate> getCertificateTemplateById(@QueryParam("templateId") String templateId) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<CertificateTemplate> response = new Response<CertificateTemplate>();
		response.setData(certificatesServices.getCertificateTemplateById(templateId));
		return response;
	}
	
	@Path(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATES)
	@GET
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATES, notes = PathProxy.CertificateTemplatesUrls.GET_CERTIFICATE_TEMPLATES)
	public Response<CertificateTemplate> getCertificateTemplates(@QueryParam("page") long page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@DefaultValue("false") @QueryParam("discarded") Boolean discarded,
			@MatrixParam("speciality") List<String> specialities, @QueryParam("type") String type) {
		Response<CertificateTemplate> response = new Response<CertificateTemplate>();
		response.setDataList(certificatesServices.getCertificateTemplates(page, size, doctorId, locationId, discarded, specialities, type));
		return response;
	}

	@Path(value = PathProxy.CertificateTemplatesUrls.DELETE_CERTIFICATE_TEMPLATES)
	@DELETE
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.DELETE_CERTIFICATE_TEMPLATES, notes = PathProxy.CertificateTemplatesUrls.DELETE_CERTIFICATE_TEMPLATES)
	public Response<Boolean> discardCertificateTemplates(@PathParam("templateId") String templateId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(templateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(certificatesServices.discardCertificateTemplates(templateId, discarded));
		return response;
	}
	
	@Path(value = PathProxy.CertificateTemplatesUrls.ADD_PATIENT_CERTIFICATE)
	@POST
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.ADD_PATIENT_CERTIFICATE, notes = PathProxy.CertificateTemplatesUrls.ADD_PATIENT_CERTIFICATE)
	public Response<ConsentForm> addPatientCertificate(ConsentForm request) {
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

	@Path(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATE_BY_ID)
	@GET
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATE_BY_ID, notes = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATE_BY_ID)
	public Response<ConsentForm> getPatientCertificateById(@PathParam("certificateId") String certificateId) {
		if (DPDoctorUtils.anyStringEmpty(certificateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(certificatesServices.getPatientCertificateById(certificateId));
		return response;
	}
	
	@Path(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATES)
	@GET
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATES, notes = PathProxy.CertificateTemplatesUrls.GET_PATIENT_CERTIFICATES)
	public Response<ConsentForm> getPatientCertificates(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("patientId") String patientId, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue("true") @QueryParam("discarded") boolean discarded,
			@DefaultValue("0") @QueryParam("updatedTime") String updatedTime, @QueryParam("type") String type) {

		Response<ConsentForm> response = new Response<ConsentForm>();
		List<ConsentForm> consentForms = certificatesServices.getPatientCertificates(page, size, patientId, doctorId, locationId,
				hospitalId, discarded, updatedTime, type);
		response.setDataList(consentForms);
		return response;
	}

	@Path(value = PathProxy.CertificateTemplatesUrls.DELETE_PATIENT_CERTIFICATE)
	@DELETE
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.DELETE_PATIENT_CERTIFICATE, notes = PathProxy.CertificateTemplatesUrls.DELETE_PATIENT_CERTIFICATE)
	public Response<ConsentForm> deletePatientCertificate(@PathParam("certificateId") String certificateId,
			@DefaultValue("true") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(certificateId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<ConsentForm> response = new Response<ConsentForm>();
		response.setData(certificatesServices.deletePatientCertificate(certificateId, discarded));
		return response;
	}

	@Path(value = PathProxy.CertificateTemplatesUrls.DOWNLOAD_PATIENT_CERTIFICATE)
	@GET
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.DOWNLOAD_PATIENT_CERTIFICATE, notes = PathProxy.CertificateTemplatesUrls.DOWNLOAD_PATIENT_CERTIFICATE)
	public Response<String> downloadPatientCertificate(@PathParam("certificateId") String certificateId) {
		if (DPDoctorUtils.anyStringEmpty(certificateId)) {
			logger.error("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<String> response = new Response<String>();
		response.setData(certificatesServices.downloadPatientCertificate(certificateId));
		return response;
	}

	@POST
	@Path(value = PathProxy.CertificateTemplatesUrls.SAVE_CERTIFICATE_SIGN_IMAGE)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@ApiOperation(value = PathProxy.CertificateTemplatesUrls.SAVE_CERTIFICATE_SIGN_IMAGE, notes = PathProxy.CertificateTemplatesUrls.SAVE_CERTIFICATE_SIGN_IMAGE)
	public Response<String> saveCertificateSignImage(@FormDataParam("file") FormDataBodyPart file,
			@FormDataParam("certificateId") FormDataBodyPart certificateId) {
		certificateId.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		String certificateIdStr = certificateId.getValueAs(String.class);

		String imageURL = certificatesServices.saveCertificateSignImage(file, certificateIdStr);
		Response<String> response = new Response<String>();
		response.setData(imageURL);
		return response;
	}
}

package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ConsentForm;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Fields;
import com.dpdocter.collections.CertificateTemplateCollection;
import com.dpdocter.collections.ConsentFormCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CertificateTemplateRepository;
import com.dpdocter.repository.ConsentFormRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.ConsentFormCollectionLookupResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.services.CertificatesServices;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class CertificateServicesImpl implements CertificatesServices {

	private static Logger logger = Logger.getLogger(CertificateServicesImpl.class.getName());

	@Autowired
	UserRepository userRepository;

	@Autowired
	private CertificateTemplateRepository certificateTemplateRepository;
	
	@Autowired
	private ConsentFormRepository consentFormRepository;
	
	@Autowired
	private PrintSettingsRepository printSettingsRepository;
	
	@Autowired
	private PatientVisitService patientVisitService;
	
	@Autowired
	private JasperReportService jasperReportService;
	
	@Value(value = "${jasper.print.patient.certificate.fileName}")
	private String patientCertificateFileName;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public Boolean addCertificateTemplates(CertificateTemplate request) {
		Boolean response = false;
		try {
			CertificateTemplateCollection certificateTemplateCollection = new CertificateTemplateCollection();
			BeanUtil.map(request, certificateTemplateCollection);
			
			if(DPDoctorUtils.anyStringEmpty(request.getId())) {
				certificateTemplateCollection.setCreatedTime(new Date());
				if(DPDoctorUtils.anyStringEmpty(request.getDoctorId())) certificateTemplateCollection.setCreatedBy("ADMIN");
				else {
					UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
					certificateTemplateCollection.setCreatedBy(userCollection.getTitle() +" "+userCollection.getFirstName());
				}
			}else {
				CertificateTemplateCollection oldCertificateTemplateCollection = certificateTemplateRepository.findOne(new ObjectId(request.getId()));
				certificateTemplateCollection.setUpdatedTime(new Date());
				certificateTemplateCollection.setCreatedBy(oldCertificateTemplateCollection.getCreatedBy());
				certificateTemplateCollection.setCreatedTime(oldCertificateTemplateCollection.getCreatedTime());
				certificateTemplateCollection.setDiscarded(oldCertificateTemplateCollection.getDiscarded());
			}
			certificateTemplateCollection = certificateTemplateRepository.save(certificateTemplateCollection);
			response = true;			
		} catch (Exception e) {
			logger.error("Error while adding certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while adding certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public CertificateTemplate getCertificateTemplateById(String templateId) {
		CertificateTemplate response = null;
		try {
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(templateId)))), CertificateTemplateCollection.class, CertificateTemplate.class).getUniqueMappedResult();
			if(response == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No Certificate template is found with this Id");
			}
		} catch (Exception e) {
			logger.error("Error while getting certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while getting certificate template" + e.getMessage());
		}
		return response;

	}

	@Override
	public List<CertificateTemplate> getCertificateTemplates(int page, int size, String doctorId, String locationId, Boolean discarded, List<String> specialities) {
		List<CertificateTemplate> response = null;
		try {
			Criteria criteria = new Criteria();
			if(!DPDoctorUtils.allStringsEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			if(!DPDoctorUtils.allStringsEmpty(locationId))criteria.and("locationId").is(new ObjectId(locationId));
			
			if(specialities != null && !specialities.isEmpty()) {
				criteria.and("specialities").in(specialities);
			}
			if(!discarded) criteria.and("discarded").is(discarded);
			
			Aggregation aggregation = null;
			
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "updatedTime"),
						Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "updatedTime"));
			}
			response = mongoTemplate.aggregate(aggregation, CertificateTemplateCollection.class, CertificateTemplate.class).getMappedResults();	
		} catch (Exception e) {
			logger.error("Error while getting certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while getting certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discardCertificateTemplates(String templateId, Boolean discarded) {
		Boolean response = false;
		try {
			CertificateTemplateCollection certificateTemplateCollection = certificateTemplateRepository.findOne(new ObjectId(templateId));
			if(certificateTemplateCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No Certificate template is found with this Id");
			}
			certificateTemplateCollection.setUpdatedTime(new Date());
			certificateTemplateCollection.setDiscarded(discarded);
			certificateTemplateCollection = certificateTemplateRepository.save(certificateTemplateCollection);
			response = true;			
		} catch (Exception e) {
			logger.error("Error while discarding certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while discarding certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public ConsentForm addPatientCertificate(ConsentForm request) {
		ConsentForm response = null;
		try {
			ConsentFormCollection consentFormCollection = new ConsentFormCollection();
			BeanUtil.map(request, consentFormCollection);
			
			if(DPDoctorUtils.anyStringEmpty(request.getId())) {
				consentFormCollection.setCreatedTime(new Date());
				if(DPDoctorUtils.anyStringEmpty(request.getDoctorId())) consentFormCollection.setCreatedBy("ADMIN");
				else {
					UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
					consentFormCollection.setCreatedBy(userCollection.getTitle() +" "+userCollection.getFirstName());
				}
			}else {
				ConsentFormCollection oldConsentFormCollection = consentFormRepository.findOne(new ObjectId(request.getId()));
				consentFormCollection.setUpdatedTime(new Date());
				consentFormCollection.setCreatedBy(oldConsentFormCollection.getCreatedBy());
				consentFormCollection.setCreatedTime(oldConsentFormCollection.getCreatedTime());
				consentFormCollection.setDiscarded(oldConsentFormCollection.getDiscarded());
			}
			consentFormCollection.setType("CERTIFICATE");
			consentFormCollection = consentFormRepository.save(consentFormCollection);
			response = new ConsentForm();
			BeanUtil.map(consentFormCollection, response);
		} catch (Exception e) {
			logger.error("Error while adding consent Form" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while adding consent Form" + e.getMessage());
		}
		return response;
	}

	@Override
	public ConsentForm getPatientCertificateById(String certificateId) {
		ConsentForm response = null;
		try {
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(certificateId))),
					Aggregation.lookup("certificate_template_cl", "templateId", "_id", "certificateTemplate"),
					Aggregation.unwind("certificateTemplate"),
					new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject("id", "$_id")
							.append("doctorId", "$doctorId")
							.append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId")
							.append("patientId", "$patientId")
							.append("dateOfSign", "$dateOfSign")
							.append("signImageURL", "$signImageURL")
							.append("templateId", "$templateId")
							.append("inputElements", "$inputElements")
							.append("templateHtmlText", "$certificateTemplate.htmlText")
							.append("type", "$type")
							.append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy"))),
					new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$id")
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("dateOfSign", new BasicDBObject("$first", "$dateOfSign"))
							.append("signImageURL", new BasicDBObject("$first", "$signImageURL"))
							.append("templateId", new BasicDBObject("$first", "$templateId"))
							.append("inputElements", new BasicDBObject("$first", "$inputElements"))
							.append("templateHtmlText", new BasicDBObject("$first", "$certificateTemplate.htmlText"))
							.append("type", new BasicDBObject("$first", "$type"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))))
					), ConsentFormCollection.class, ConsentForm.class).getUniqueMappedResult();
			
			if(response != null) {
				response.setSignImageURL(getFinalImageURL(response.getSignImageURL()));
			}
		} catch (Exception e) {
			logger.error("Error while getting patient certificate By Id" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while getting patient certificate By Id" + e.getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (!DPDoctorUtils.anyStringEmpty(imageURL)) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public List<ConsentForm> getPatientCertificates(int page, int size, String patientId, String doctorId,
			String locationId, String hospitalId, boolean discarded, String updatedTime) {
		List<ConsentForm> response = null;
		try {
			long createdTimestamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
					
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			
			if (!discarded)criteria.and("discarded").is(discarded);
			
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("certificate_template_cl", "templateId", "_id", "certificateTemplate"),
					Aggregation.unwind("certificateTemplate"),
					new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject("id", "$_id")
							.append("doctorId", "$doctorId")
							.append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId")
							.append("patientId", "$patientId")
							.append("dateOfSign", "$dateOfSign")
							.append("signImageURL", "$signImageURL")
							.append("templateId", "$templateId")
							.append("inputElements", "$inputElements")
							.append("templateHtmlText", "$certificateTemplate.htmlText")
							.append("type", "$type")
							.append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy"))),
					new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$id")
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("dateOfSign", new BasicDBObject("$first", "$dateOfSign"))
							.append("signImageURL", new BasicDBObject("$first", "$signImageURL"))
							.append("templateId", new BasicDBObject("$first", "$templateId"))
							.append("inputElements", new BasicDBObject("$first", "$inputElements"))
							.append("templateHtmlText", new BasicDBObject("$first", "$certificateTemplate.htmlText"))
							.append("type", new BasicDBObject("$first", "$type"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))))
					), ConsentFormCollection.class, ConsentForm.class).getMappedResults();
			
			if(response != null) {
				for(ConsentForm consentForm : response)consentForm.setSignImageURL(getFinalImageURL(consentForm.getSignImageURL()));
			}
		} catch (Exception e) {
			logger.error("Error while getting patient certificates" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error  while getting patient certificates" + e.getMessage());
		}
		return response;
	}

	@Override
	public ConsentForm deletePatientCertificate(String certificateId, Boolean discarded) {
		ConsentForm response = null;
		try {
			ConsentFormCollection consentFormCollection = consentFormRepository.findOne(new ObjectId(certificateId));
			if(consentFormCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No patient certificate is found with this Id");
			}
			consentFormCollection.setUpdatedTime(new Date());
			consentFormCollection.setDiscarded(discarded);
			consentFormCollection = consentFormRepository.save(consentFormCollection);
			response = new ConsentForm();
			BeanUtil.map(consentFormCollection, response);
		} catch (Exception e) {
			logger.error("Error while discarding patient certificate" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while discarding patient certificate" + e.getMessage());
		}
		return response;
	}

	@Override
	public String downloadPatientCertificate(String certificateId) {
		String response = null;
		try {
			ConsentFormCollectionLookupResponse consentFormCollection = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(certificateId))),
							Aggregation.lookup("patient_cl","patientId", "userId", "patientCollection"),
							Aggregation.unwind("patientCollection"),
							new CustomAggregationOperation(new BasicDBObject("$redact", 
									new BasicDBObject("$cond",
											new BasicDBObject("if", new BasicDBObject("$eq", Arrays.asList("$patientCollection.locationId", "$locationId")))
											.append("then", "$$KEEP")
											.append("else", "$$PRUNE")))),
							Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
							Aggregation.unwind("patientUser"),
							Aggregation.lookup("certificate_template_cl", "templateId", "_id", "certificateTemplate"),
							Aggregation.unwind("certificateTemplate")),
					ConsentFormCollection.class, ConsentFormCollectionLookupResponse.class).getUniqueMappedResult();

			if (consentFormCollection != null) {
				PatientCollection patient = consentFormCollection.getPatientCollection();
				UserCollection user = consentFormCollection.getPatientUser();

				JasperReportResponse jasperReportResponse = createJasper(consentFormCollection, patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Prescription Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Prescription PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(ConsentFormCollectionLookupResponse consentFormCollection, PatientCollection patient,
			UserCollection user) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		JasperReportResponse response = null;

		String htmlText = consentFormCollection.getCertificateTemplate().getHtmlText();
		if(consentFormCollection.getInputElements() != null && !consentFormCollection.getInputElements().isEmpty()) {
			for(Fields field : consentFormCollection.getInputElements()) {
				htmlText = htmlText.replace(field.getKey(), field.getValue());
			}
		}
		parameters.put("certificateId", consentFormCollection.getId());
		parameters.put("htmlText", htmlText);
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				new ObjectId(consentFormCollection.getDoctorId()), new ObjectId(consentFormCollection.getLocationId()),
				new ObjectId(consentFormCollection.getHospitalId()), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"",
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				consentFormCollection.getUpdatedTime(), printSettings.getHospitalUId());
		patientVisitService.generatePrintSetup(parameters, printSettings, new ObjectId(consentFormCollection.getDoctorId()));
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "CERTIFICATE-"
				+ new Date().getTime();
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getTopMargin() != null
						? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getBottomMargin() != null
						? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;

		response = jasperReportService.createPDF(ComponentType.CERTIFICATE, parameters, patientCertificateFileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}
}
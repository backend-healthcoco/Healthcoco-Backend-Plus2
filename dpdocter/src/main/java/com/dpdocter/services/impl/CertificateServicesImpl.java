package com.dpdocter.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ConsentForm;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.collections.CertificateTemplateCollection;
import com.dpdocter.collections.ConsentFormCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CertificateTemplateRepository;
import com.dpdocter.repository.ConsentFormRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.ConsentFormCollectionLookupResponse;
import com.dpdocter.services.CertificatesServices;
import com.dpdocter.services.FileManager;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

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

	@Value(value = "${jasper.print.patient.certificate.fileName}")
	private String patientCertificateFileName;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private FileManager fileManager;

	@Value(value = "${jasper.templates.resource}")
	private String JASPER_TEMPLATES_RESOURCE;

	@Value(value = "${jasper.templates.root.path}")
	private String JASPER_TEMPLATES_ROOT_PATH;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Override
	public Boolean addCertificateTemplates(CertificateTemplate request) {
		Boolean response = false;
		try {
			CertificateTemplateCollection certificateTemplateCollection = new CertificateTemplateCollection();
			BeanUtil.map(request, certificateTemplateCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				certificateTemplateCollection.setCreatedTime(new Date());
				if (DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
					certificateTemplateCollection.setCreatedBy("ADMIN");
				else {
					UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
							.orElse(null);
					certificateTemplateCollection
							.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				}
			} else {
				CertificateTemplateCollection oldCertificateTemplateCollection = certificateTemplateRepository
						.findById(new ObjectId(request.getId())).orElse(null);
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
			throw new BusinessException(ServiceError.Unknown,
					"Error  while adding certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public CertificateTemplate getCertificateTemplateById(String templateId) {
		CertificateTemplate response = null;
		try {
			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(templateId)))),
					CertificateTemplateCollection.class, CertificateTemplate.class).getUniqueMappedResult();
			if (response == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No Certificate template is found with this Id");
			}
		} catch (Exception e) {
			logger.error("Error while getting certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting certificate template" + e.getMessage());
		}
		return response;

	}

	@Override
	public List<CertificateTemplate> getCertificateTemplates(long page, int size, String doctorId, String locationId,
			Boolean discarded, List<String> specialities, String type) {
		List<CertificateTemplate> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.allStringsEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.allStringsEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (specialities != null && !specialities.isEmpty()) {
				criteria.and("specialities").in(specialities);
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Direction.DESC, "updatedTime"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Direction.DESC, "updatedTime"));
			}
			response = mongoTemplate
					.aggregate(aggregation, CertificateTemplateCollection.class, CertificateTemplate.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean discardCertificateTemplates(String templateId, Boolean discarded) {
		Boolean response = false;
		try {
			CertificateTemplateCollection certificateTemplateCollection = certificateTemplateRepository
					.findById(new ObjectId(templateId)).orElse(null);
			if (certificateTemplateCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "No Certificate template is found with this Id");
			}
			certificateTemplateCollection.setUpdatedTime(new Date());
			certificateTemplateCollection.setDiscarded(discarded);
			certificateTemplateCollection = certificateTemplateRepository.save(certificateTemplateCollection);
			response = true;
		} catch (Exception e) {
			logger.error("Error while discarding certificate template" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while discarding certificate template" + e.getMessage());
		}
		return response;
	}

	@Override
	public ConsentForm addPatientCertificate(ConsentForm request) {
		ConsentForm response = null;
		try {
			ConsentFormCollection consentFormCollection = new ConsentFormCollection();
			BeanUtil.map(request, consentFormCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				consentFormCollection.setCreatedTime(new Date());
				if (DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
					consentFormCollection.setCreatedBy("ADMIN");
				else {
					UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
							.orElse(null);
					consentFormCollection.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				}
			} else {
				ConsentFormCollection oldConsentFormCollection = consentFormRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				consentFormCollection.setUpdatedTime(new Date());
				consentFormCollection.setCreatedBy(oldConsentFormCollection.getCreatedBy());
				consentFormCollection.setCreatedTime(oldConsentFormCollection.getCreatedTime());
				consentFormCollection.setDiscarded(oldConsentFormCollection.getDiscarded());
			}

			if (consentFormCollection.getInputElements() != null) {
				for (Fields inputElement : consentFormCollection.getInputElements()) {
					if (!DPDoctorUtils.anyStringEmpty(inputElement.getType(), inputElement.getValue())
							&& inputElement.getType().equalsIgnoreCase("IMAGE"))
						inputElement.getValue().replace(imagePath, "");
				}
			}

			consentFormCollection = consentFormRepository.save(consentFormCollection);
			response = new ConsentForm();
			BeanUtil.map(consentFormCollection, response);
			if (response.getInputElements() != null) {
				for (Fields inputElement : response.getInputElements()) {
					if (!DPDoctorUtils.anyStringEmpty(inputElement.getType(), inputElement.getValue())
							&& inputElement.getType().equalsIgnoreCase("IMAGE")
							&& !inputElement.getValue().matches("[\\_]+"))
						inputElement.setValue(getFinalImageURL(inputElement.getValue()));
				}
			}
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
			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(new ObjectId(certificateId)))),
					ConsentFormCollection.class, ConsentForm.class).getUniqueMappedResult();

			if (response != null) {
				response.setSignImageURL(getFinalImageURL(response.getSignImageURL()));
				if (response.getInputElements() != null) {
					for (Fields inputElement : response.getInputElements()) {
						if (!DPDoctorUtils.anyStringEmpty(inputElement.getType(), inputElement.getValue())
								&& inputElement.getType().equalsIgnoreCase("IMAGE")
								&& !inputElement.getValue().matches("[\\_]+"))
							inputElement.setValue(getFinalImageURL(inputElement.getValue()));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting patient certificate By Id" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting patient certificate By Id" + e.getMessage());
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
	public List<ConsentForm> getPatientCertificates(long page, int size, String patientId, String doctorId,
			String locationId, String hospitalId, boolean discarded, String updatedTime, String type) {
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
			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			if (!discarded)
				criteria.and("discarded").is(discarded);

			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("doctorId", "$doctorId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("patientId", "$patientId")
							.append("dateOfSign", "$dateOfSign").append("signImageURL", "$signImageURL")
							.append("templateId", "$templateId").append("inputElements", "$inputElements")
							.append("templateHtmlText", "$templateHtmlText").append("type", "$type")
							.append("localPatientName", "$patient.localPatientName")
							.append("mobileNumber", "$user.mobileNumber").append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("dateOfSign", new BasicDBObject("$first", "$dateOfSign"))
							.append("signImageURL", new BasicDBObject("$first", "$signImageURL"))
							.append("templateId", new BasicDBObject("$first", "$templateId"))
							.append("inputElements", new BasicDBObject("$first", "$inputElements"))
							.append("templateHtmlText", new BasicDBObject("$first", "$templateHtmlText"))
							.append("type", new BasicDBObject("$first", "$type"))
							.append("localPatientName", new BasicDBObject("$first", "$localPatientName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			if (size > 0) {
				response = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
										Aggregation.unwind("patient"),
										new CustomAggregationOperation(new Document("$redact",
												new BasicDBObject("$cond", new BasicDBObject("if",
														new BasicDBObject("$eq",
																Arrays.asList("$patient.locationId", "$locationId")))
														.append("then", "$$KEEP").append("else", "$$PRUNE")))),
										Aggregation.lookup("user_cl", "patientId", "_id", "user"),
										Aggregation.unwind("user"), project, group, Aggregation.skip((page) * size),
										Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "createdTime")),
								ConsentFormCollection.class, ConsentForm.class)
						.getMappedResults();
			} else {
				response = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
										Aggregation.unwind("patient"),
										new CustomAggregationOperation(new Document("$redact",
												new BasicDBObject("$cond", new BasicDBObject("if",
														new BasicDBObject("$eq",
																Arrays.asList("$patient.locationId", "$locationId")))
														.append("then", "$$KEEP").append("else", "$$PRUNE")))),
										Aggregation.lookup("user_cl", "patientId", "_id", "user"),
										Aggregation.unwind("user"), project, group,
										Aggregation.sort(Sort.Direction.DESC, "createdTime")),
								ConsentFormCollection.class, ConsentForm.class)
						.getMappedResults();
			}

			if (response != null) {
				for (ConsentForm consentForm : response) {
					consentForm.setSignImageURL(getFinalImageURL(consentForm.getSignImageURL()));
					if (consentForm.getInputElements() != null) {
						for (Fields inputElement : consentForm.getInputElements()) {
							if (!DPDoctorUtils.anyStringEmpty(inputElement.getType(), inputElement.getValue())
									&& inputElement.getType().equalsIgnoreCase("IMAGE")
									&& !inputElement.getValue().matches("[\\_]+"))
								inputElement.setValue(getFinalImageURL(inputElement.getValue()));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting patient certificates" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error  while getting patient certificates" + e.getMessage());
		}
		return response;
	}

	@Override
	public ConsentForm deletePatientCertificate(String certificateId, Boolean discarded) {
		ConsentForm response = null;
		try {
			ConsentFormCollection consentFormCollection = consentFormRepository.findById(new ObjectId(certificateId))
					.orElse(null);
			if (consentFormCollection == null) {
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
			throw new BusinessException(ServiceError.Unknown,
					"Error while discarding patient certificate" + e.getMessage());
		}
		return response;
	}

	@Override
	public String downloadPatientCertificate(String certificateId) {
		String response = null;
		try {
			ConsentFormCollectionLookupResponse consentFormCollection = mongoTemplate
					.aggregate(
							Aggregation
									.newAggregation(
											Aggregation.match(new Criteria("_id").is(new ObjectId(certificateId))),
											Aggregation
													.lookup("patient_cl", "patientId", "userId", "patientCollection"),
											Aggregation.unwind("patientCollection"),
											new CustomAggregationOperation(new Document("$redact",
													new BasicDBObject("$cond",
															new BasicDBObject("if", new BasicDBObject("$eq",
																	Arrays.asList("$patientCollection.locationId",
																			"$locationId")))
																	.append("then", "$$KEEP")
																	.append("else", "$$PRUNE")))),
											Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
											Aggregation.unwind("patientUser")),
							ConsentFormCollection.class, ConsentFormCollectionLookupResponse.class)
					.getUniqueMappedResult();

			if (consentFormCollection != null) {
				PatientCollection patient = consentFormCollection.getPatientCollection();
				PrintSettingsCollection printSettings = null;
				printSettings = printSettingsRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
								new ObjectId(consentFormCollection.getDoctorId()),
								new ObjectId(consentFormCollection.getLocationId()),
								new ObjectId(consentFormCollection.getHospitalId()), ComponentType.ALL.getType(),
								PrintSettingType.EMR.getType());
				if (printSettings == null) {
					List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
							.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
									new ObjectId(consentFormCollection.getDoctorId()),
									new ObjectId(consentFormCollection.getLocationId()),
									new ObjectId(consentFormCollection.getHospitalId()), ComponentType.ALL.getType(),
									PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
					if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
						printSettings = printSettingsCollections.get(0);
				}
				if (printSettings == null) {
					printSettings = new PrintSettingsCollection();
					DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
					BeanUtil.map(defaultPrintSettings, printSettings);
				}

				String header = createCertificateHeader(printSettings);
				String htmlText = consentFormCollection.getTemplateHtmlText();
				if (consentFormCollection.getInputElements() != null
						&& !consentFormCollection.getInputElements().isEmpty()) {
					for (Fields field : consentFormCollection.getInputElements()) {
						if (!DPDoctorUtils.anyStringEmpty(field.getType(), field.getValue())
								&& field.getType().equalsIgnoreCase("IMAGE") && !field.getValue().matches("[\\_]+"))
							field.setValue("<img style='padding-top: 12px;height:50px;' src='"
									+ getFinalImageURL(field.getValue()) + "'/>");

						if (!DPDoctorUtils.anyStringEmpty(field.getValue())) {
							htmlText = htmlText.replace(field.getKey(), field.getValue());
						} else {
							htmlText = htmlText.replace(field.getKey(), "__________");
						}
					}
				}

				if (!htmlText.startsWith("<html>"))
					htmlText = "<html>" + htmlText + "</html>";
				if (!htmlText.endsWith("</html>"))
					htmlText = htmlText + "</html>";
				htmlText = "<!DOCTYPE html PUBLIC \'-//W3C//DTD XHTML 1.0 Strict//EN\' \'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\'><html>"
						+ header + htmlText + "</html>";

				String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "CERTIFICATE-"
						+ new Date().getTime();
				pdfName = pdfName.replaceAll("\\s+", "");

				File file = new File(JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
				FileOutputStream os = new FileOutputStream(file);
				ITextRenderer itxtrenderer = new ITextRenderer();
				itxtrenderer.setDocumentFromString(htmlText);
				itxtrenderer.layout();
				itxtrenderer.createPDF(os, true);

				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentEncoding("pdf");
				metadata.setContentType("application/pdf");
				metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
						JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf", file);
				putObjectRequest.setMetadata(metadata);

				BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
				AmazonS3 s3client = new AmazonS3Client(credentials);
				s3client.putObject(putObjectRequest);

				response = getFinalImageURL(JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf");
				if (file != null && file.exists())
					file.delete();

			} else {
				logger.warn("Patient Certificate Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Certificate Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Certificate PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Certificate PDF");
		}
		return response;
	}

	private String createCertificateHeader(PrintSettingsCollection printSettings) {
		String header = "";

		if (printSettings != null) {
			String headerLeftText = "", headerRightText = "", logoURL = "";

			if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getCustomHeader()) {
				if (printSettings.getHeaderSetup().getHeaderHtml() != null) {
					header = printSettings.getHeaderSetup().getHeaderHtml();
				} else {
					if (printSettings.getHeaderSetup().getTopLeftText() != null)
						for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {
							boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
							boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
							if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
								String text = str.getText();
								if (isItalic)
									text = "<i>" + text + "</i>";
								if (isBold)
									text = "<b>" + text + "</b>";

								if (headerLeftText.isEmpty())
									headerLeftText = "<span style='font-size:" + str.getFontSize() + "'>" + text
											+ "</span>";
								else
									headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:"
											+ str.getFontSize() + "'>" + text + "</span>";
							}
						}
					if (printSettings.getHeaderSetup().getTopRightText() != null)
						for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {

							boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
							boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());

							if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
								String text = str.getText();
								if (isItalic)
									text = "<i>" + text + "</i>";
								if (isBold)
									text = "<b>" + text + "</b>";

								if (headerRightText.isEmpty())
									headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text
											+ "</span>";
								else
									headerRightText = headerRightText + "<br/>" + "<span style='font-size:"
											+ str.getFontSize() + "'>" + text + "</span>";
							}
						}
				}

				if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getCustomHeader()
						&& printSettings.getHeaderSetup().getCustomLogo() && printSettings.getClinicLogoUrl() != null) {
					logoURL = getFinalImageURL(printSettings.getClinicLogoUrl());
				}

				header = "<div style = 'width:100%;'> <div style='width:38%; display:inline-block;'>" + headerLeftText
						+ "</div>"
						+ "<div style='width:24%;display:inline-block;vertical-align: top;'><img style='width:50px;height:40px;' src='"
						+ logoURL + "'/></div>" + "<div style='width:38%; display:inline-block;'>" + headerRightText
						+ "</div></div>";
			}
		}
		return header;
	}

	@Override
	public String saveCertificateSignImage(FormDataBodyPart file, String certificateIdStr) {
		String recordPath = null;
		try {

			Date createdTime = new Date();
			if (file != null) {
				String path = "certificateSigns" + File.separator + certificateIdStr;
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");

				recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
				fileManager.saveRecord(file, recordPath, 0.0, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return recordPath;
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}
}
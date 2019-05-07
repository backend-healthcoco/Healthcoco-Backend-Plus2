package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.collections.AppLinkDetailsCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.ContactUsCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.ProcedureNoteCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;
import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESMedicalCouncilDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;
import com.dpdocter.elasticsearch.document.ESServicesDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESComplaintsRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosesRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESDrugRepository;
import com.dpdocter.elasticsearch.repository.ESEducationInstituteRepository;
import com.dpdocter.elasticsearch.repository.ESEducationQualificationRepository;
import com.dpdocter.elasticsearch.repository.ESInvestigationsRepository;
import com.dpdocter.elasticsearch.repository.ESLandmarkLocalityRepository;
import com.dpdocter.elasticsearch.repository.ESMedicalCouncilRepository;
import com.dpdocter.elasticsearch.repository.ESObservationsRepository;
import com.dpdocter.elasticsearch.repository.ESProcedureNoteRepository;
import com.dpdocter.elasticsearch.repository.ESProfessionalMembershipRepository;
import com.dpdocter.elasticsearch.repository.ESServicesRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.enums.AppType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppLinkDetailsRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ContactUsRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.EducationInstituteRepository;
import com.dpdocter.repository.EducationQualificationRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MedicalCouncilRepository;
import com.dpdocter.repository.ProcedureNoteRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.ResumeRepository;
import com.dpdocter.repository.ServicesRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.response.ClinicalItemsResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.CSVUtils;
import common.util.web.DPDoctorUtils;

@Service
public class AdminServicesImpl implements AdminServices {

	private static Logger logger = Logger.getLogger(AdminServicesImpl.class.getName());

	@Autowired
	UserRepository userRepository;

	@Autowired
	HospitalRepository hospitalRepository;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	ResumeRepository resumeRepository;

	@Autowired
	ContactUsRepository contactUsRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private ESCityService esCityService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private EducationInstituteRepository educationInstituteRepository;

	@Autowired
	private EducationQualificationRepository educationQualificationRepository;

	@Autowired
	private ESEducationInstituteRepository esEducationInstituteRepository;

	@Autowired
	private ESEducationQualificationRepository esEducationQualificationRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private ESDiagnosticTestRepository esDiagnosticTestRepository;

	@Autowired
	private ESDrugRepository esDrugRepository;

	@Autowired
	private AppLinkDetailsRepository appLinkDetailsRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private ProfessionalMembershipRepository professionalMembershipRepository;

	@Autowired
	private ESProfessionalMembershipRepository esProfessionalMembershipRepository;

	@Autowired
	private MedicalCouncilRepository medicalCouncilRepository;

	@Autowired
	private ESMedicalCouncilRepository esMedicalCouncilRepository;

	@Autowired
	private SMSServices sMSServices;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${app.link.message}")
	private String appLinkMessage;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Value(value = "${doctor.app.bit.link}")
	private String doctorAppBitLink;

	@Value(value = "${ipad.app.bit.link}")
	private String ipadAppBitLink;

	@Value(value = "${mail.get.app.link.subject}")
	private String getAppLinkSubject;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	ESComplaintsRepository esComplaintsRepository;
	
	@Autowired
	ESObservationsRepository esObservationsRepository;
	
	@Autowired
	ESInvestigationsRepository esInvestigationsRepository;
	
	@Autowired
	ESDiagnosesRepository esDiagnosesRepository;
	
	@Autowired
	ProcedureNoteRepository procedureNoteRepository;
	
	@Autowired
	InvestigationRepository investigationRepository;
	
	@Autowired
	ESProcedureNoteRepository esProcedureNoteRepository;
	
	@Autowired
	TransactionalManagementService transactionalManagementService;
	
	@Autowired
	TransnationalRepositiory transnationalRepositiory;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	LandmarkLocalityRepository landmarkLocalityRepository;
	
	@Autowired
	ESLandmarkLocalityRepository esLandmarkLocalityRepository;
	
	@Autowired
	ServicesRepository servicesRepository;
	
	@Autowired
	SpecialityRepository specialityRepository;
	
	@Autowired
	private ESMasterService esMasterService;
	
	@Autowired
	private ESDoctorRepository esDoctorRepository;
	
	@Autowired
	private ESSpecialityRepository esSpecialityRepository;

	@Autowired
	private ESServicesRepository esServicesRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private TransactionalManagementService transnationalService;
	
	@Override
	@Transactional
	public Resume addResumes(Resume request) {
		Resume response = null;
		try {
			ResumeCollection resumeCollection = new ResumeCollection();
			BeanUtil.map(request, resumeCollection);
			resumeCollection.setCreatedTime(new Date());
			if (request.getFile() != null) {
				request.getFile().setFileName(request.getFile().getFileName() + (new Date()).getTime());
				String path = "resumes" + File.separator + request.getType();
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getFile(), path,
						false);
				resumeCollection.setPath(imageURLResponse.getImageUrl());
			}
			resumeCollection = resumeRepository.save(resumeCollection);
			if (resumeCollection != null) {
				response = new Resume();
				BeanUtil.map(resumeCollection, response);
			}
			String body = mailBodyGenerator.generateEmailBody(resumeCollection.getName(), resumeCollection.getType(),
					"applyForPostTemplate.vm");
			mailService.sendEmail(resumeCollection.getEmailAddress(), "Your application has been received", body, null);
		} catch (Exception e) {
			logger.error("Error while adding resume " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding resume " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public void importCity() {
		String csvFile = "/home/ubuntu/cities.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				CityCollection cityCollection = new CityCollection();
				cityCollection.setCity(obj[0]);
				cityCollection.setState(obj[1]);
				cityCollection.setCountry("India");
				List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
						cityCollection.getCity() + " " + cityCollection.getState() + " " + cityCollection.getCountry());

				if (geocodedLocations != null && !geocodedLocations.isEmpty())
					BeanUtil.map(geocodedLocations.get(0), cityCollection);

				cityCollection = cityRepository.save(cityCollection);
				ESCityDocument esCityDocument = new ESCityDocument();
				BeanUtil.map(cityCollection, esCityDocument);
				esCityDocument.setGeoPoint(new GeoPoint(cityCollection.getLatitude(), cityCollection.getLongitude()));
				esCityService.addCities(esCityDocument);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importDrug() {
		BufferedReader br = null;

		try {
			// br = new BufferedReader(new FileReader(csvFile));
			// while ((line = br.readLine()) != null) {
			// String[] obj = line.split(cvsSplitBy);
			// String drugType = obj[2];
			// DrugTypeCollection drugTypeCollection =
			// drugTypeRepository.findByType(drugType);
			//
			// DrugType type = null;
			// if (drugTypeCollection != null) {
			// type = new DrugType();
			// drugTypeCollection.setType(drugType);
			// BeanUtil.map(drugTypeCollection, type);
			//
			// }
			//
			// DrugCollection drugCollection = new DrugCollection();
			// drugCollection.setCreatedBy("ADMIN");
			// drugCollection.setCreatedTime(new Date());
			// drugCollection.setUpdatedTime(new Date());
			// drugCollection.setDiscarded(false);
			// drugCollection.setDrugName(obj[1]);
			// drugCollection.setDrugType(type);
			// drugCollection.setDoctorId(null);
			// drugCollection.setHospitalId(null);
			// drugCollection.setLocationId(null);
			// drugCollection.setDrugCode(obj[0]);
			// drugCollection.setPackSize(obj[3]);
			// drugCollection.setCompanyName(obj[4]);
			//
			// if (obj.length > 5) {
			// String[] genericCodesArray = obj[5].split("\\+");
			//
			// if (genericCodesArray.length > 0) {
			// List<String> genericCodes = new ArrayList<String>();
			// for (String code : genericCodesArray)
			// genericCodes.add(code);
			// drugCollection.setGenericCodes(genericCodes);
			// }
			// }
			//
			// drugCollection = drugRepository.save(drugCollection);

			List<DrugCollection> drugCollections = drugRepository.findAll();
			for (DrugCollection drugCollection : drugCollections) {
				ESDrugDocument esDrugDocument = new ESDrugDocument();
				BeanUtil.map(drugCollection, esDrugDocument);
				if (drugCollection.getDrugType() != null) {
					esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				esDrugRepository.save(esDrugDocument);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importDiagnosticTest() {
		String csvFile = "/home/ubuntu/DiagnosticTests.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				DiagnosticTestCollection diagnosticTestCollection = new DiagnosticTestCollection();
				diagnosticTestCollection.setTestName(obj[0]);
				diagnosticTestRepository.save(diagnosticTestCollection);
				ESDiagnosticTestDocument document = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, document);
				esDiagnosticTestRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importEducationInstitute() {
		String csvFile = "/home/ubuntu/EducationInstitute.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				EducationInstituteCollection educationInstituteCollection = new EducationInstituteCollection();
				educationInstituteCollection.setName(obj[0]);
				educationInstituteCollection.setCreatedBy("ADMIN");
				educationInstituteCollection.setCreatedTime(new Date());
				educationInstituteCollection.setUpdatedTime(new Date());

				educationInstituteRepository.save(educationInstituteCollection);
				ESEducationInstituteDocument document = new ESEducationInstituteDocument();
				BeanUtil.map(educationInstituteCollection, document);
				esEducationInstituteRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public void importEducationQualification() {
		String csvFile = "/home/ubuntu/EducationQualification.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				EducationQualificationCollection educationQualificationCollection = new EducationQualificationCollection();
				educationQualificationCollection.setName(obj[0]);
				educationQualificationCollection.setCreatedBy("ADMIN");
				educationQualificationCollection.setCreatedTime(new Date());
				educationQualificationCollection.setUpdatedTime(new Date());

				educationQualificationRepository.save(educationQualificationCollection);
				ESEducationQualificationDocument document = new ESEducationQualificationDocument();
				BeanUtil.map(educationQualificationCollection, document);
				esEducationQualificationRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional
	public ContactUs addContactUs(ContactUs request) {
		ContactUs response = null;
		try {
			ContactUsCollection contactUsCollection = new ContactUsCollection();
			BeanUtil.map(request, contactUsCollection);
			contactUsCollection.setCreatedTime(new Date());
			contactUsCollection = contactUsRepository.save(contactUsCollection);
			if (contactUsCollection != null) {
				response = new ContactUs();
				BeanUtil.map(contactUsCollection, response);
			}
		} catch (Exception e) {
			logger.error("Error while adding contact us " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding contact us " + e.getMessage());
		}
		return response;
	}

	public void importProfessionalMembership() {
		String csvFile = "/home/ubuntu/Memberships.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				ProfessionalMembershipCollection professionalMembershipCollection = new ProfessionalMembershipCollection();
				professionalMembershipCollection.setMembership(obj[0]);
				professionalMembershipCollection.setCreatedBy("ADMIN");
				professionalMembershipCollection.setCreatedTime(new Date());
				professionalMembershipCollection.setUpdatedTime(new Date());

				professionalMembershipRepository.save(professionalMembershipCollection);
				ESProfessionalMembershipDocument document = new ESProfessionalMembershipDocument();
				BeanUtil.map(professionalMembershipCollection, document);
				esProfessionalMembershipRepository.save(document);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void importMedicalCouncil() {
		String csvFile = "/home/ubuntu/medicalCouncil.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\?";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				MedicalCouncilCollection medicalCouncilCollection = new MedicalCouncilCollection();
				medicalCouncilCollection.setMedicalCouncil(obj[0]);
				medicalCouncilCollection.setCreatedBy("ADMIN");
				medicalCouncilCollection.setCreatedTime(new Date());
				medicalCouncilCollection.setUpdatedTime(new Date());

				medicalCouncilRepository.save(medicalCouncilCollection);
				ESMedicalCouncilDocument esMedicalCouncilDocument = new ESMedicalCouncilDocument();
				BeanUtil.map(medicalCouncilCollection, esMedicalCouncilDocument);
				esMedicalCouncilRepository.save(esMedicalCouncilDocument);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Boolean sendLink(SendAppLink request) {
		Boolean response = false;
		try {
			String appType = "", appBitLink = "", appDeviceType = "";
			if (request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO.getType())) {
				appType = "Healthcoco";
				appBitLink = patientAppBitLink;
				appDeviceType = "phone";
			} else if (request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO_PLUS.getType())) {
				appType = "Healthcoco+";
				appBitLink = doctorAppBitLink;
				appDeviceType = "phone";
			} else if (request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO_PAD.getType())) {
				appType = "Healthcoco Pad";
				appBitLink = ipadAppBitLink;
				appDeviceType = "ipad";
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
				AppLinkDetailsCollection appLinkDetailsCollection = appLinkDetailsRepository
						.findByMobileNumber(request.getMobileNumber());
				if (appLinkDetailsCollection == null) {
					appLinkDetailsCollection = new AppLinkDetailsCollection();
					appLinkDetailsCollection.setMobileNumber(request.getMobileNumber());
					appLinkDetailsCollection.setCreatedTime(new Date());
				}
				if (appLinkDetailsCollection.getCount() < 3) {
					SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null,
							appLinkMessage.replace("{appType}", appType).replace("{appLink}", appBitLink),
							request.getMobileNumber(), "Get App Link");
					sMSServices.sendSMS(smsTrackDetail, false);
					response = true;
					appLinkDetailsCollection.setCount(appLinkDetailsCollection.getCount() + 1);
					appLinkDetailsRepository.save(appLinkDetailsCollection);
				}
			} else if (!DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
				String body = mailBodyGenerator.generateAppLinkEmailBody(appType, appBitLink, appDeviceType,
						"appLinkTemplate.vm");
				mailService.sendEmail(request.getEmailAddress(), getAppLinkSubject.replace("{appType}", appType), body,
						null);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public Boolean discardDuplicateClinicalItems(String doctorId) {
		Boolean response = false;
		try {
			discardDuplicateClinicalItemsInDb(doctorId, Resource.COMPLAINT.getType(), "complaint", ComplaintCollection.class);
			discardDuplicateClinicalItemsInDb(doctorId, Resource.OBSERVATION.getType(), "observation", ObservationCollection.class);
			discardDuplicateClinicalItemsInDb(doctorId, Resource.INVESTIGATION.getType(), "investigation", InvestigationCollection.class);
			discardDuplicateClinicalItemsInDb(doctorId, Resource.DIAGNOSIS.getType(), "diagnosis", DiagnosisCollection.class);
			response = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private void discardDuplicateClinicalItemsInDb(String doctorId, String resource, String fieldName, Class className) {
		
		List<ClinicalItemsResponse> items = mongoTemplate.aggregate(Aggregation.newAggregation(
				Aggregation.match(new Criteria("doctorId").is(new ObjectId(doctorId)).and("discarded").is(false)),
				Aggregation.sort(new Sort(Direction.ASC, "createdTime")),
				new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject("_id", "$_id")
						.append("resourceIds", "$_id").append("resourceName", "$"+fieldName).append("resourceIdsForEs", "$_id"))),
				new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", "$resourceName")
								.append("keepResourceId", new BasicDBObject("$first", "$resourceIds"))
								.append("resourceIds", new BasicDBObject("$addToSet", "$resourceIds"))
								.append("resourceIdsForEs", new BasicDBObject("$addToSet", "$resourceIdsForEs"))
								.append("resourceName", new BasicDBObject("$first","$resourceName"))
								.append("count", new BasicDBObject("$sum", 1)))),
				new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",
						new BasicDBObject("if", new BasicDBObject("$gt", Arrays.asList("$count", 1)))
						.append("then", "$$KEEP").append("else", "$$PRUNE"))))), 
				className, ClinicalItemsResponse.class).getMappedResults();
		
		if(items != null) {
			for(ClinicalItemsResponse itemsResponse : items) {
				Date updatedTime = new Date();
				itemsResponse.getResourceIds().remove(itemsResponse.getKeepResourceId());
				itemsResponse.getResourceIdsForEs().remove(itemsResponse.getKeepResourceId().toString());
				
				mongoTemplate.updateMulti(new Query(new Criteria("id").in(itemsResponse.getResourceIds())), Update.update("discarded", true).currentDate("updatedTime"), className);
				
				if(resource.equalsIgnoreCase(Resource.COMPLAINT.getType())) {
					List<ESComplaintsDocument> esItems = elasticsearchTemplate.queryForList(
							new CriteriaQuery(new org.springframework.data.elasticsearch.core.query.Criteria("id").in(itemsResponse.getResourceIdsForEs())), ESComplaintsDocument.class);
					if(esItems != null) {
						for(ESComplaintsDocument esComplaintsDocument : esItems) {
							esComplaintsDocument.setDiscarded(true);esComplaintsDocument.setUpdatedTime(updatedTime);
							esComplaintsRepository.save(esComplaintsDocument);
						}
					}
				}else if(resource.equalsIgnoreCase(Resource.OBSERVATION.getType())) {
					List<ESObservationsDocument> esItems = elasticsearchTemplate.queryForList(
							new CriteriaQuery(new org.springframework.data.elasticsearch.core.query.Criteria("id").in(itemsResponse.getResourceIdsForEs())), ESObservationsDocument.class);
					if(esItems != null) {
						for(ESObservationsDocument esDocument : esItems) {
							esDocument.setDiscarded(true);esDocument.setUpdatedTime(updatedTime);
							esObservationsRepository.save(esDocument);
						}
					}
				}else if(resource.equalsIgnoreCase(Resource.INVESTIGATION.getType())) {
					List<ESInvestigationsDocument> esItems = elasticsearchTemplate.queryForList(
							new CriteriaQuery(new org.springframework.data.elasticsearch.core.query.Criteria("id").in(itemsResponse.getResourceIdsForEs())), ESInvestigationsDocument.class);
					if(esItems != null) {
						for(ESInvestigationsDocument esDocument : esItems) {
							esDocument.setDiscarded(true);esDocument.setUpdatedTime(updatedTime);
							esInvestigationsRepository.save(esDocument);
						}
					}
				}else if(resource.equalsIgnoreCase(Resource.DIAGNOSIS.getType())) {
					List<ESDiagnosesDocument> esItems = elasticsearchTemplate.queryForList(
							new CriteriaQuery(new org.springframework.data.elasticsearch.core.query.Criteria("id").in(itemsResponse.getResourceIdsForEs())), ESDiagnosesDocument.class);
					if(esItems != null) {
						for(ESDiagnosesDocument esDocument : esItems) {
							esDocument.setDiscarded(true);esDocument.setUpdatedTime(updatedTime);
							esDiagnosesRepository.save(esDocument);
						}
					}
				}
				
			}
		}
	}

	@Override
	public Boolean copyClinicalItems(String doctorId, String locationId, List<String> drIds) {
		Boolean response = false;
		try {
			List<InvestigationCollection> investigationCollections = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(
					new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId)))), InvestigationCollection.class, InvestigationCollection.class).getMappedResults();
			
			List<ObjectId> drObjectIds = new ArrayList<>();
			for(String id : drIds)drObjectIds.add(new ObjectId(id));		

			if(investigationCollections != null) {
				for(InvestigationCollection investigationCollection : investigationCollections) {
					for(ObjectId id : drObjectIds) {
						long count = mongoTemplate.count(new Query(new Criteria("doctorId").is(id).and("locationId").is(new ObjectId(locationId))
								.and("investigation").is(investigationCollection.getInvestigation())), InvestigationCollection.class);
						if(count == 0) {
							InvestigationCollection inCollection = new InvestigationCollection();
							BeanUtil.map(investigationCollection, inCollection);
							inCollection.setId(null);
							inCollection.setDoctorId(id);
							inCollection.setLocationId(investigationCollection.getLocationId());
							inCollection.setHospitalId(investigationCollection.getHospitalId());
							inCollection = investigationRepository.save(inCollection);
							
							transactionalManagementService.addResource(inCollection.getId(), Resource.INVESTIGATION, false);
							ESInvestigationsDocument esInvestigationsDocument = new ESInvestigationsDocument();
							BeanUtil.map(inCollection, esInvestigationsDocument);
							esInvestigationsRepository.save(esInvestigationsDocument);
							response = true;
							transactionalManagementService.addResource(inCollection.getId(), Resource.INVESTIGATION, true);
						}
					}
				}
			}

			List<ProcedureNoteCollection> procedureNoteCollections = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(
					new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId)))), ProcedureNoteCollection.class, ProcedureNoteCollection.class).getMappedResults();
			
			if(procedureNoteCollections != null) {
				for(ProcedureNoteCollection procedureNoteCollection : procedureNoteCollections) {
					for(ObjectId id : drObjectIds) {
						long count = mongoTemplate.count(new Query(new Criteria("doctorId").is(id).and("locationId").is(new ObjectId(locationId))
								.and("procedureNote").is(procedureNoteCollection.getProcedureNote())), ProcedureNoteCollection.class);
						if(count == 0) {
							ProcedureNoteCollection prCollection = new ProcedureNoteCollection();
							BeanUtil.map(procedureNoteCollection, prCollection);
							prCollection.setId(null);
							prCollection.setDoctorId(id);
							prCollection.setLocationId(procedureNoteCollection.getLocationId());
							prCollection.setHospitalId(procedureNoteCollection.getHospitalId());
							prCollection = procedureNoteRepository.save(prCollection);
							
							transactionalManagementService.addResource(prCollection.getId(), Resource.PROCEDURE_NOTE, false);
							ESProcedureNoteDocument esProcedureNoteDocument = new ESProcedureNoteDocument();
							BeanUtil.map(prCollection, esProcedureNoteDocument);
							esProcedureNoteRepository.save(esProcedureNoteDocument);
							response = true;
							transactionalManagementService.addResource(prCollection.getId(), Resource.PROCEDURE_NOTE, true);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean updateLocationIdInRole() {
		Boolean response = false;
		try {
			
			Aggregation a = Aggregation.newAggregation(Aggregation.match(new Criteria("roleId").is(new ObjectId("5792556ee4b01207387a7a9c"))),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.match(new Criteria("location").size(0)));
			
			List<UserRoleCollection> userRoleCollections = mongoTemplate.aggregate(a
					, UserRoleCollection.class, UserRoleCollection.class).getMappedResults();
			
			if(userRoleCollections != null) {
				for(UserRoleCollection userRoleCollection : userRoleCollections) {
					List<LocationCollection> locationCollections = locationRepository.findByHospitalId(userRoleCollection.getHospitalId(), new Sort(Direction.ASC, "createdTime"));
					if(locationCollections != null && !locationCollections.isEmpty()) {
						userRoleCollection.setLocationId(locationCollections.get(0).getId());
						userRoleRepository.save(userRoleCollection);
						response = true;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Boolean importLandmarkLocalities() {
		
		String csvFile = "/home/ubuntu/landmarklocalities.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] obj = line.split(cvsSplitBy);
				
				CityCollection cityCollection = cityRepository.findByName(obj[3]);
				
				if(cityCollection != null) {
					LandmarkLocalityCollection landmarkLocalityCollection = new LandmarkLocalityCollection();
					landmarkLocalityCollection.setCityId(cityCollection.getId());
					landmarkLocalityCollection.setLocality(obj[0]);
					landmarkLocalityCollection.setLatitude(Double.parseDouble(obj[1]));
					landmarkLocalityCollection.setLongitude(Double.parseDouble(obj[2]));
					
					landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
					ESLandmarkLocalityDocument esLandmarkLocalityDocument = new ESLandmarkLocalityDocument();
					BeanUtil.map(landmarkLocalityCollection, esLandmarkLocalityDocument);
					esLandmarkLocalityDocument.setGeoPoint(new GeoPoint(landmarkLocalityCollection.getLatitude(), landmarkLocalityCollection.getLongitude()));
					
					transactionalManagementService.addResource(new ObjectId(esLandmarkLocalityDocument.getId()),
							Resource.LANDMARKLOCALITY, false);
					esCityService.addLocalityLandmark(esLandmarkLocalityDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean addServices() {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File("/home/ubuntu/GlobalTreatments.csv"));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		ServicesCollection servicesCollection = new ServicesCollection();
	        		servicesCollection.setAdminCreatedTime(new Date());
	        		servicesCollection.setCreatedTime(new Date());
	        		servicesCollection.setUpdatedTime(new Date());
	        		servicesCollection.setCreatedBy("ADMIN");
	        		servicesCollection.setService(line.get(0));
	        		servicesCollection.setToShow(true);
	        		
	        		if(line.size()>1) {
	        			if(!DPDoctorUtils.anyStringEmpty(line.get(1))) {
	        				String[] specialities = line.get(1).split("\\+");
	        				List<SpecialityCollection> specialityCollections = specialityRepository.find(specialities);
	        				List<ObjectId> specialityIds = (List<ObjectId>) CollectionUtils.collect(specialityCollections,
	    							new BeanToPropertyValueTransformer("id"));
	        				
	        				List<String> specialitiesList = (List<String>) CollectionUtils.collect(specialityCollections,
	    							new BeanToPropertyValueTransformer("superSpeciality"));
	        				
	        				servicesCollection.setSpecialities(specialitiesList);
	        				servicesCollection.setSpecialityIds(specialityIds);
	        			}
	        		}
	        		
	        		servicesCollection = servicesRepository.save(servicesCollection);
	        		
	        		if (servicesCollection != null) {
	        			transactionalManagementService.addResource(servicesCollection.getId(), Resource.SERVICE,
	        					false);
	    				ESServicesDocument esServicesDocument = new ESServicesDocument();
	    				BeanUtil.map(servicesCollection, esServicesDocument);
	    				esMasterService.addEditServices(esServicesDocument);
	    			}
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Boolean updateServicesAndSpecialities() {
		Boolean response = false;
		try {
			Iterator<ESDoctorDocument> doctorDocuments = esDoctorRepository.findAll().iterator();
			while (doctorDocuments.hasNext()) {
				ESDoctorDocument doctorDocument = doctorDocuments.next();
				if (doctorDocument.getSpecialities() != null && !doctorDocument.getSpecialities().isEmpty()) {
					Iterable<ESSpecialityDocument>  iterableSpecialities = esSpecialityRepository.findAll(doctorDocument.getSpecialities());
					List<String> specialities = new ArrayList<>();
					List<String> parentSpecialities = new ArrayList<>();
					if(iterableSpecialities != null) {
						for(ESSpecialityDocument esSpecialityDocument : iterableSpecialities) {
							specialities.add(esSpecialityDocument.getSuperSpeciality().toLowerCase());
							parentSpecialities.add(esSpecialityDocument.getSpeciality().toLowerCase());
						}
						doctorDocument.setSpecialitiesValue(specialities);
						doctorDocument.setParentSpecialities(parentSpecialities);
					}
				}
			

				if (doctorDocument.getServices() != null  && !doctorDocument.getServices().isEmpty()) {
					Iterable<ESServicesDocument> iterableServices = esServicesRepository.findAll(doctorDocument.getServices());
					List<String> services = new ArrayList<>();
					if(iterableServices != null) {
						for(ESServicesDocument esServicesDocument : iterableServices) {
							services.add(esServicesDocument.getService().toLowerCase());
						}
						doctorDocument.setServicesValue(services);
					}					
				}
				esDoctorRepository.save(doctorDocument);
				response = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean addServicesOfSpecialities() {
		Boolean response = false;
		try {
			List<DoctorCollection> doctorCollections = doctorRepository.findAll();
			for(DoctorCollection doctorCollection : doctorCollections) {
				List<ServicesCollection> servicesCollections = servicesRepository.findbySpeciality(doctorCollection.getSpecialities());
				Set<ObjectId> services = (Set<ObjectId>) CollectionUtils.collect(servicesCollections, new BeanToPropertyValueTransformer("id"));
				if(doctorCollection.getServices()!= null)doctorCollection.getServices().addAll(services);
				else doctorCollection.setServices(services);
				transnationalService.checkDoctor(doctorCollection.getUserId(), null);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}

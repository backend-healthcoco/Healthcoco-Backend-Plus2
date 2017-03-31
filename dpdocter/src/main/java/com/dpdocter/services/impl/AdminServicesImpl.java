package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.collections.AppLinkDetailsCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ContactUsCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;
import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;
import com.dpdocter.elasticsearch.document.ESMedicalCouncilDocument;
import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDrugRepository;
import com.dpdocter.elasticsearch.repository.ESEducationInstituteRepository;
import com.dpdocter.elasticsearch.repository.ESEducationQualificationRepository;
import com.dpdocter.elasticsearch.repository.ESMedicalCouncilRepository;
import com.dpdocter.elasticsearch.repository.ESProfessionalMembershipRepository;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.AppType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppLinkDetailsRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ContactUsRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.EducationInstituteRepository;
import com.dpdocter.repository.EducationQualificationRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MedicalCouncilRepository;
import com.dpdocter.repository.ProfessionalMembershipRepository;
import com.dpdocter.repository.ResumeRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;

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
	private DrugTypeRepository drugTypeRepository;

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
		String csvFile = "/home/ubuntu/Drugs.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

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

}

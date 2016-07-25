package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ContactUsCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.ProfessionalMembershipCollection;
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
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
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
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
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

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
    private DrugRepository drugRepository;

    @Autowired
    private DrugTypeRepository drugTypeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

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
	public List<User> getInactiveUsers(int page, int size) {
		List<User> response = null;
		try{
			List<UserCollection> userCollections = null;
			if(size > 0)userCollections = userRepository.findInactiveDoctors(true, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else userCollections = userRepository.findInactiveDoctors(true, new Sort(Direction.DESC, "createdTime"));
			if(userCollections != null){
				response = new ArrayList<User>();
				BeanUtil.map(userCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting inactive users "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive users "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Hospital> getHospitals(int page, int size) {
		List<Hospital> response = null;
		try{
			List<HospitalCollection> hospitalCollections = null;
			if(size > 0)hospitalCollections = hospitalRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
			else hospitalCollections = hospitalRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			if(hospitalCollections != null){
				response = new ArrayList<Hospital>();
				BeanUtil.map(hospitalCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive hospitals "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Location> getClinics(int page, int size, String hospitalId, Boolean isClinic, Boolean isLab, String searchTerm) {
		List<Location> response = null;
		try{
			List<LocationCollection> locationCollections = null;
			ObjectId hospitalObjectId= null;
			if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
	    	
			if(DPDoctorUtils.anyStringEmpty(searchTerm)){
				if(DPDoctorUtils.anyStringEmpty(hospitalObjectId)){
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findAll(new PageRequest(page, size, Direction.DESC, "updatedTime")).getContent();
						else locationCollections = locationRepository.findAll(new Sort(Direction.DESC, "updatedTime"));
					}else{	
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, new Sort(Direction.DESC, "updatedTime"));
					}
				}else{
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findByHospitalId(hospitalObjectId, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findByHospitalId(hospitalObjectId, new Sort(Direction.DESC, "updatedTime"));
					}else{	
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(hospitalObjectId, isClinic, isLab, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(hospitalObjectId, isClinic, isLab, new Sort(Direction.DESC, "updatedTime"));
					}
				}
			}else{
				if(DPDoctorUtils.anyStringEmpty(hospitalObjectId)){
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findByNameOrEmailAddress(searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findByNameOrEmailAddress(searchTerm, new Sort(Direction.DESC, "updatedTime"));
					}else{
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, searchTerm, new Sort(Direction.DESC, "updatedTime"));
					}				
				}else{
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findByNameOrEmailAddressAndHospitalId(hospitalObjectId, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findByNameOrEmailAddressAndHospitalId(hospitalObjectId, searchTerm, new Sort(Direction.DESC, "updatedTime"));
					}else{
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(hospitalObjectId, isClinic, isLab, searchTerm, new PageRequest(page, size, Direction.DESC, "updatedTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(hospitalObjectId, isClinic, isLab, searchTerm, new Sort(Direction.DESC, "updatedTime"));	
					}
				}
			}
			
			if(locationCollections != null){
				response = new ArrayList<Location>();
				for(LocationCollection locationCollection : locationCollections){
						if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
							for (ClinicImage clinicImage : locationCollection.getImages()) {
							    if (clinicImage.getImageUrl() != null) {
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							    }
							    if (clinicImage.getThumbnailUrl() != null) {
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
							    }
							}
						    }
						    if (locationCollection.getLogoUrl() != null)
						    	locationCollection.setLogoUrl(getFinalImageURL(locationCollection.getLogoUrl()));
						    if (locationCollection.getLogoThumbnailUrl() != null)
						    	locationCollection.setLogoThumbnailUrl(getFinalImageURL(locationCollection.getLogoThumbnailUrl()));
						    Location location = new Location();
						    BeanUtil.map(locationCollection, location);
						    response.add(location);
				    }
				}
		}catch(Exception e){
			logger.error("Error while getting clinics "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive clinics "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Resume addResumes(Resume request) {
		Resume response = null;
		try{
			ResumeCollection resumeCollection = new ResumeCollection();
			BeanUtil.map(request, resumeCollection);
			resumeCollection.setCreatedTime(new Date());
			if (request.getFile() != null) {
				request.getFile().setFileName(request.getFile().getFileName() + (new Date()).getTime());
				String path = "resumes" + File.separator + request.getType();
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getFile(), path, false);
				resumeCollection.setPath(imageURLResponse.getImageUrl());
			    }
			resumeCollection = resumeRepository.save(resumeCollection);
			if(resumeCollection != null){
				response = new Resume();
				BeanUtil.map(resumeCollection, response);
			}
			String body = mailBodyGenerator.generateEmailBody(resumeCollection.getName(), resumeCollection.getType(), "applyForPostTemplate.vm");
			 mailService.sendEmail(resumeCollection.getEmailAddress(), "Your application has been received", body, null);
		}catch(Exception e){
			logger.error("Error while adding resume "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while adding resume "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Resume> getResumes(int page, int size, String type) {
		List<Resume> response = null;
		try{
			List<ResumeCollection> resumeCollections = null;
			if(DPDoctorUtils.anyStringEmpty(type)){
				if(size > 0)resumeCollections = resumeRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
				else resumeCollections = resumeRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			}else{
				if(size > 0)resumeCollections = resumeRepository.find(type, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else resumeCollections = resumeRepository.find(type, new Sort(Direction.DESC, "createdTime"));
			}
			if(resumeCollections != null){
				response = new ArrayList<Resume>();
				for(ResumeCollection resumeCollection : resumeCollections){
					resumeCollection.setPath(getFinalImageURL(resumeCollection.getPath()));
				}
				BeanUtil.map(resumeCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting clinics "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive clinics "+ e.getMessage());
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
			List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(cityCollection.getCity() + " "
				    + cityCollection.getState() + " "+cityCollection.getCountry());

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
		} 
		finally {
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
		    br = new BufferedReader(new FileReader(csvFile));
		    while ((line = br.readLine()) != null) {
			String[] obj = line.split(cvsSplitBy);
			String drugType = obj[2];
			 DrugTypeCollection drugTypeCollection = drugTypeRepository.findByType(drugType);
			 
			 DrugType type = null;
			 if (drugTypeCollection != null) {
			 type = new DrugType();
			 drugTypeCollection.setType(drugType);
			 BeanUtil.map(drugTypeCollection, type);
			
			 }
			
			 DrugCollection drugCollection = new DrugCollection();
			 drugCollection.setCreatedBy("ADMIN");
			 drugCollection.setCreatedTime(new Date());
			 drugCollection.setUpdatedTime(new Date());
			 drugCollection.setDiscarded(false);
			 drugCollection.setDrugName(obj[1]);
			 drugCollection.setDrugType(type);
			 drugCollection.setDoctorId(null);
			 drugCollection.setHospitalId(null);
			 drugCollection.setLocationId(null);
			 drugCollection.setDrugCode(obj[0]);
			 drugCollection.setPackSize(obj[3]);
			 drugCollection.setCompanyName(obj[4]);
			 
			 if(obj.length > 5){
				 String[] genericCodesArray = obj[5].split("\\+");
				 
				 if(genericCodesArray.length > 0){
					 List<String> genericCodes = new ArrayList<String>();
					 for(String code : genericCodesArray)genericCodes.add(code);
					 drugCollection.setGenericCodes(genericCodes);
				 }
			 }
			 
			 drugCollection = drugRepository.save(drugCollection);
	
			 ESDrugDocument esDrugDocument = new ESDrugDocument();
				BeanUtil.map(drugCollection, esDrugDocument);
				if(drugCollection.getDrugType()!=null){
					esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				esDrugRepository.save(esDrugDocument);
		    }
			
			
		} catch (Exception e) {
		    e.printStackTrace();
		} 
		finally {
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
			ESDiagnosticTestDocument document = new  ESDiagnosticTestDocument();
			BeanUtil.map(diagnosticTestCollection, document);
			esDiagnosticTestRepository.save(document);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		} 
		finally {
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
			ESEducationInstituteDocument document = new  ESEducationInstituteDocument();
			BeanUtil.map(educationInstituteCollection, document);
			esEducationInstituteRepository.save(document);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		} 
		finally {
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
			ESEducationQualificationDocument document = new  ESEducationQualificationDocument();
			BeanUtil.map(educationQualificationCollection, document);
			esEducationQualificationRepository.save(document);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		} 
		finally {
		    if (br != null) {
			try {
			    br.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}
	}
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
		    return imagePath + imageURL;
		} else
		    return null;
	    }

	@Override
	public List<DoctorResponse> getDoctors(int page, int size, String locationId, String state, String searchTerm) {
		List<DoctorResponse> response = null;
		try{
			 Aggregation aggregation = null;
			 
			 Criteria criteria = null;
			 if(!DPDoctorUtils.anyStringEmpty(state)){
				 criteria = new Criteria("userState").is(state);
			 }else{
				 criteria = new Criteria("userState").ne(UserState.ADMIN);
			 }
			 
			 if(!DPDoctorUtils.anyStringEmpty(locationId)){
				 List<UserLocationCollection> userLocationCollections = userLocationRepository.findByLocationId(new ObjectId(locationId));
				 @SuppressWarnings("unchecked")
				 Collection<ObjectId> userIds = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("userId"));
				 criteria.and("id").in(userIds);
			 }
			 
			 if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
					 criteria = new Criteria().orOperator(new Criteria("emailAddress").regex("^"+searchTerm,"i").andOperator(criteria), new Criteria("firstName").regex("^"+searchTerm).andOperator(criteria));
			 }
	
			 if(size > 0){
					 aggregation = Aggregation.newAggregation(Aggregation.match(criteria), 
							 new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
				              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "updatedTime"));
			 }else{
					 aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
			              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))), Aggregation.sort(Sort.Direction.DESC, "updatedTime"));
			 }
		
	    AggregationResults<UserCollection> results = mongoTemplate.aggregate(aggregation, UserCollection.class, UserCollection.class);
	    List<UserCollection> userCollections = results.getMappedResults();
	    if(userCollections != null && !userCollections.isEmpty()){
	    	response = new ArrayList<DoctorResponse>();
	    	for(UserCollection userCollection : userCollections){
		    	DoctorResponse doctorResponse = new DoctorResponse();
		    	BeanUtil.map(userCollection, doctorResponse);
		    	List<UserRoleCollection> userRoleCollection = userRoleRepository.findByUserId(userCollection.getId());
				@SuppressWarnings("unchecked")
			    Collection<ObjectId> roleIds = CollectionUtils.collect(userRoleCollection, new BeanToPropertyValueTransformer("roleId"));
			    if(roleIds != null && !roleIds.isEmpty()){
			    	Integer count = roleRepository.findCountByIdAndRole(roleIds, RoleEnum.LOCATION_ADMIN.getRole());
			    	if(count != null && count > 0)doctorResponse.setRole(RoleEnum.LOCATION_ADMIN.getRole());
			    }
			    response.add(doctorResponse);
		    }
	    }
	    }catch(Exception e){
			logger.error("Error while getting doctors "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctors "+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<Location> getLabs(int page, int size, String hospitalId) {
		List<Location> response = null;
		try{
			List<LocationCollection> locationCollections = null;
			if(DPDoctorUtils.anyStringEmpty(hospitalId)){
				if(size > 0)locationCollections = locationRepository.findLabs(true, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.findLabs(true, new Sort(Direction.DESC, "createdTime"));
			}else{
				ObjectId hospitalObjectId= null;
				if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
		    	
				if(size > 0)locationCollections = locationRepository.findLabs(hospitalObjectId, true, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.findLabs(hospitalObjectId, true, new Sort(Direction.DESC, "createdTime"));
			}
			if(locationCollections != null){
				response = new ArrayList<Location>();
//				for(LocationCollection location : locationCollections){
//						if (location.getImages() != null && !location.getImages().isEmpty()) {
//							for (ClinicImage clinicImage : location.getImages()) {
//							    if (clinicImage.getImageUrl() != null) {
//								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
//							    }
//							    if (clinicImage.getThumbnailUrl() != null) {
//								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
//							    }
//							}
//						    }
//						    if (location.getLogoUrl() != null)
//						    	location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
//						    if (location.getLogoThumbnailUrl() != null)
//						    	location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
//					}
					BeanUtil.map(locationCollections, response);
				}
		}catch(Exception e){
			logger.error("Error while getting clinics "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive clinics "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public ContactUs addContactUs(ContactUs request) {
		ContactUs response = null;
		try{
			ContactUsCollection contactUsCollection = new ContactUsCollection();
			BeanUtil.map(request, contactUsCollection);
			contactUsCollection.setCreatedTime(new Date());
			contactUsCollection = contactUsRepository.save(contactUsCollection);
			if(contactUsCollection != null){
				response = new ContactUs();
				BeanUtil.map(contactUsCollection, response);
			}
		}catch(Exception e){
			logger.error("Error while adding contact us "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while adding contact us "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<ContactUs> getContactUs(int page, int size) {
		List<ContactUs> response = null;
		try{
			List<ContactUsCollection> contactUs = null;
			if(size > 0)contactUs = contactUsRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
			else contactUs = contactUsRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			
			if(contactUs != null){
				response = new ArrayList<ContactUs>();
				BeanUtil.map(contactUs, response);
			}
		}catch(Exception e){
			logger.error("Error while getting clinics "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive clinics "+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<Speciality> getUniqueSpecialities(String searchTerm, String updatedTime, int page, int size) {
	   List<Speciality> response = null;
	  try {
			Aggregation aggregation = null;
			
			if(DPDoctorUtils.anyStringEmpty(searchTerm)){
				aggregation = Aggregation.newAggregation(Aggregation.group("speciality").first("speciality").as("speciality"), Aggregation.sort(Sort.Direction.ASC, "speciality"));
			}else{
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("speciality").regex("^"+searchTerm,"i")), Aggregation.group("speciality").first("speciality").as("speciality"), Aggregation.sort(Sort.Direction.ASC, "speciality"));
			}
			
			AggregationResults <Speciality> groupResults = mongoTemplate.aggregate(aggregation, SpecialityCollection.class, Speciality.class);
			response = groupResults.getMappedResults();
			
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
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
			ESProfessionalMembershipDocument document = new  ESProfessionalMembershipDocument();
			BeanUtil.map(professionalMembershipCollection, document);
			esProfessionalMembershipRepository.save(document);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		} 
		finally {
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
		} 
		finally {
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
			String appType = "", appBitLink = "";  
			if(request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO.getType())){
				appType = "Healthcoco"; appBitLink = patientAppBitLink;
			}else if(request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO_PLUS.getType())){
				appType = "Healthcoco+"; appBitLink = doctorAppBitLink;
			}else if(request.getAppType().getType().equalsIgnoreCase(AppType.HEALTHCOCO_PAD.getType())){
				appType = "Healthcoco Pad"; appBitLink = ipadAppBitLink;
			} 
		
			if(!DPDoctorUtils.anyStringEmpty(request.getMobileNumber())){
				appLinkMessage.replace("{appType}", appType).replace("{appLink}", appBitLink);
				SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null, appLinkMessage, request.getMobileNumber(), "Get App Link");
				sMSServices.sendSMS(smsTrackDetail, false);
			}else if(!DPDoctorUtils.anyStringEmpty(request.getEmailAddress())){
			    String body = mailBodyGenerator.generateAppLinkEmailBody(appType, appBitLink, "appLinkTemplate.vm");
				mailService.sendEmail(request.getEmailAddress(), getAppLinkSubject.replace("{appType}", appType), body, null);
			} 		
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

}

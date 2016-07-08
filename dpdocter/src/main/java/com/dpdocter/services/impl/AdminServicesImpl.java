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
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESEducationInstituteDocument;
import com.dpdocter.elasticsearch.document.ESEducationQualificationDocument;
import com.dpdocter.elasticsearch.repository.ESDiagnosticTestRepository;
import com.dpdocter.elasticsearch.repository.ESDrugRepository;
import com.dpdocter.elasticsearch.repository.ESEducationInstituteRepository;
import com.dpdocter.elasticsearch.repository.ESEducationQualificationRepository;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.RoleEnum;
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

    @Value(value = "${image.path}")
    private String imagePath;

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
			if(DPDoctorUtils.anyStringEmpty(searchTerm)){
				if(DPDoctorUtils.anyStringEmpty(hospitalId)){
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
						else locationCollections = locationRepository.findAll(new Sort(Direction.DESC, "createdTime"));
					}else{	
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, new Sort(Direction.DESC, "createdTime"));
					}
				}else{
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findByHospitalId(hospitalId, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findByHospitalId(hospitalId, new Sort(Direction.DESC, "createdTime"));
					}else{	
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(hospitalId, isClinic, isLab, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(hospitalId, isClinic, isLab, new Sort(Direction.DESC, "createdTime"));
					}
				}
			}else{
				if(DPDoctorUtils.anyStringEmpty(hospitalId)){
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findByNameOrEmailAddress(searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findByNameOrEmailAddress(searchTerm, new Sort(Direction.DESC, "createdTime"));
					}else{
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(isClinic, isLab, searchTerm, new Sort(Direction.DESC, "createdTime"));
					}				
				}else{
					if(!isClinic && !isLab){
						if(size > 0)locationCollections = locationRepository.findByNameOrEmailAddressAndHospitalId(hospitalId, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findByNameOrEmailAddressAndHospitalId(hospitalId, searchTerm, new Sort(Direction.DESC, "createdTime"));
					}else{
						if(size > 0)locationCollections = locationRepository.findClinicsAndLabs(hospitalId, isClinic, isLab, searchTerm, new PageRequest(page, size, Direction.DESC, "createdTime"));
						else locationCollections = locationRepository.findClinicsAndLabs(hospitalId, isClinic, isLab, searchTerm, new Sort(Direction.DESC, "createdTime"));	
					}
				}
			}
			
			if(locationCollections != null){
				response = new ArrayList<Location>();
				for(LocationCollection location : locationCollections){
						if (location.getImages() != null && !location.getImages().isEmpty()) {
							for (ClinicImage clinicImage : location.getImages()) {
							    if (clinicImage.getImageUrl() != null) {
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							    }
							    if (clinicImage.getThumbnailUrl() != null) {
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
							    }
							}
						    }
						    if (location.getLogoUrl() != null)
						    	location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
						    if (location.getLogoThumbnailUrl() != null)
						    	location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
					}
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
	public Resume addResumes(Resume request) {
		Resume response = null;
		try{
			ResumeCollection resumeCollection = new ResumeCollection();
			BeanUtil.map(request, resumeCollection);
			resumeCollection.setCreatedTime(new Date());
			if (request.getFile() != null) {
				request.getFile().setFileName(request.getFile().getFileName() + (new Date()).getTime());
				String path = "resume" + File.separator + request.getType();
				// save image
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
		    int i = 0;
		    while ((line = br.readLine()) != null) {
			System.out.println(i++);
			String[] obj = line.split(cvsSplitBy);
			CityCollection cityCollection = new CityCollection();
			cityCollection.setCity(obj[1]);
			cityCollection.setState(obj[2]);
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
		System.out.println("Done");
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
		    int i = 0;
		    while ((line = br.readLine()) != null) {
		    	i=i++;
		    	
			System.out.println(i++);
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
		System.out.println("Drugs done");
		
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
		    int i = 0;
		    while ((line = br.readLine()) != null) {
			System.out.println(i++);
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
		System.out.println("Diagnostic test done");		
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
		    int i = 0;
		    while ((line = br.readLine()) != null) {
			System.out.println(i++);
			String[] obj = line.split(cvsSplitBy);
			EducationInstituteCollection educationInstituteCollection = new EducationInstituteCollection();
			educationInstituteCollection.setName(obj[1]);
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
		System.out.println("Education Institute test done");		
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
		    int i = 0;
		    while ((line = br.readLine()) != null) {
			System.out.println(i++);
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
		System.out.println("Education Qualification done");				
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
			 if(!DPDoctorUtils.anyStringEmpty(locationId)){
				 List<UserLocationCollection> userLocationCollections = userLocationRepository.findByLocationId(locationId);
				 @SuppressWarnings("unchecked")
				 Collection<String> userIds = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("userId"));
				 if(criteria == null)criteria = new Criteria("id").in(userIds);
			 }
			 if(!DPDoctorUtils.anyStringEmpty(state)){
				 if(criteria == null)criteria = new Criteria("userState").is(state);
				 else criteria.and("userState").is(state);
			 }
			 if(!DPDoctorUtils.anyStringEmpty(searchTerm)){
				 if(criteria == null)
					 criteria = new Criteria().orOperator(new Criteria("emailAddress").regex("^"+searchTerm,"i"), new Criteria("firstName").regex("^"+searchTerm,"i"));
				 else
					 criteria = new Criteria().orOperator(new Criteria("emailAddress").regex("^"+searchTerm,"i").andOperator(criteria), new Criteria("firstName").regex("^"+searchTerm).andOperator(criteria));
			 }
			 if(criteria != null){
				 if(size > 0){
					 aggregation = Aggregation.newAggregation(Aggregation.match(criteria), 
							 new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
				              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size));
				 }else{
					 aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
			              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))));
				 }
		}else{
			if(size > 0){
				 aggregation = Aggregation.newAggregation(new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
			              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size));
			 }else{
				 aggregation = Aggregation.newAggregation(new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
		              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))));
			 }
		}
			 System.out.println(aggregation.toString());
	    AggregationResults<DoctorResponse> results = mongoTemplate.aggregate(aggregation, UserCollection.class, DoctorResponse.class);
	    response = results.getMappedResults();
	    for(DoctorResponse doctorResponse : response){
	    	List<UserRoleCollection> userRoleCollection = userRoleRepository.findByUserId(doctorResponse.getId());
			@SuppressWarnings("unchecked")
		    Collection<String> roleIds = CollectionUtils.collect(userRoleCollection, new BeanToPropertyValueTransformer("roleId"));
		    if(roleIds != null && !roleIds.isEmpty()){
		    	Integer count = roleRepository.findCountByIdAndRole(roleIds, RoleEnum.LOCATION_ADMIN.getRole());
		    	if(count != null && count > 0)doctorResponse.setRole(RoleEnum.LOCATION_ADMIN.getRole());
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
				if(size > 0)locationCollections = locationRepository.findLabs(hospitalId, true, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.findLabs(hospitalId, true, new Sort(Direction.DESC, "createdTime"));
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
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("speciality").first("speciality").as("speciality"),
					 Aggregation.sort(Sort.Direction.ASC, "speciality"));
			AggregationResults <Speciality> groupResults = mongoTemplate.aggregate(aggregation, SpecialityCollection.class, Speciality.class);
			response = groupResults.getMappedResults();
			
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}

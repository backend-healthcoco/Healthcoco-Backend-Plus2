package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
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
import com.dpdocter.collections.UserCollection;
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
import com.dpdocter.repository.UserRepository;
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

//    @Autowired
//    private SolrCityService solrCityService;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Autowired
    private EducationInstituteRepository educationInstituteRepository;
    
    @Autowired
    private EducationQualificationRepository educationQualificationRepository;

//    @Autowired
//    private SolrEducationInstituteRepository solrEducationInstituteRepository;
//
//    @Autowired
//    private SolrEducationQualificationRepository solrEducationQualificationRepository;

    @Autowired
    private DiagnosticTestRepository diagnosticTestRepository;

//    @Autowired
//    private SolrDiagnosticTestRepository solrDiagnosticTestRepository;
//
//    @Autowired
//    private SolrDrugRepository solrDrugRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private DrugTypeRepository drugTypeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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
	public List<Location> getClinics(int page, int size, String hospitalId) {
		List<Location> response = null;
		try{
			List<LocationCollection> locationCollections = null;
			if(DPDoctorUtils.anyStringEmpty(hospitalId)){
				if(size > 0)locationCollections = locationRepository.findClinics(true, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.findClinics(true, new Sort(Direction.DESC, "createdTime"));
			}else{
				if(size > 0)locationCollections = locationRepository.findClinics(hospitalId, true, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.findClinics(hospitalId, true, new Sort(Direction.DESC, "createdTime"));
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
//			    SolrCityDocument solrCities = new SolrCityDocument();
//				BeanUtil.map(cityCollection, solrCities);
//				solrCities.setGeoLocation(new GeoLocation(cityCollection.getLatitude(), cityCollection.getLongitude()));
//				solrCityService.addCities(solrCities);
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
	
//			 SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
//				BeanUtil.map(drugCollection, solrDrugDocument);
//				if(drugCollection.getDrugType()!=null){
//					solrDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
//					solrDrugDocument.setDrugType(drugCollection.getDrugType().getType());
//				}
//				solrDrugRepository.save(solrDrugDocument);
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
//			SolrDiagnosticTestDocument document = new  SolrDiagnosticTestDocument();
//			BeanUtil.map(diagnosticTestCollection, document);
//			solrDiagnosticTestRepository.save(document);
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
//			SolrEducationInstituteDocument document = new  SolrEducationInstituteDocument();
//			BeanUtil.map(educationInstituteCollection, document);
//			solrEducationInstituteRepository.save(document);
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
//			SolrEducationQualificationDocument document = new  SolrEducationQualificationDocument();
//			BeanUtil.map(educationQualificationCollection, document);
//			solrEducationQualificationRepository.save(document);
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
	public List<DoctorResponse> getDoctors(int page, int size, String locationId, String state) {
		List<DoctorResponse> response = null;
		try{
			 Aggregation aggregation = null;
			 if(DPDoctorUtils.anyStringEmpty(state)){
				 if(size > 0){
					 if(DPDoctorUtils.anyStringEmpty(locationId)){
						 aggregation = Aggregation.newAggregation(new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
							              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size));
					 }else{
						 aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("locationId").is(locationId)), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
					              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size));
					 }
				 }else{
					 if(DPDoctorUtils.anyStringEmpty(locationId)){
						 aggregation = Aggregation.newAggregation(new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
							              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))));
					 }else{
						 aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("locationId").is(locationId)), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
					              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))));
					 }
				 }
			 }else{
				 if(size > 0){
					 if(DPDoctorUtils.anyStringEmpty(locationId)){
						 aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("userState").is(state)), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
							              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size));
					 }else{
						 aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("locationId").is(locationId).and("userState").is(state)), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
					              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))),Aggregation.skip((page) * size), Aggregation.limit(size));
					 }
				 }else{
					 if(DPDoctorUtils.anyStringEmpty(locationId)){
						 aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("userState").is(state)), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
							              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))));
					 }else{
						 aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("locationId").is(locationId).and("userState").is(state)), new CustomAggregationOperation(new BasicDBObject("$redact",new BasicDBObject("$cond",new BasicDBObject()
					              .append("if", new BasicDBObject("$eq", Arrays.asList("$emailAddress", "$userName"))).append("then", "$$KEEP").append("else", "$$PRUNE")))));
					 }
				 }
			 }
			 
	    AggregationResults<DoctorResponse> results = mongoTemplate.aggregate(aggregation, UserCollection.class, DoctorResponse.class);
	    response = results.getMappedResults();
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
}

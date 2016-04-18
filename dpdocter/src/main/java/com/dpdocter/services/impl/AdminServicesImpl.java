package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.EducationInstituteCollection;
import com.dpdocter.collections.EducationQualificationCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.StateCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.EducationInstituteRepository;
import com.dpdocter.repository.EducationQualificationRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.ResumeRepository;
import com.dpdocter.repository.StateRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrCountryDocument;
import com.dpdocter.solr.document.SolrDiagnosticTestDocument;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrEducationInstituteDocument;
import com.dpdocter.solr.document.SolrEducationQualificationDocument;
import com.dpdocter.solr.document.SolrStateDocument;
import com.dpdocter.solr.repository.SolrDiagnosticTestRepository;
import com.dpdocter.solr.repository.SolrDrugRepository;
import com.dpdocter.solr.repository.SolrEducationInstituteRepository;
import com.dpdocter.solr.repository.SolrEducationQualificationRepository;
import com.dpdocter.solr.services.SolrCityService;

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
    private FileManager fileManager;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LocationServices locationServices;

    @Autowired
    private SolrCityService solrCityService;

    @Autowired
    private EducationInstituteRepository educationInstituteRepository;
    
    @Autowired
    private EducationQualificationRepository educationQualificationRepository;

    @Autowired
    private SolrEducationInstituteRepository solrEducationInstituteRepository;

    @Autowired
    private SolrEducationQualificationRepository solrEducationQualificationRepository;

    @Autowired
    private DiagnosticTestRepository diagnosticTestRepository;

    @Autowired
    private SolrDiagnosticTestRepository solrDiagnosticTestRepository;

    @Autowired
    private SolrDrugRepository solrDrugRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private DrugTypeRepository drugTypeRepository;

	@Override
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
	public List<Location> getClinics(int page, int size, String hospitalId) {
		List<Location> response = null;
		try{
			List<LocationCollection> locationCollections = null;
			if(DPDoctorUtils.anyStringEmpty(hospitalId)){
				if(size > 0)locationCollections = locationRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
				else locationCollections = locationRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			}else{
				if(size > 0)locationCollections = locationRepository.find(hospitalId, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.find(hospitalId, new Sort(Direction.DESC, "createdTime"));
			}
			if(locationCollections != null){
				response = new ArrayList<Location>();
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
	public Resume addResumes(Resume request) {
		Resume response = null;
		try{
			ResumeCollection resumeCollection = new ResumeCollection();
			BeanUtil.map(request, resumeCollection);
			resumeCollection.setCreatedTime(new Date());
			if (request.getFileDetails() != null) {
				request.getFileDetails().setFileName(request.getFileDetails().getFileName() + (new Date()).getTime());
				String path = "resume" + File.separator + request.getType();
				// save image
				String resumeUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
				resumeCollection.setPath(resumeUrl);
			    }
			resumeCollection = resumeRepository.save(resumeCollection);
			if(resumeCollection != null){
				response = new Resume();
				BeanUtil.map(resumeCollection, response);
			}
		}catch(Exception e){
			logger.error("Error while adding resume "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while adding resume "+ e.getMessage());
		}
		return response;
	}

	@Override
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
	public void importCity() {
		String csvFile = "/home/ubuntu/cities.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
//			CountryCollection countryCollection = new CountryCollection();
//			countryCollection.setCountry("India");
//			List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation((countryCollection != null ? countryCollection.getCountry() : ""));
//			if (geocodedLocations != null && !geocodedLocations.isEmpty())BeanUtil.map(geocodedLocations.get(0), countryCollection);
//			countryCollection = countryRepository.save(countryCollection);
//			
//		    br = new BufferedReader(new FileReader(csvFile));
//		    int i = 0;
//		    while ((line = br.readLine()) != null) {
//			System.out.println(i++);
//			String[] obj = line.split(cvsSplitBy);
//
//			StateCollection stateCollection = stateRepository.findByName(obj[2]);
//			if(stateCollection == null){
//				stateCollection = new StateCollection();
//			    geocodedLocations = locationServices.geocodeLocation(obj[2] + " "
//				    + (countryCollection != null ? countryCollection.getCountry() : ""));
//
//			    if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), stateCollection);
//			    stateCollection.setCountryId(countryCollection.getId());
//			    stateCollection.setState(obj[2]);
//			    stateCollection = stateRepository.save(stateCollection);
//			}
//			
//			CityCollection cityCollection = new CityCollection();
//			cityCollection.setCity(obj[1]);
//			cityCollection.setStateId(stateCollection.getId());
//			geocodedLocations = locationServices.geocodeLocation(cityCollection.getCity() + " "
//				    + (stateCollection != null ? stateCollection.getState() : "")+ " "
//						    + (countryCollection != null ? countryCollection.getCountry() : ""));
//
//			    if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), cityCollection);
//
//			    cityCollection = cityRepository.save(cityCollection);
//		    }

				List<CountryCollection> countries = countryRepository.findAll();
		    	if(countries != null){
		    		for(CountryCollection country : countries){
						SolrCountryDocument solrCountry = new SolrCountryDocument();
						BeanUtil.map(country, solrCountry);
						solrCountry.setGeoLocation(new GeoLocation(country.getLatitude(), country.getLongitude()));
						solrCityService.addCountry(solrCountry);
					}
		    	}
		    	List<StateCollection> states = stateRepository.findAll();
		    	if (states != null) {
					for(StateCollection state : states){
						SolrStateDocument solrState = new SolrStateDocument();
						BeanUtil.map(state, solrState);
						solrState.setGeoLocation(new GeoLocation(state.getLatitude(), state.getLongitude()));
						solrCityService.addState(solrState);
					}
			    }
			    List<CityCollection> cities = cityRepository.findAll();
			    if (cities != null) {
				for(CityCollection city : cities){
					SolrCityDocument solrCities = new SolrCityDocument();
					BeanUtil.map(city, solrCities);
					solrCities.setGeoLocation(new GeoLocation(city.getLatitude(), city.getLongitude()));
					solrCityService.addCities(solrCities);
				}
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
		    	if(i == 100) break;
			System.out.println(i++);
			String[] obj = line.split(cvsSplitBy);
			String drugType = obj[1];
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
			 drugCollection.setDrugName(obj[0]);
			 drugCollection.setDrugType(type);
			 drugCollection.setDoctorId(null);
			 drugCollection.setHospitalId(null);
			 drugCollection.setLocationId(null);
			
			 drugCollection = drugRepository.save(drugCollection);
	
			 SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
				BeanUtil.map(drugCollection, solrDrugDocument);
				if(drugCollection.getDrugType()!=null){
					solrDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					solrDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				solrDrugRepository.save(solrDrugDocument);
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
			SolrDiagnosticTestDocument document = new  SolrDiagnosticTestDocument();
			BeanUtil.map(diagnosticTestCollection, document);
			solrDiagnosticTestRepository.save(document);
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
			SolrEducationInstituteDocument document = new  SolrEducationInstituteDocument();
			BeanUtil.map(educationInstituteCollection, document);
			solrEducationInstituteRepository.save(document);
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
			SolrEducationQualificationDocument document = new  SolrEducationQualificationDocument();
			BeanUtil.map(educationQualificationCollection, document);
			solrEducationQualificationRepository.save(document);
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

}

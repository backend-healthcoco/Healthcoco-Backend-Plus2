package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.DeliveryReportsCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.IPDReportsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OPDReportsCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DeliveryReportsRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.IPDReportsRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.OTReportsRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.ReportsService;

import common.util.web.DPDoctorUtils;

@Service
public class ReportsServiceImpl implements ReportsService {

	private static Logger logger = Logger.getLogger(ReportsServiceImpl.class.getName());

	@Autowired
	IPDReportsRepository ipdReportsRepository;

	@Autowired
	OPDReportsRepository opdReportsRepository;

	@Autowired
	OTReportsRepository otReportsRepository;
	
	@Autowired
	DeliveryReportsRepository deliveryReportsRepository;
	
	@Autowired
	PrescriptionRepository prescriptionRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PatientRepository patientRepository;
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	HospitalRepository hospitalRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	@Transactional
	public IPDReports submitIPDReport(IPDReports ipdReports) {
		IPDReports response = null;
		IPDReportsCollection ipdReportsCollection = new IPDReportsCollection();
		if(ipdReports != null)
		{
			BeanUtil.map(ipdReports, ipdReportsCollection);
			try {
				ipdReportsCollection.setCreatedTime(new Date());
				ipdReportsCollection = ipdReportsRepository.save(ipdReportsCollection);
				
				if(ipdReportsCollection != null)
				{
					BeanUtil.map(ipdReportsCollection, ipdReports);
					response = new IPDReports();
					response = ipdReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating IPD Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while creating IPD Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public OPDReports submitOPDReport(OPDReports opdReports) {
		OPDReports response = null;
		OPDReportsCollection opdReportsCollection = new OPDReportsCollection();
		if(opdReports != null)
		{
			BeanUtil.map(opdReports, opdReportsCollection);
			try {
				opdReportsCollection.setCreatedTime(new Date());
				opdReportsCollection = opdReportsRepository.save(opdReportsCollection);
				
				if(opdReportsCollection != null)
				{
					BeanUtil.map(opdReportsCollection, opdReports);
					response = new OPDReports();
					response = opdReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating OPD Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while OPD Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public OTReports submitOTReport(OTReports otReports) {
		OTReports response = null;
		OTReportsCollection otReportsCollection = new OTReportsCollection();
		if(otReports != null)
		{
			BeanUtil.map(otReports, otReportsCollection);
			try {
				otReportsCollection.setCreatedTime(new Date());
				otReportsCollection = otReportsRepository.save(otReportsCollection);
				
				if(otReportsCollection != null)
				{
					BeanUtil.map(otReportsCollection, otReports);
					response = new OTReports();
					response = otReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating OT Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while OT Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public DeliveryReports submitDeliveryReport(DeliveryReports deliveryReports) {
		DeliveryReports response = null;
		DeliveryReportsCollection deliveryReportsCollection = new DeliveryReportsCollection();
		if(deliveryReports != null)
		{
			BeanUtil.map(deliveryReports, deliveryReportsCollection);
			try {
				deliveryReportsCollection.setCreatedTime(new Date());
				deliveryReportsCollection = deliveryReportsRepository.save(deliveryReportsCollection);
				
				if(deliveryReportsCollection != null)
				{
					BeanUtil.map(deliveryReportsCollection, deliveryReports);
					response = new DeliveryReports();
					response = deliveryReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating Delivery Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while Delivery Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
    public List<IPDReports> getIPDReportsList(String locationId, String doctorId, String patientId, String from, String to, int page, int size, String updatedTime) {
		List<IPDReports> response = null;
		List<IPDReportsCollection> ipdReportsCollections = null;
		try {		
			
			//long updatedTimeStamp = Long.parseLong(updatedTime);
			//Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
		    Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(new ObjectId(locationId));
		    
		    if(doctorId != null)criteria.and("doctorId").is(new ObjectId(doctorId));
		    
		    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(new ObjectId(patientId));
		    
		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		    if(!DPDoctorUtils.anyStringEmpty(from)){
		    	localCalendar.setTime(new Date(Long.parseLong(from)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
			    
		    	criteria.and("createdTime").gte(fromTime);
		    }
		    else if(!DPDoctorUtils.anyStringEmpty(to)){
		    	localCalendar.setTime(new Date(Long.parseLong(to)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    	
		    	criteria.and("createdTime").lte(toTime);
		    }

		    
		    Query query = new Query(criteria);
		    if(size > 0) ipdReportsCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.DESC, "createdTime")), IPDReportsCollection.class);
		    else ipdReportsCollections = mongoTemplate.find(query.with(new Sort(Direction.DESC, "createdTime")), IPDReportsCollection.class);
			
		    if (ipdReportsCollections != null) {
			    response = new ArrayList<IPDReports>();   
			    for(IPDReportsCollection collection : ipdReportsCollections){
			    	IPDReports ipdReports = new IPDReports();
			     	BeanUtil.map(collection, ipdReports);
			    	if(collection.getDoctorId() != null){
			    		UserCollection doctor = userRepository.findOne(collection.getDoctorId());
			    		if(doctor != null)ipdReports.setDoctorName(doctor.getFirstName());
			    	}
			    	if(collection.getLocationId() != null){
			    		LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
			    		if(locationCollection != null){
			    			ipdReports.setLocationName(locationCollection.getLocationName());
			    		}
			    	}
			    	if(collection.getHospitalId() !=null){
			    		HospitalCollection hospitalCollection = hospitalRepository.findOne(collection.getHospitalId());
			    		if(hospitalCollection != null){
			    			ipdReports.setHospitalName(hospitalCollection.getHospitalName());
			    		}
			    	}
			    	if(collection.getPatientId() !=null)
			    	{
			    		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(collection.getPatientId(), collection.getDoctorId(), collection.getLocationId(), collection.getHospitalId());
			    		if(patientCollection != null){
			    			Patient patient = new Patient();
			    			BeanUtil.map(patientCollection, patient);
			    			ipdReports.setPatient(patient);
			    		}
			    	}
			    	response.add(ipdReports);
			    }
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
 }

	

	@Override
	@Transactional
	public List<OPDReports> getOPDReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		// TODO Auto-generated method stub
		List<OPDReports> response = null;
		List<OPDReportsCollection> opdReportsCollections = null;
		try {		
			
			//long updatedTimeStamp = Long.parseLong(updatedTime);
			//Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
			Criteria criteria = new Criteria();
		    if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(new ObjectId(locationId));
		    
		    if(doctorId != null)criteria.and("doctorId").is(new ObjectId(doctorId));
		    
		    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(new ObjectId(patientId));
		    
		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		    if(!DPDoctorUtils.anyStringEmpty(from)){
		    	localCalendar.setTime(new Date(Long.parseLong(from)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
			    
		    	criteria.and("createdTime").gte(fromTime);
		    }
		    else if(!DPDoctorUtils.anyStringEmpty(to)){
		    	localCalendar.setTime(new Date(Long.parseLong(to)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    	
		    	criteria.and("createdTime").lte(toTime);
		    }

		    
		    Query query = new Query(criteria);
		    if(size > 0) opdReportsCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.DESC, "createdTime")), OPDReportsCollection.class);
		    else opdReportsCollections = mongoTemplate.find(query.with(new Sort(Direction.DESC, "createdTime")), OPDReportsCollection.class);
			
		    if (opdReportsCollections != null) {
			    response = new ArrayList<OPDReports>();   
			    for(OPDReportsCollection collection : opdReportsCollections){
			    	OPDReports opdReports = new OPDReports();
			     	BeanUtil.map(collection, opdReports);
			    	if(collection.getDoctorId() != null){
			    		UserCollection doctor = userRepository.findOne(collection.getDoctorId());
			    		if(doctor != null)opdReports.setDoctorName(doctor.getFirstName());
			    	}
			    	if(collection.getLocationId() != null){
			    		LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
			    		if(locationCollection != null){
			    			opdReports.setLocationName(locationCollection.getLocationName());
			    		}
			    	}
			    	if(collection.getHospitalId() !=null){
			    		HospitalCollection hospitalCollection = hospitalRepository.findOne(collection.getHospitalId());
			    		if(hospitalCollection != null){
			    			opdReports.setHospitalName(hospitalCollection.getHospitalName());
			    		}
			    	}
			    	if(collection.getPatientId() !=null)
			    	{
			    		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(collection.getPatientId(), collection.getDoctorId(), collection.getLocationId(), collection.getHospitalId());
			    		if(patientCollection != null){
			    			Patient patient = new Patient();
			    			BeanUtil.map(patientCollection, patient);
			    			opdReports.setPatient(patient);
			    		}
			    	}
			    	
			    	if(collection.getPrescriptionId() !=null)
			    	{
			    		PrescriptionCollection prescriptionCollection = prescriptionRepository.findOne(collection.getPrescriptionId());
			    		if(prescriptionCollection != null){
			    			Prescription prescription = new Prescription();
			    			BeanUtil.map(prescriptionCollection, prescription);
			    			opdReports.setPrescription(prescription);
			    		}
			    	}
			    	
			    	response.add(opdReports);
			    }
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<OTReports> getOTReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		// TODO Auto-generated method stub
		List<OTReports> response = null;
		List<OTReportsCollection> otReportsCollections = null;
		try {		
			
			//long updatedTimeStamp = Long.parseLong(updatedTime);
			//Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
		    Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(new ObjectId(locationId));
		    
		    if(doctorId != null)criteria.and("doctorId").is(new ObjectId(doctorId));
		    
		    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(new ObjectId(patientId));
		    
		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		    if(!DPDoctorUtils.anyStringEmpty(from)){
		    	localCalendar.setTime(new Date(Long.parseLong(from)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
			    
		    	criteria.and("createdTime").gte(fromTime);
		    }
		    else if(!DPDoctorUtils.anyStringEmpty(to)){
		    	localCalendar.setTime(new Date(Long.parseLong(to)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    	
		    	criteria.and("createdTime").lte(toTime);
		    }

		    
		    Query query = new Query(criteria);
		    if(size > 0) otReportsCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.DESC, "createdTime")), OTReportsCollection.class);
		    else otReportsCollections = mongoTemplate.find(query.with(new Sort(Direction.DESC, "createdTime")), OTReportsCollection.class);
			
		    if (otReportsCollections != null) {
			    response = new ArrayList<OTReports>();   
			    for(OTReportsCollection collection : otReportsCollections){
			    	OTReports otReports = new OTReports();
			     	BeanUtil.map(collection, otReports);
			    	if(collection.getDoctorId() != null){
			    		UserCollection doctor = userRepository.findOne(collection.getDoctorId());
			    		if(doctor != null)otReports.setDoctorName(doctor.getFirstName());
			    	}
			    	if(collection.getLocationId() != null){
			    		LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
			    		if(locationCollection != null){
			    			otReports.setLocationName(locationCollection.getLocationName());
			    			
			    		}
			    	}
			    	if(collection.getHospitalId() !=null){
			    		HospitalCollection hospitalCollection = hospitalRepository.findOne(collection.getHospitalId());
			    		if(hospitalCollection != null){
			    			otReports.setHospitalName(hospitalCollection.getHospitalName());
			    		}
			    	}
			    	if(collection.getPatientId() !=null)
			    	{
			    		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(collection.getPatientId(), collection.getDoctorId(), collection.getLocationId(), collection.getHospitalId());
			    		if(patientCollection != null){
			    			Patient patient = new Patient();
			    			BeanUtil.map(patientCollection, patient);
			    			otReports.setPatient(patient);
			    		}
			    	}
			    	
			    	
			    	response.add(otReports);
			    }
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<DeliveryReports> getDeliveryReportsList(String locationId, String doctorId, String patientId,
			String from, String to, int page, int size, String updatedTime) {
		// TODO Auto-generated method stub
		List<DeliveryReports> response = null;
		List<DeliveryReportsCollection> deliveryReportsCollections = null;
		try {		
			
			//long updatedTimeStamp = Long.parseLong(updatedTime);
			//Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
		   Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(new ObjectId(locationId));
		    
		    if(doctorId != null)criteria.and("doctorId").is(new ObjectId(doctorId));
		    
		    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(new ObjectId(patientId));
		    
		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		    if(!DPDoctorUtils.anyStringEmpty(from)){
		    	localCalendar.setTime(new Date(Long.parseLong(from)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
			    
		    	criteria.and("createdTime").gte(fromTime);
		    }
		    else if(!DPDoctorUtils.anyStringEmpty(to)){
		    	localCalendar.setTime(new Date(Long.parseLong(to)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    	
		    	criteria.and("createdTime").lte(toTime);
		    }

		    
		    Query query = new Query(criteria);
		    if(size > 0) deliveryReportsCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.DESC, "createdTime")), DeliveryReportsCollection.class);
		    else deliveryReportsCollections = mongoTemplate.find(query.with(new Sort(Direction.DESC, "createdTime")), DeliveryReportsCollection.class);
			
		    if (deliveryReportsCollections != null) {
			    response = new ArrayList<DeliveryReports>();   
			    for(DeliveryReportsCollection collection : deliveryReportsCollections){
			    	DeliveryReports deliveryReports = new DeliveryReports();
			     	BeanUtil.map(collection, deliveryReports);
			    	if(collection.getDoctorId() != null){
			    		UserCollection doctor = userRepository.findOne(collection.getDoctorId());
			    		if(doctor != null)deliveryReports.setDoctorName(doctor.getFirstName());
			    	}
			    	if(collection.getLocationId() != null){
			    		LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
			    		if(locationCollection != null){
			    			deliveryReports.setLocationName(locationCollection.getLocationName());
			    			
			    		}
			    	}
			    	if(collection.getHospitalId() !=null){
			    		HospitalCollection hospitalCollection = hospitalRepository.findOne(collection.getHospitalId());
			    		if(hospitalCollection != null){
			    			deliveryReports.setHospitalName(hospitalCollection.getHospitalName());
			    		}
			    	}
			    	if(collection.getPatientId() !=null)
			    	{
			    		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(collection.getPatientId(), collection.getDoctorId(), collection.getLocationId(), collection.getHospitalId());
			    		if(patientCollection != null){
			    			Patient patient = new Patient();
			    			BeanUtil.map(patientCollection, patient);
			    			deliveryReports.setPatient(patient);
			    		}
			    	}
			    	
			    	
			    	response.add(deliveryReports);
			    }
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	
	

}

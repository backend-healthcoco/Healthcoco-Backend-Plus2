package com.dpdocter.services.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionReferenceCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientFeedbackCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionReferenceRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.services.NutritionService;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionServiceImpl implements NutritionService{

	@Autowired
	private NutritionReferenceRepository nutritionReferenceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Override
	@Transactional
	public NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request) {
		NutritionReferenceResponse response = null;
		NutritionReferenceCollection nutritionReferenceCollection = null;
		try {

			response = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutritionReferenceCollection = nutritionReferenceRepository.findOne(new ObjectId(request.getId()));
			}
			if (nutritionReferenceCollection == null) {
				nutritionReferenceCollection = new NutritionReferenceCollection();
			}
			BeanUtil.map(request, nutritionReferenceCollection);
			nutritionReferenceCollection = nutritionReferenceRepository.save(nutritionReferenceCollection);
			if (nutritionReferenceCollection != null) {
				response = new NutritionReferenceResponse();
				BeanUtil.map(nutritionReferenceCollection, response);
				
				
				if(response.getLocationId() != null)
				{
					LocationCollection locationCollection = locationRepository.findOne(new ObjectId(response.getLocationId()));
					response.setLocationName(locationCollection.getLocationName());
				}
				if(response.getDoctorId() != null)
				{
					UserCollection userCollection = userRepository.findOne(new ObjectId(response.getDoctorId()));
					response.setDoctorName(userCollection.getFirstName());
				}
				
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()), new ObjectId(response.getHospitalId())); 
				if(patientCollection != null)
				{
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientCard);
					response.setPatient(patientCard);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}
	
	@Override
	@Transactional
	public List<NutritionReferenceResponse> getNutritionReferenceList(String doctorId, String locationId, int page , int size) {
		List<NutritionReferenceResponse> nutritionReferenceResponses= null;
		LocationCollection locationCollection = null;
		UserCollection userCollection = null;
		try {
			Criteria criteria = new Criteria();
			
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				criteria.and("doctorId").is(new ObjectId(doctorId));
				userCollection = userRepository.findOne(new ObjectId(doctorId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				locationCollection = locationRepository.findOne(new ObjectId(locationId));
			}
			if (size > 0)
				nutritionReferenceResponses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(page * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), NutritionReferenceCollection.class,
						NutritionReferenceResponse.class).getMappedResults();
			else
				nutritionReferenceResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						NutritionReferenceCollection.class, NutritionReferenceResponse.class).getMappedResults();
			
			for (NutritionReferenceResponse nutritionReferenceResponse : nutritionReferenceResponses) {
				
				if(locationCollection != null)
				{
					nutritionReferenceResponse.setLocationName(locationCollection.getLocationName());
				}
				if(userCollection != null)
				{
					nutritionReferenceResponse.setDoctorName(userCollection.getFirstName());
				}
				
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(new ObjectId(nutritionReferenceResponse.getPatientId()), new ObjectId(nutritionReferenceResponse.getLocationId()), new ObjectId(nutritionReferenceResponse.getHospitalId())); 
				if(patientCollection != null)
				{
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientCard);
					nutritionReferenceResponse.setPatient(patientCard);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return nutritionReferenceResponses;
	}
	
}

package com.dpdocter.services.impl;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Locale;
import com.dpdocter.beans.Location;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RecommendationType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.LocaleService;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactoryClass;

@Service
public class LocaleServiceImpl implements LocaleService {

	@Autowired
	LocaleRepository localeRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RecommendationsRepository recommendationsRepository;

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());
	
	@Override
	@Transactional
	public Locale getLocaleDetails(String id) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findOne(new ObjectId(id));
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		response = new Locale();
		BeanUtil.map(localeCollection, response);

		return response;
	}

	@Override
	@Transactional
	public Locale getLocaleDetailsByContactDetails(String contactNumber) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findByMobileNumber(contactNumber);
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		response = new Locale();
		BeanUtil.map(localeCollection, response);

		return response;
	}
	
	@Override
	public Locale addEditRecommedation(String localeId, String patientId,RecommendationType type) {
		Locale response;

		try {

			ObjectId localeObjectId = new ObjectId(localeId);
			ObjectId patientObjectId = new ObjectId(patientId);
			RecommendationsCollection recommendationsCollection = null;

			LocaleCollection localeCollection = localeRepository.findOne(localeObjectId);

			UserCollection userCollection = userRepository.findOne(patientObjectId);

			if (userCollection != null & localeCollection != null) {
				recommendationsCollection = recommendationsRepository.findByDoctorIdLocationIdAndPatientId(null,
						localeObjectId, patientObjectId);
				
				if(recommendationsCollection == null)
				{
					recommendationsCollection = new RecommendationsCollection();
					recommendationsCollection.setLocationId(localeObjectId);
					recommendationsCollection.setPatientId(patientObjectId);
					localeCollection
							.setNoOfLocaleRecommendation(1);
				}
				else
				{
					switch (type) {
					case LIKE:
						localeCollection
						.setNoOfLocaleRecommendation(localeCollection.getNoOfLocaleRecommendation() + 1);
						recommendationsCollection.setDiscarded(false);
						break;
						
					case UNLIKE:
						localeCollection
						.setNoOfLocaleRecommendation(localeCollection.getNoOfLocaleRecommendation() - 1);
						recommendationsCollection.setDiscarded(true);
						break;

					default:
						break;
					}
				}

				recommendationsCollection = recommendationsRepository.save(recommendationsCollection);
				localeCollection = localeRepository.save(localeCollection);
				response = new Locale();
				BeanUtil.map(localeCollection, response);
				response.setIsLocaleRecommended(!recommendationsCollection.getDiscarded());
			//	response.setIsClinicRecommended(!recommendationsCollection.getDiscarded());

			} else {
				throw new BusinessException(ServiceError.Unknown, "Error  location  not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error recommending");
		}

		return response;
	}

}

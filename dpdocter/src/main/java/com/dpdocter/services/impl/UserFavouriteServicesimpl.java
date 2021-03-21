package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserResourceFavouriteCollection;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserResourceFavouriteRepository;
import com.dpdocter.response.FavouriteLookupResponse;
import com.dpdocter.services.UserFavouriteService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class UserFavouriteServicesimpl implements UserFavouriteService {

	private static Logger logger = LogManager.getLogger(UserFavouriteServicesimpl.class.getName());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private UserResourceFavouriteRepository userResourceFavouriteRepository;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public Boolean addRemoveFavourites(String userId, String resourceId, String resourceType, String locationId, Boolean discarded) {
		Boolean response = false;
		try {
			ObjectId userObjectId = new ObjectId(userId), 
					 resourceObjectId = new ObjectId(resourceId),
					 locationObjectId = !DPDoctorUtils.anyStringEmpty(locationId) ? new ObjectId(locationId) : null;
			UserResourceFavouriteCollection resourceFavouriteCollection = userResourceFavouriteRepository.findByUserIdAndResourceIdAndResourceTypeAndLocationId(userObjectId, resourceObjectId, resourceType.toUpperCase(), locationObjectId);
			if(discarded) {
				if(resourceFavouriteCollection == null)throw new BusinessException(ServiceError.Unknown,resourceType+" does not exist in your favourites list.");
				else {
					resourceFavouriteCollection.setDiscarded(true);
					resourceFavouriteCollection.setUpdatedTime(new Date());
					userResourceFavouriteRepository.save(resourceFavouriteCollection);
					response = true;
			}
			}else {
				if(resourceFavouriteCollection == null) {
					resourceFavouriteCollection = new UserResourceFavouriteCollection();
					resourceFavouriteCollection.setDiscarded(false);
					resourceFavouriteCollection.setUserId(userObjectId);
					resourceFavouriteCollection.setResourceId(resourceObjectId);
					resourceFavouriteCollection.setResourceType(Resource.valueOf(resourceType.toUpperCase()));
					resourceFavouriteCollection.setLocationId(locationObjectId);
					resourceFavouriteCollection.setUpdatedTime(new Date());
					resourceFavouriteCollection.setCreatedTime(new Date());
					userResourceFavouriteRepository.save(resourceFavouriteCollection);
					response = true;
				}else if(resourceFavouriteCollection.getDiscarded()) {
						resourceFavouriteCollection.setDiscarded(false);
						resourceFavouriteCollection.setUpdatedTime(new Date());
						userResourceFavouriteRepository.save(resourceFavouriteCollection);
						response = true;
				}
			}
		}catch(Exception e){
			logger.error("Error while adding favourite resource "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting favourite resource.");
		}
		return response;
	}

	@Override
	public List<ESDoctorDocument> getFavouriteDoctors(long page, int size, String userId) {
		List<ESDoctorDocument> response = null;
		try {
			Aggregation aggregation = null;
			
					
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.DOCTOR.name()).and("discarded").is(false)),
					Aggregation.lookup("docter_cl", "resourceId", "userId", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("user_cl", "resourceId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("location_cl", "locationId", "_id", "lab"), Aggregation.unwind("lab"),
					Aggregation.lookup("doctor_clinic_profile_cl", "resourceId", "doctorId", "clinicProfileCollection"),
					Aggregation.unwind("clinicProfileCollection"),
					new CustomAggregationOperation(new Document("$redact",new BasicDBObject("$cond",
							new BasicDBObject("if", new BasicDBObject("$eq", Arrays.asList("$clinicProfileCollection.locationId", "$locationId")))
							.append("then", "$$KEEP").append("else", "$$PRUNE")))),
					Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
					Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.DOCTOR.name()).and("discarded").is(false)),
						Aggregation.lookup("docter_cl", "resourceId", "userId", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("user_cl", "resourceId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("location_cl", "locationId", "_id", "lab"), Aggregation.unwind("lab"),
						Aggregation.lookup("doctor_clinic_profile_cl", "resourceId", "doctorId", "clinicProfileCollection"), Aggregation.unwind("clinicProfileCollection"),
						new CustomAggregationOperation(new Document("$redact",new BasicDBObject("$cond",
								new BasicDBObject("if", new BasicDBObject("$eq", Arrays.asList("$clinicProfileCollection.locationId", "$locationId")))
								.append("then", "$$KEEP").append("else", "$$PRUNE")))),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			
			List<FavouriteLookupResponse> doctors = mongoTemplate.aggregate(aggregation, UserResourceFavouriteCollection.class, FavouriteLookupResponse.class).getMappedResults();
			
			if(doctors != null) {
				response = new ArrayList<ESDoctorDocument>();
				for(FavouriteLookupResponse favouriteLookupResponse : doctors) {
					DoctorCollection doctor = favouriteLookupResponse.getDoctor();
					UserCollection user = favouriteLookupResponse.getUser();
					LocationCollection location = favouriteLookupResponse.getLab();
					
					
					
					ESDoctorDocument doctorDocument = new ESDoctorDocument();
					BeanUtil.map(location, doctorDocument);
					BeanUtil.map(favouriteLookupResponse.getClinicProfileCollection(), doctorDocument);
					BeanUtil.map(doctor, doctorDocument);
					BeanUtil.map(user, doctorDocument);
					
					String address = (!DPDoctorUtils.anyStringEmpty(location.getStreetAddress())
							? location.getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(location.getLandmarkDetails())
									? location.getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(location.getLocality())
									? location.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(location.getCity())
									? location.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(location.getState())
									? location.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(location.getCountry())
									? location.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(location.getPostalCode())
									? location.getPostalCode() : "");

					if (!DPDoctorUtils.anyStringEmpty(address) && address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					doctorDocument.setClinicAddress(address);

					doctorDocument.setImageUrl(getFinalImageURL(user.getImageUrl()));
					doctorDocument.setCoverImageUrl(getFinalImageURL(user.getCoverImageUrl()));
					doctorDocument.setCoverThumbnailImageUrl(getFinalImageURL(user.getCoverThumbnailImageUrl()));
					
					if (location.getImages() != null) {
						List<String> images = new ArrayList<String>();
						for (ClinicImage clinicImage : location.getImages()) {
							images.add(getFinalImageURL(clinicImage.getImageUrl()));
						}
						doctorDocument.setImages(images);
					}
					response.add(doctorDocument);
				}
			}
				
		}catch(Exception e){
			logger.error("Error while getting favourite doctors "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting favourite doctors.");
		}
		return response;
	}

	@Override
	public List<ESUserLocaleDocument> getFavouritePharmacies(long page, int size, String userId) {
		List<ESUserLocaleDocument> response = null;
		try {
			Aggregation aggregation = null;
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.PHARMACY.name()).and("discarded").is(false)),
					Aggregation.lookup("locale_cl", "resourceId", "_id", "pharmacy"), Aggregation.unwind("pharmacy"),
					Aggregation.lookup("user_cl", "$pharmacy.contactNumber", "mobileNumber", "user"), Aggregation.unwind("user"),
					Aggregation.match(new Criteria("user.userState").is(Resource.PHARMACY.getType())),
					Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
					Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.PHARMACY.name()).and("discarded").is(false)),
						Aggregation.lookup("locale_cl", "resourceId", "_id", "pharmacy"), Aggregation.unwind("pharmacy"),
						Aggregation.lookup("user_cl", "$pharmacy.contactNumber", "mobileNumber", "user"), Aggregation.unwind("user"),
						Aggregation.match(new Criteria("user.userState").is(Resource.PHARMACY.getType())),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			List<FavouriteLookupResponse> favouriteLookupResponses = mongoTemplate.aggregate(aggregation, UserResourceFavouriteCollection.class, FavouriteLookupResponse.class).getMappedResults();
			if(favouriteLookupResponses != null) {
				response = new ArrayList<ESUserLocaleDocument>();
				for(FavouriteLookupResponse favouriteLookupResponse : favouriteLookupResponses) {
					ESUserLocaleDocument locale = new ESUserLocaleDocument();
					BeanUtil.map(favouriteLookupResponse.getUser(), locale);
					BeanUtil.map(favouriteLookupResponse.getPharmacy(), locale);
					locale.setLocaleId(favouriteLookupResponse.getPharmacy().getId().toString());
					if (locale.getLocaleImages() != null) {
						for (LocaleImage localImage : locale.getLocaleImages()) {
							localImage.setImageUrl(getFinalImageURL(localImage.getImageUrl()));
							localImage.setThumbnailUrl(getFinalImageURL(localImage.getThumbnailUrl()));
							}
						}
					response.add(locale);
				}
			}
		}catch(Exception e){
			logger.error("Error while getting favourite pharmacies "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting favourite pharmacies.");
		}
		return response;
	}

	@Override
	public List<LabResponse> getFavouriteLabs(long page, int size, String userId) {
		List<LabResponse> response = null;
		try {
			Aggregation aggregation = null;
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.LAB.name()).and("discarded").is(false)),
					Aggregation.lookup("location_cl", "resourceId", "_id", "lab"), Aggregation.unwind("lab"),
					Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
					Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.LAB.name()).and("discarded").is(false)),
						Aggregation.lookup("location_cl", "resourceId", "_id", "lab"), Aggregation.unwind("lab"),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			
			List<FavouriteLookupResponse> favouriteLookupResponses = mongoTemplate.aggregate(aggregation, UserResourceFavouriteCollection.class, FavouriteLookupResponse.class).getMappedResults();
			if(favouriteLookupResponses != null) {
				response = new ArrayList<LabResponse>();
				for(FavouriteLookupResponse favouriteLookupResponse : favouriteLookupResponses) {
					LabResponse labResponse = new LabResponse();
					BeanUtil.map(favouriteLookupResponse.getLab(), labResponse);
					labResponse.setLocationId(favouriteLookupResponse.getLab().getId().toString());
					List<String> images = new ArrayList<String>();
					
					if (favouriteLookupResponse.getLab().getImages() != null)
						for (ClinicImage clinicImage : favouriteLookupResponse.getLab().getImages()) {
							String imgURL = getFinalImageURL(clinicImage.getImageUrl());
							if(!DPDoctorUtils.anyStringEmpty(imgURL))images.add(imgURL);
					}
					labResponse.setImages(images);
					if (favouriteLookupResponse.getLab().getLogoUrl() != null)
						labResponse.setLogoUrl(getFinalImageURL(favouriteLookupResponse.getLab().getLogoUrl()));
					
					String address = (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getStreetAddress())
							? favouriteLookupResponse.getLab().getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getLandmarkDetails())
									? favouriteLookupResponse.getLab().getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getLocality()) ? favouriteLookupResponse.getLab().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getCity()) ? favouriteLookupResponse.getLab().getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getState()) ? favouriteLookupResponse.getLab().getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getCountry()) ? favouriteLookupResponse.getLab().getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(favouriteLookupResponse.getLab().getPostalCode()) ? favouriteLookupResponse.getLab().getPostalCode() : "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					labResponse.setClinicAddress(address);
					response.add(labResponse);
				}
			}
		}catch(Exception e){
			logger.error("Error while getting favourite doctors "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting favourite doctors.");
		}
		return response;
	}
	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}

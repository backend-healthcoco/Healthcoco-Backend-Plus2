package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Locale;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.UserResourceFavouriteCollection;
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

	private static Logger logger = Logger.getLogger(UserFavouriteServicesimpl.class.getName());
	
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
			UserResourceFavouriteCollection resourceFavouriteCollection = userResourceFavouriteRepository.find(userObjectId, resourceObjectId, resourceType.toUpperCase(), locationObjectId);
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
	public List<DoctorInfo> getFavouriteDoctors(int page, int size, String userId) {
		List<DoctorInfo> response = null;
		try {
			Aggregation aggregation = null;
			
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("resourceId", "$resourceId"), Fields.field("title", "$user.title"),
							Fields.field("firstName", "$user.firstName"), Fields.field("countryCode", "$user.countryCode"),
							Fields.field("mobileNumber", "$user.mobileNumber"), Fields.field("emailAddress", "$user.emailAddress"),
							Fields.field("imageUrl", "$user.imageUrl"), Fields.field("thumbnailUrl", "$user.thumbnailUrl"),
							Fields.field("coverImageUrl", "$user.coverImageUrl"), Fields.field("coverThumbnailImageUrl", "$user.coverThumbnailImageUrl"),
							Fields.field("locationId", "$locationId"), Fields.field("locationName", "$location.locationName"),
							Fields.field("latitude", "$location.latitude"), Fields.field("longitude", "$location.longitude"),
							Fields.field("updatedTime", "$updatedTime"),
							Fields.field("experience", "$doctor.experience"),
							Fields.field("colorCode", "$user.colorCode"),
							Fields.field("images", "$location.images"),
							Fields.field("country", "$location.country"),
							Fields.field("state", "$location.state"),
							Fields.field("city", "$location.city"),
							Fields.field("postalCode", "$location.postalCode"),
							Fields.field("streetAddress", "$location.streetAddress"),
							Fields.field("locality", "$location.locality"),
							Fields.field("landmarkDetails", "$location.landmarkDetails")));
			
			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$resourceId")
					.append("title", new BasicDBObject("$first", "$title"))
					.append("firstName", new BasicDBObject("$first", "$firstName"))
					.append("countryCode", new BasicDBObject("$first", "$countryCode"))
					.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
					.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
					.append("imageUrl", new BasicDBObject("$first", "$imageUrl"))
					.append("thumbnailUrl", new BasicDBObject("$first", "$thumbnailUrl"))
					.append("coverImageUrl", new BasicDBObject("$first", "$coverImageUrl"))
					.append("coverThumbnailImageUrl", new BasicDBObject("$first", "$coverThumbnailImageUrl"))
					.append("locationId", new BasicDBObject("$first", "$locationId"))
					.append("locationName", new BasicDBObject("$first", "$locationName"))
					.append("latitude", new BasicDBObject("$first", "$latitude"))
					.append("longitude", new BasicDBObject("$first", "$longitude"))
					.append("experience", new BasicDBObject("$first", "$experience"))
					.append("colorCode", new BasicDBObject("$first", "$colorCode"))
					.append("images", new BasicDBObject("$first", "$images"))
					.append("country", new BasicDBObject("$first", "$country"))
					.append("state", new BasicDBObject("$first", "$state"))
					.append("city", new BasicDBObject("$first", "$city"))
					.append("postalCode", new BasicDBObject("$first", "$postalCode"))
					.append("streetAddress", new BasicDBObject("$first", "$streetAddress"))
					.append("locality", new BasicDBObject("$first", "$locality")).append("landmarkDetails", new BasicDBObject("$first", "$landmarkDetails"))
					.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))));
					
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.DOCTOR.name()).and("discarded").is(false)),
					Aggregation.lookup("docter_cl", "resourceId", "userId", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("user_cl", "resourceId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					projectList, groupOperation,
					Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
					Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.DOCTOR.name()).and("discarded").is(false)),
						Aggregation.lookup("docter_cl", "resourceId", "userId", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("user_cl", "resourceId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
						projectList, groupOperation,
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			
			response = mongoTemplate.aggregate(aggregation, UserResourceFavouriteCollection.class, DoctorInfo.class).getMappedResults();
			
			if(response != null) {
				for(DoctorInfo doctorInfo : response) {
					DoctorClinicProfileCollection clinicProfileCollection = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("doctorId").is(new ObjectId(doctorInfo.getId())).and("locationId").is(new ObjectId(doctorInfo.getLocationId()))),
									Aggregation.project(Fields.fields("consultationFee","facility"))),                              
							DoctorClinicProfileCollection.class, DoctorClinicProfileCollection.class).getUniqueMappedResult();
					
					doctorInfo.setConsultationFee(clinicProfileCollection.getConsultationFee());
					doctorInfo.setFacility(clinicProfileCollection.getFacility());
					String address = (!DPDoctorUtils.anyStringEmpty(doctorInfo.getStreetAddress())
							? doctorInfo.getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorInfo.getLandmarkDetails())
									? doctorInfo.getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorInfo.getLocality())
									? doctorInfo.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorInfo.getCity())
									? doctorInfo.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorInfo.getState())
									? doctorInfo.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorInfo.getCountry())
									? doctorInfo.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(doctorInfo.getPostalCode())
									? doctorInfo.getPostalCode() : "");

					if (!DPDoctorUtils.anyStringEmpty(address) && address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					doctorInfo.setAddress(address);

					doctorInfo.setImageUrl(getFinalImageURL(doctorInfo.getImageUrl()));
					doctorInfo.setThumbnailUrl(getFinalImageURL(doctorInfo.getThumbnailUrl()));
					doctorInfo.setCoverImageUrl(getFinalImageURL(doctorInfo.getCoverImageUrl()));
					doctorInfo.setCoverThumbnailImageUrl(getFinalImageURL(doctorInfo.getCoverThumbnailImageUrl()));
					
					if (doctorInfo.getImages() != null) {
						for (ClinicImage clinicImage : doctorInfo.getImages()) {
							clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
							}
					}
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
	public List<Locale> getFavouritePharmacies(int page, int size, String userId) {
		List<Locale> response = null;
		try {
			Aggregation aggregation = null;
			if(size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.PHARMACY.name()).and("discarded").is(false)),
					Aggregation.lookup("locale_cl", "resourceId", "_id", "pharmacy"), Aggregation.unwind("pharmacy"),
					Aggregation.sort(new Sort(Direction.DESC, "updatedTime")),
					Aggregation.skip(page * size), Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(new ObjectId(userId)).and("resourceType").is(Resource.PHARMACY.name()).and("discarded").is(false)),
						Aggregation.lookup("locale_cl", "resourceId", "_id", "pharmacy"), Aggregation.unwind("pharmacy"),
						Aggregation.sort(new Sort(Direction.DESC, "updatedTime")));
			}
			
			List<FavouriteLookupResponse> favouriteLookupResponses = mongoTemplate.aggregate(aggregation, UserResourceFavouriteCollection.class, FavouriteLookupResponse.class).getMappedResults();
			if(favouriteLookupResponses != null) {
				response = new ArrayList<Locale>();
				for(FavouriteLookupResponse favouriteLookupResponse : favouriteLookupResponses) {
					Locale locale = new Locale();
					BeanUtil.map(favouriteLookupResponse.getPharmacy(), locale);
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
	public List<LabResponse> getFavouriteLabs(int page, int size, String userId) {
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

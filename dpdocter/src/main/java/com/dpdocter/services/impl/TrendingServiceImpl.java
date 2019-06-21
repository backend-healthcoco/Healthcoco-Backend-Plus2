package com.dpdocter.services.impl;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Offer;
import com.dpdocter.collections.OfferCollection;
import com.dpdocter.collections.TrendingCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.OfferRepository;
import com.dpdocter.repository.TrendingRepository;
import com.dpdocter.response.OfferResponse;
import com.dpdocter.response.TrendingResponse;
import com.dpdocter.services.BlogService;
import com.dpdocter.services.NutritionService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.TrendingService;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class TrendingServiceImpl implements TrendingService {

	private static Logger logger = Logger.getLogger(TrendingServiceImpl.class.getName());

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private TrendingRepository trendingRepository;

	@Autowired
	private BlogService blogService;

	@Autowired
	private NutritionService nutritionService;

	@Autowired
	private PrescriptionServices prescriptionService;

	@Autowired
	private PatientTreatmentServices patientTreatmentService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public TrendingResponse getTrending(String id, String userId) {
		TrendingResponse response = null;
		try {
			response = new TrendingResponse();
			TrendingCollection trendingCollection = trendingRepository.findOne(new ObjectId(id));
			BeanUtil.map(trendingCollection, response);
			if (response != null) {
				if (!DPDoctorUtils.anyStringEmpty(trendingCollection.getBlogId())) {
					response.setBlog(blogService.getBlog(response.getBlogId(), null, null));
				} else if (!DPDoctorUtils.anyStringEmpty(trendingCollection.getOfferId())) {
					OfferCollection offerCollection = offerRepository.findOne(trendingCollection.getOfferId());
					Offer offer = new Offer();
					BeanUtil.map(offerCollection, offer);
					response.setOffer(offer);
					if (offer != null) {
						if (offer.getTitleImage() != null) {
							if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
								offer.getTitleImage()
										.setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

							if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
								offer.getTitleImage()
										.setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

						}
					}

				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Trending " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Trending " + e.getMessage());

		}
		return response;
	}

	@Override
	public OfferResponse getOffer(String id) {
		OfferResponse response = null;
		try {

			OfferCollection offerCollection = offerRepository.findOne(new ObjectId(id));
			response = new OfferResponse();

			BeanUtil.map(offerCollection, response);
			if (offerCollection.getTreatmentServiceIds() != null && !offerCollection.getTreatmentServiceIds().isEmpty())
				response.setTreatmentServices(
						patientTreatmentService.getTreatmentServices(offerCollection.getTreatmentServiceIds()));
			if (offerCollection.getNutritionPlanIds() != null && !offerCollection.getNutritionPlanIds().isEmpty())
				response.setNutritionPlans(nutritionService.getNutritionPlans(offerCollection.getNutritionPlanIds()));
			if (offerCollection.getSubscriptionPlanIds() != null && !offerCollection.getSubscriptionPlanIds().isEmpty())
				response.setSubscriptionPlans(
						nutritionService.getSubscritionPlans(offerCollection.getSubscriptionPlanIds()));
			if (offerCollection.getDrugIds() != null && !offerCollection.getDrugIds().isEmpty())
				response.setDrugs(prescriptionService.getDrugs(offerCollection.getDrugIds()));
			if (response != null) {
				if (response.getTitleImage() != null) {
					if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage().getImageUrl()))
						response.getTitleImage().setImageUrl(getFinalImageURL(response.getTitleImage().getImageUrl()));

					if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage().getThumbnailUrl()))
						response.getTitleImage()
								.setThumbnailUrl(getFinalImageURL(response.getTitleImage().getThumbnailUrl()));

				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting nutrients " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting nutrients " + e.getMessage());

		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

}

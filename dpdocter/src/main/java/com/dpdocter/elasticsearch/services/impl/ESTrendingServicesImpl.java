package com.dpdocter.elasticsearch.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.search.join.ScoreMode;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Blog;
import com.dpdocter.beans.Offer;
import com.dpdocter.collections.BlogCollection;
import com.dpdocter.elasticsearch.document.ESOfferDocument;
import com.dpdocter.elasticsearch.document.ESTrendingDocument;
import com.dpdocter.elasticsearch.repository.ESOfferRepository;
import com.dpdocter.elasticsearch.repository.ESTrendingRepository;
import com.dpdocter.elasticsearch.services.ESTrendingServices;
import com.dpdocter.enums.Resource;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlogRepository;
import com.dpdocter.response.TrendingResponse;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESTrendingServicesImpl implements ESTrendingServices {

	private static Logger logger = Logger.getLogger(ESTrendingServicesImpl.class.getName());

	@Autowired
	private ESOfferRepository esOfferRepository;

	@Autowired
	private ESTrendingRepository esTrendingRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public boolean addOffer(ESOfferDocument request) {
		boolean response = false;
		try {
			esOfferRepository.save(request);
			response = true;
			transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.OFFER, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Offer in ES");
		}
		return response;
	}

	@Override
	public boolean addTrending(ESTrendingDocument request) {
		boolean response = false;
		try {
			esTrendingRepository.save(request);
			response = true;
			transactionalManagementService.addResource(new ObjectId(request.getId()), Resource.TRENDING, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Offer in ES");
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Offer> searchOffer(int size, int page, Boolean discarded, String searchTerm, String productId,
			String offerType, String productType) {
		List<Offer> response = null;
		try {

			Offer offer = null;
			SearchQuery searchQuery = null;
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				boolQueryBuilder
						.must(QueryBuilders.matchPhrasePrefixQuery("title", searchTerm.replaceAll("[^a-zA-Z0-9]", "")));
			}

			if (!DPDoctorUtils.anyStringEmpty(productId)) {
				boolQueryBuilder.must(boolQuery().should(QueryBuilders.termsQuery("drugIds", productId))
						.should(QueryBuilders.termsQuery("treatmentServiceIds", productId))
						.should(QueryBuilders.termsQuery("nutritionPlanIds", productId))
						.should(QueryBuilders.termsQuery("subscriptionPlanIds", productId))).minimumShouldMatch(1);
			}

			if (!DPDoctorUtils.anyStringEmpty(offerType)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("type", offerType));
			}

			if (!DPDoctorUtils.anyStringEmpty(productType)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("productType", productType));
			}

			if (size == 0)
				size = 15;

			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(new PageRequest(page, size, Direction.ASC, "updatedTime")).build();
			List<ESOfferDocument> documents = elasticsearchTemplate.queryForList(searchQuery, ESOfferDocument.class);
			if (documents != null && !documents.isEmpty()) {
				response = new ArrayList<Offer>();
				for (ESOfferDocument document : documents) {
					offer = new Offer();
					BeanUtil.map(document, offer);
					response.add(offer);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While search Offer in ES");
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<TrendingResponse> searchTrendings(int size, int page, Boolean discarded, String searchTerm,
			String trendingType, String resourceType) {
		List<TrendingResponse> response = null;
		try {
			TrendingResponse trending = null;
			SearchQuery searchQuery = null;
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchPhrasePrefixQuery("discarded", discarded));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				boolQueryBuilder
						.must(QueryBuilders.matchPhrasePrefixQuery("title", searchTerm.replaceAll("[^a-zA-Z0-9]", "")));
			}

			if (!DPDoctorUtils.anyStringEmpty(trendingType)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("type", trendingType));
			}
			if (!DPDoctorUtils.anyStringEmpty(resourceType)) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("resourceType", resourceType));
			}

			if (size == 0)
				size = 15;

			searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
					.withPageable(new PageRequest(page, size, Direction.ASC, "rank")).build();
			List<ESTrendingDocument> documents = elasticsearchTemplate.queryForList(searchQuery,
					ESTrendingDocument.class);
			if (documents != null && !documents.isEmpty()) {
				response = new ArrayList<TrendingResponse>();
				for (ESTrendingDocument document : documents) {
					trending = new TrendingResponse();

					BeanUtil.map(document, trending);
					if (!DPDoctorUtils.anyStringEmpty(trending.getOfferId())) {
						Offer offer = new Offer();
						ESOfferDocument offerDocument = esOfferRepository.findById(trending.getOfferId()).orElse(null);
						BeanUtil.map(offerDocument, offer);
						if (offer != null) {
							if (offer.getTitleImage() != null) {
								if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
									offer.getTitleImage()
											.setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

								if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
									offer.getTitleImage()
											.setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

							}
							trending.setOffer(offer);
						}
					} else if (!DPDoctorUtils.anyStringEmpty(trending.getBlogId())) {
						Blog blog = new Blog();
						BlogCollection blogCollection = blogRepository.findById(new ObjectId(trending.getBlogId())).orElse(null);
						BeanUtil.map(blogCollection, blog);

						if (blog != null && !DPDoctorUtils.anyStringEmpty(blog.getTitleImage())) {
							blog.setTitleImage(imagePath + blog.getTitleImage());
						}
						trending.setBlog(blog);
					}
					response.add(trending);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While search Trending in ES");
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@SuppressWarnings("deprecation")
	private void createTimeFilter(BoolQueryBuilder boolQueryBuilder, int maxTime, int minTime, List<String> days) {
		if (days != null && !days.isEmpty()) {
			for (int i = 0; i < days.size(); i++) {
				days.set(i, days.get(i).toLowerCase());
			}

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder.must(QueryBuilders.nestedQuery("time",
					boolQuery().must(nestedQuery("time.workingHours", 
							boolQuery().should(QueryBuilders.rangeQuery("time.workingHours.toTime").gt(minTime).lt(maxTime))
							.should(QueryBuilders.rangeQuery("time.workingHours.fromTime").gt(minTime).lt(maxTime))
							.should(boolQuery().must(QueryBuilders.rangeQuery("time.workingHours.toTime").gt(minTime).lt(1439))
									.must(QueryBuilders.rangeQuery("time.workingHours.fromTime").gt(0).lt(maxTime))
									).minimumShouldMatch(1), ScoreMode.None))
					.must(QueryBuilders.termsQuery("time.workingDay", days)), ScoreMode.None));
			
		} else {

			if (maxTime == 0) {
				maxTime = 1439;
			}
			boolQueryBuilder.must(QueryBuilders.nestedQuery("time",
					boolQuery().must(nestedQuery("time.workingHours", 
							boolQuery().should(QueryBuilders.rangeQuery("time.workingHours.toTime").gt(minTime).lt(maxTime))
							.should(QueryBuilders.rangeQuery("time.workingHours.fromTime").gt(minTime).lt(maxTime))
							.should(boolQuery().must(QueryBuilders.rangeQuery("time.workingHours.toTime").gt(minTime).lt(1439))
									.must(QueryBuilders.rangeQuery("time.workingHours.fromTime").gt(0).lt(maxTime))
									).minimumShouldMatch(1), ScoreMode.None)), ScoreMode.None));
		}
	}

}

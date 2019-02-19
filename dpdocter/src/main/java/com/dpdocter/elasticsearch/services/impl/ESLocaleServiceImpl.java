package com.dpdocter.elasticsearch.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.repository.ESUserLocaleRepository;
import com.dpdocter.elasticsearch.services.ESLocaleService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.services.TransactionalManagementService;

@Service
public class ESLocaleServiceImpl implements ESLocaleService {
	private static Logger logger = Logger.getLogger(ESLocaleServiceImpl.class.getName());

	@Autowired
	private ESUserLocaleRepository esUserLocaleRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Override
	public boolean addLocale(ESUserLocaleDocument request) {
		boolean response = false;
		try {
			if (request.getAddress() != null && request.getAddress().getLatitude() != null
					&& request.getAddress().getLongitude() != null)
				request.setGeoPoint(
						new GeoPoint(request.getAddress().getLatitude(), request.getAddress().getLongitude()));
			esUserLocaleRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PHARMACY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving User Locale");
		}
		return response;
	}

	@Override
	public List<ESUserLocaleDocument> getLocale(UserSearchRequest userSearchRequest, Integer distance) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(userSearchRequest.getLatitude())
						.lon(userSearchRequest.getLongitude()).distance(distance + "km"))
				.must(QueryBuilders.matchPhrasePrefixQuery("isLocaleListed", true))
				.must(QueryBuilders.matchPhrasePrefixQuery("isOpen", true));
		List<ESUserLocaleDocument> esUserLocaleDocuments = elasticsearchTemplate.queryForList(
				new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC)).build(),
				ESUserLocaleDocument.class);
		return esUserLocaleDocuments;
	}

	@Override
	public Boolean updateStatus(String localeId, Boolean isOpen) {
		Boolean response = false;
		ESUserLocaleDocument esUserLocale = esUserLocaleRepository.findOne(localeId);
		if (esUserLocale == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		} else {
			response = true;
			esUserLocale.setIsAcceptRequest(isOpen);
			esUserLocale = esUserLocaleRepository.save(esUserLocale);
		}
		return response;
	}
	
	@Override
	public Boolean updateLocale(String localeId, LocaleCollection localeCollection) {
		Boolean response = false;
		ESUserLocaleDocument esUserLocale = esUserLocaleRepository.findOne(localeId);
		if (esUserLocale == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		} else {
			response = true;

			BeanUtil.map(localeCollection, esUserLocale);
			esUserLocale = esUserLocaleRepository.save(esUserLocale);
		}
		return response;
	}
	
	
}

package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;
import com.dpdocter.elasticsearch.repository.ESExpenseTypeRepository;
import com.dpdocter.elasticsearch.services.ESExpenseTypeService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class ESExpenseTypeServiceImpl implements ESExpenseTypeService {

	private static Logger logger = LogManager.getLogger(ESTreatmentServiceImpl.class.getName());

	@Autowired
	private ESExpenseTypeRepository esExpenseRepository;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public void addEditExpenseType(ESExpenseTypeDocument esDocument) {
		try {
			esExpenseRepository.save(esDocument);
			transactionalManagementService.addResource(new ObjectId(esDocument.getId()), Resource.EXPENSE_TYPE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving  Expense in ES");
		}
	}

	@Override
	public List<?> search(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm) {
		List<?> response = new ArrayList<Object>();
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalExpenseTypes(page, size, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomExpenseType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalExpenseTypes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getGlobalExpenseTypes(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm) {
		List<ESExpenseTypeDocument> response = null;
		try {

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.EXPENSE_TYPE, page, 0, updatedTime,
					discarded, null, searchTerm, null, null, null, "name");
			response = elasticsearchTemplate.queryForList(searchQuery, ESExpenseTypeDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Expenses");
		}
		return response;
	}

	private List<?> getCustomExpenseType(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm) {
		List<ESExpenseTypeDocument> response = null;
		try {
			SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
					updatedTime, discarded, null, searchTerm, null, null, "name");
			response = elasticsearchTemplate.queryForList(searchQuery, ESExpenseTypeDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Expenses");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getCustomGlobalExpenseTypes(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<ESExpenseTypeDocument> response = null;
		try {

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.EXPENSE_TYPE, page, 0, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, null, null, null, "name");
			response = elasticsearchTemplate.queryForList(searchQuery, ESExpenseTypeDocument.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Expenses");
		}
		return response;
	}
}

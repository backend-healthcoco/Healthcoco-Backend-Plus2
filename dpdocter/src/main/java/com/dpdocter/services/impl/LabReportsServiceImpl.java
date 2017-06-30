package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.LabReports;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LabReportsRepository;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LabReportsService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class LabReportsServiceImpl implements LabReportsService{


	public static final Logger LOGGER = Logger.getLogger(LabReportsServiceImpl.class);
	
	@Autowired
	LabReportsRepository labReportsRepository;
	
	@Autowired
	FileManager fileManager;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	@Transactional
	public LabReports addLabReports(FormDataBodyPart file, LabReportsAddRequest request) {
		LabReports response = null;
		LabReportsCollection labReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "lab-reports" + File.separator + request.getRequestId();
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
			}
			labReportsCollection = labReportsRepository.getByRequestIdandSAmpleId(new ObjectId(request.getRequestId()),
					new ObjectId(request.getLabTestSampleId()));
			if (labReportsCollection == null) {
				labReportsCollection = new LabReportsCollection();
			}
			if (labReportsCollection.getLabReports() == null) {
				List<ImageURLResponse> responses = new ArrayList<>();
				labReportsCollection.setLabReports(responses);
			}
			BeanUtil.map(request, labReportsCollection);
			labReportsCollection.getLabReports().add(imageURLResponse);
			labReportsCollection = labReportsRepository.save(labReportsCollection);
			response = new LabReports();
			BeanUtil.map(labReportsCollection, response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<LabReports> getLabReports(String labTestSampleId,
			String searchTerm, int page, int size) {
		List<LabReports> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm));
			}

			criteria.and("labTestSampleId").is(new ObjectId(labTestSampleId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<LabReports> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabReportsCollection.class, LabReports.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e + " Error Getting lab Reports");
			throw new BusinessException(ServiceError.Unknown, "Error Getting lab reports");
		}
		return response;
	}
	
}

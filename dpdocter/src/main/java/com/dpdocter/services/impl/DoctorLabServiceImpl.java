package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorLabReport;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.collections.UserAllowanceDetailsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DoctorLabReportRepository;
import com.dpdocter.request.DoctorLabReportsAddRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.services.DoctorLabService;
import com.dpdocter.services.FileManager;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class DoctorLabServiceImpl implements DoctorLabService {

	@Autowired
	private DoctorLabReportRepository doctorLabReportRepository;

	@Autowired
	private FileManager fileManager;

	@Override
	@Transactional
	public DoctorLabReport addDoctorLabReport(DoctorLabReport request) {

		return null;
	}

	public RecordsFile uploadDoctorLabReport(FileDetails fileDetails, MyFiileRequest request) {
		RecordsFile recordsFile = null;
		try {
			UserAllowanceDetailsCollection userAllowanceDetailsCollection = null;
			Date createdTime = new Date();

			if (fileDetails != null) {
				String path = "doctorLabReport" + File.separator + request.getPatientId();

				String fileName = fileDetails.getFileName().replaceFirst("." + fileDetails.getFileExtension(), "");
				String recordPath = path + File.separator + fileName + createdTime.getTime() + "."
						+ fileDetails.getFileExtension();
				String recordfileLabel = fileName;
				Double fileSizeInMB = 0.0;

				//fileSizeInMB = fileManager.saveRecord(file, recordPath, fileSizeInMB, false);

				recordsFile = new RecordsFile();
				recordsFile.setFileId("file" + DPDoctorUtils.generateRandomId());
				recordsFile.setFileSizeInMB(fileSizeInMB);
				recordsFile.setRecordsUrl(recordPath);
				recordsFile.setThumbnailUrl(fileManager.saveThumbnailAndReturnThumbNailUrl(fileDetails, recordPath));
				recordsFile.setRecordsFileLabel(recordfileLabel);
				recordsFile.setRecordsPath(path);
				recordsFile.setRecordsType(request.getRecordsType());

			}

		} catch (Exception e) {
			//logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while uploading Doctor Lab Report");

		}
		return recordsFile;

	}

}

package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DentalLabReports;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LabTestSampleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.DentalLabReportService;

import common.util.web.DPDoctorUtils;

@Service
public class DentalLabReportServiceImpl implements DentalLabReportService{

	
	
	
	/*@Override
	@Transactional
	public DentalLabReports addLabReportBase64(FileDetails fileDetails, LabReportsAddRequest request) {
		LabReports response = null;
		LabReportsCollection labReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();

			if (fileDetails != null) {
				// String path = "lab-reports";
				// String recordLabel = fileDetails.getFileName();
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());

				String path = "lab-reports" + File.separator + request.getPatientName();

				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, path, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}
			labReportsCollection = labReportsRepository
					.getByRequestIdandSAmpleId(new ObjectId(request.getLabTestSampleId()));
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

			LabTestPickupCollection labTestPickupCollection = labTestPickupRepository
					.getByLabTestSampleId(new ObjectId(request.getLabTestSampleId()));
			if (labTestPickupCollection != null) {
				labTestPickupCollection.setStatus("REPORTS UPLOADED");
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);
			}

			LabTestSampleCollection labTestSampleCollection = labTestSampleRepository
					.findOne(new ObjectId(request.getLabTestSampleId()));
			if (labTestSampleCollection != null) {
				if (labTestSampleCollection.getIsCompleted() == true
						&& !DPDoctorUtils.anyStringEmpty(labTestSampleCollection.getParentLabLocationId())
						&& DPDoctorUtils.anyStringEmpty(labReportsCollection.getSerialNumber())) {
					String serialNumber = reportSerialNumberGenerator(
							labTestSampleCollection.getParentLabLocationId().toString());
					labReportsCollection.setSerialNumber(serialNumber);
				}
				labTestSampleCollection.setStatus("REPORTS UPLOADED");
				labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
				LocationCollection daughterlocationCollection = locationRepository
						.findOne(labReportsCollection.getLocationId());
				LocationCollection parentLocationCollection = locationRepository
						.findOne(labReportsCollection.getUploadedByLocationId());
				String message = labReportUploadMessage;
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("LAB REPORT UPLOAD");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(daughterlocationCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(daughterlocationCollection.getLocationName());
				message = message.replace("{patientName}", request.getPatientName());
				message = message.replace("{specimenName}", labTestSampleCollection.getSampleType());
				message = message.replace("{parentLab}", parentLocationCollection.getLocationName());
				sms.setSmsText(message);
				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(daughterlocationCollection.getClinicNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}*/
	
}

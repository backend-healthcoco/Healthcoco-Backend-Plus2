package com.dpdocter.services.impl;

import org.springframework.stereotype.Service;

import com.dpdocter.services.DentalLabReportService;

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
					.findById(new ObjectId(request.getLabTestSampleId()));
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
						.findById(labReportsCollection.getLocationId());
				LocationCollection parentLocationCollection = locationRepository
						.findById(labReportsCollection.getUploadedByLocationId());
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

package com.dpdocter.services;

import org.bson.types.ObjectId;

import com.dpdocter.beans.MailAttachment;
import com.dpdocter.request.ExportRequest;

public interface DownloadDataService {

	Boolean downlaodData(ExportRequest request);

	void sendDataToDoctor();

	Boolean downloadClinicalItems(String doctorId, String locationId, String hospitalId);

	MailAttachment generatePatientData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	MailAttachment downloadPrescriptionData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	MailAttachment downloadAppointmentData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	MailAttachment downloadTreatmentData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	MailAttachment downloadClinicalNotesData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	MailAttachment downloadInvoicesData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	MailAttachment downloadPaymentsData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId);

	Boolean update(String doctorId, String locationId, String hospitalId);

	Boolean downloadfiles(String doctorId, String locationId, String hospitalId, int page, int size);

}

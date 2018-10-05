package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ConsentForm;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface CertificatesServices {

	Boolean addCertificateTemplates(CertificateTemplate request);

	List<?> getCertificateTemplates(long page, int size, String doctorId, String locationId, Boolean discarded, List<String> specialities, String type);

	Boolean discardCertificateTemplates(String templateId, Boolean discarded);

	ConsentForm addPatientCertificate(ConsentForm request);

	CertificateTemplate getCertificateTemplateById(String templateId);

	ConsentForm getPatientCertificateById(String certificateId);

	List<ConsentForm> getPatientCertificates(long page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, boolean discarded, String updatedTime, String type);

	ConsentForm deletePatientCertificate(String certificateId, Boolean discarded);

	String downloadPatientCertificate(String certificateId);

	String saveCertificateSignImage(FormDataBodyPart file, String certificateIdStr);

}

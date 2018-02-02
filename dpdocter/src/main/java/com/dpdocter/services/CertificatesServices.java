package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CertificateTemplate;
import com.dpdocter.beans.ConsentForm;

public interface CertificatesServices {

	Boolean addCertificateTemplates(CertificateTemplate request);

	List<?> getCertificateTemplates(int page, int size, String doctorId, String locationId, Boolean discarded, List<String> specialities);

	Boolean discardCertificateTemplates(String templateId, Boolean discarded);

	ConsentForm addPatientCertificate(ConsentForm request);

	CertificateTemplate getCertificateTemplateById(String templateId);

	ConsentForm getPatientCertificateById(String certificateId);

	List<ConsentForm> getPatientCertificates(int page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, boolean discarded, String updatedTime);

	ConsentForm deletePatientCertificate(String certificateId, Boolean discarded);

	String downloadPatientCertificate(String certificateId);

}

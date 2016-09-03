package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;

public interface ESDoctorDrugRepository extends ElasticsearchRepository<ESDoctorDrugDocument, String> {

	@Query("{\"bool\": {\"must\": [{\"match\": {\"drugId\": \"?0\"}}, {\"match\": {\"doctorId\": \"?1\"}}, {\"match\": {\"locationId\": \"?2\"}}, {\"match\": {\"hospitalId\": \"?3\"}}]}}")
	ESDoctorDrugDocument findByDrugIdDoctorIdLocaationIdHospitalId(String drugId, String doctorId, String locationId, String hospitalId);
}

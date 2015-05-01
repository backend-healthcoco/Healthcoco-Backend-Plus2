package com.dpdocter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.collections.HistoryCollection;

public interface HistoryRepository extends MongoRepository<HistoryCollection, String>{
	@Query("{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}")
	HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientId(String doctorId,String locationId,String hospitalId,String patientId);
	
	List<HistoryCollection> findByPatientId(String patientId);
	
	@Query(value = "{'patientId':?0}",fields="{ 'prescriptions' : 0, 'clinicalNotes' : 0}")
	List<HistoryCollection> findByPatientIdFilterByReports(String patientId);
	@Query(value="{'patientId':?0}",fields="{ 'prescriptions' : 0, 'reports' : 0}")
	List<HistoryCollection> findByPatientIdFilterByClinicalNotes(String patientId);
	@Query(value="{'patientId':?0}",fields="{ 'clinicalNotes' : 0, 'reports' : 0}")
	List<HistoryCollection> findByPatientIdFilterByPrescriptions(String patientId);
	
	@Query(value = "{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}",fields="{ 'prescriptions' : 0, 'clinicalNotes' : 0}")
	HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientIdFilterByReports(String doctorId,String locationId,String hospitalId,String patientId);
	
	
	@Query(value="{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}",fields="{ 'prescriptions' : 0, 'reports' : 0}")
	HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientIdFilterByClinicalNotes(String doctorId,String locationId,String hospitalId,String patientId);

	@Query(value="{'doctorId':?0,'locationId':?1,'hospitalId':?2,'patientId':?3}",fields="{ 'clinicalNotes' : 0, 'reports' : 0}")
	HistoryCollection findByDoctorIdLocationIdHospitalIdAndPatientIdFilterByPrescriptions(String doctorId,String locationId,String hospitalId,String patientId);

}

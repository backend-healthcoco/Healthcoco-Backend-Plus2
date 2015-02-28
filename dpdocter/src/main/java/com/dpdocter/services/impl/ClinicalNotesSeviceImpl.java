package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.PatientClinicalNotesCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.PatientClinicalNotesRepository;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.services.ClinicalNotesService;

@Service
public class ClinicalNotesSeviceImpl implements ClinicalNotesService {

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private PatientClinicalNotesRepository patientClinicalNotesRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public ClinicalNotes addNotes(ClinicalNotesAddRequest request) {
		ClinicalNotes clinicalNotes = null;
		try {
			// save clinical notes.
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			BeanUtil.map(request, clinicalNotesCollection);
			clinicalNotesCollection = clinicalNotesRepository
					.save(clinicalNotesCollection);
			if (clinicalNotesCollection != null) {
				// map the clinical notes with patient
				PatientClinicalNotesCollection patientClinicalNotesCollection = new PatientClinicalNotesCollection();
				patientClinicalNotesCollection
						.setClinicalNotesId(clinicalNotesCollection.getId());
				patientClinicalNotesCollection.setPatientId(request
						.getPatientId());
				patientClinicalNotesRepository
						.save(patientClinicalNotesCollection);
				clinicalNotes = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNotes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return clinicalNotes;
	}

	@Override
	public ClinicalNotes getNotesById(String id) {
		ClinicalNotes clinicalNotes = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository
					.findOne(id);
			if (clinicalNotesCollection != null) {
				clinicalNotes = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNotes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
		ClinicalNotes clinicalNotes = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			BeanUtil.map(request, clinicalNotesCollection);
			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);
			clinicalNotes = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);
			return clinicalNotes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public void deleteNote(String id) {
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = 
					patientClinicalNotesRepository.findByClinicalNotesId(id);
			if(patientClinicalNotesCollections != null){
				patientClinicalNotesRepository.delete(patientClinicalNotesCollections);
			}
			clinicalNotesRepository.delete(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public List<ClinicalNotes> getPatientsClinicalNotesWithVarifiedOTP(
			String patientId) {
		List<ClinicalNotes> clinicalNotes = null;
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository
					.findByPatientId(patientId);
			@SuppressWarnings("unchecked")
			Collection<String> clinicalNotesId =  CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer("clinicalNotesId"));
			
			Query queryForGettingAllClinicalNotesFromPatient = new Query();
			queryForGettingAllClinicalNotesFromPatient.addCriteria(Criteria.where("id").in(clinicalNotesId));
			List<ClinicalNotesCollection> clinicalNotesCollections = mongoTemplate.find(queryForGettingAllClinicalNotesFromPatient, ClinicalNotesCollection.class);
			clinicalNotes = new ArrayList<ClinicalNotes>();
			BeanUtil.map(clinicalNotesCollections, clinicalNotes);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public List<ClinicalNotes> getPatientsClinicalNotesWithoutVarifiedOTP(
			String patientId,String doctorId) {
		List<ClinicalNotes> clinicalNotes = null;
		try {
			List<PatientClinicalNotesCollection> patientClinicalNotesCollections = patientClinicalNotesRepository
					.findByPatientId(patientId);
			@SuppressWarnings("unchecked")
			Collection<String> clinicalNotesId =  CollectionUtils.collect(patientClinicalNotesCollections, new BeanToPropertyValueTransformer("clinicalNotesId"));
			
			Query queryForGettingAllClinicalNotesFromPatient = new Query();
			queryForGettingAllClinicalNotesFromPatient.addCriteria(Criteria.where("id").in(clinicalNotesId));
			queryForGettingAllClinicalNotesFromPatient.addCriteria(Criteria.where("doctorId").is(doctorId));
			List<ClinicalNotesCollection> clinicalNotesCollections = mongoTemplate.find(queryForGettingAllClinicalNotesFromPatient, ClinicalNotesCollection.class);
			clinicalNotes = new ArrayList<ClinicalNotes>();
			BeanUtil.map(clinicalNotesCollections, clinicalNotes);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}
	
	

}

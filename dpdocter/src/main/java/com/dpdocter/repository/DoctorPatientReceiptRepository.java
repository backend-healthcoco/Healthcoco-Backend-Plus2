package com.dpdocter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dpdocter.collections.DoctorPatientReceiptCollection;

public interface DoctorPatientReceiptRepository extends MongoRepository<DoctorPatientReceiptCollection, ObjectId>,
		PagingAndSortingRepository<DoctorPatientReceiptCollection, ObjectId> {

	List<DoctorPatientReceiptCollection> findByReceiptTypeAndDoctorIdAndLocationIdAndHospitalIdAndPatientIdAndRemainingAdvanceAmountAndDiscarded(String name, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId, ObjectId patientId, Double remainingAdvanceAmount, Boolean discarded, Sort sort);

	@Query(value = "{'patientId' : ?0, 'doctorId' : ?1, 'locationId' : ?2, 'hospitalId' : ?3,'discarded' : false, 'isPatientDiscarded':{'$ne' : true}}", count = true)
	Integer countByPatientIdDoctorLocationHospital(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId);

	@Query(value = "{'patientId' : ?0,'discarded' : false, 'isPatientDiscarded':{'$ne' : true}}", count = true)
	Integer countByPatientId(ObjectId patientId);

	List<DoctorPatientReceiptCollection> findByAdvanceReceiptIdWithAmountsReceiptId(ObjectId advanceReceiptId);

	List<DoctorPatientReceiptCollection> findByInvoiceIdAndDiscarded(ObjectId invoiceId, boolean discarded);

	DoctorPatientReceiptCollection findByUniqueReceiptIdAndDoctorIdAndLocationIdAndHospitalId(String uniqueReceiptId, ObjectId doctorObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId);
	
	DoctorPatientReceiptCollection findByUniqueReceiptIdAndLocationIdAndHospitalId(String uniqueReceiptId, ObjectId locationObjectId, ObjectId hospitalObjectId);
	
	DoctorPatientReceiptCollection findByUniqueInvoiceIdAndLocationIdAndHospitalId(String uniqueInvoiceId, ObjectId locationObjectId, ObjectId hospitalObjectId);
}

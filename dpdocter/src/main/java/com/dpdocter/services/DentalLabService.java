package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorksAmount;
import com.dpdocter.beans.DentalWorksInvoice;
import com.dpdocter.beans.DentalWorksReceipt;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.enums.LabType;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.AddEditTaxRequest;
import com.dpdocter.request.DentalLabDoctorRegistrationRequest;
import com.dpdocter.request.DentalLabPickupChangeStatusRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.request.UpdateDentalStagingRequest;
import com.dpdocter.request.UpdateETARequest;
import com.dpdocter.response.CBDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;
import com.dpdocter.response.DentalWorksInvoiceResponse;
import com.dpdocter.response.DentalWorksReceiptResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.TaxResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface DentalLabService {

	DentalWork addEditCustomWork(AddEditCustomWorkRequest request);

	List<DentalWork> getCustomWorks(long page, int size, String searchTerm);

	DentalWork deleteCustomWork(String id, boolean discarded);

	Boolean changeLabType(String doctorId, String locationId, LabType labType);

	DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request);

	Boolean addEditRateCardDentalWorkAssociation(List<RateCardDentalWorkAssociation> request);

	/*
	 * List<RateCardDentalWorkAssociation> getRateCardWorks(long page, int size,
	 * String searchTerm, String rateCardId, Boolean discarded);
	 */
	// Boolean addEditRateCardDoctorAssociation(List<RateCardDoctorAssociation>
	// request);

	DentalLabDoctorAssociation addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request);

	/*
	 * List<DentalLabDoctorAssociationLookupResponse>
	 * getDentalLabDoctorAssociations(String locationId, long page, int size, String
	 * searchTerm);
	 */

	/*
	 * List<RateCardDoctorAssociation> getRateCards(long page, int size, String
	 * searchTerm, String doctorId, Boolean discarded);
	 */

	Boolean addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request);

	List<CBDoctorAssociationLookupResponse> getCBAssociatedDoctors(String doctorId, String dentalLabId,
			String collectionBoyId, int size, long page);

	Boolean addEditDentalLabDoctorAssociation(List<DentalLabDoctorAssociation> request);

	RateCardDoctorAssociation addEditRateCardDoctorAssociation(RateCardDoctorAssociation request);

	List<RateCardDoctorAssociation> getRateCards(long page, int size, String searchTerm, String doctorId,
			String dentalLabId, Boolean discarded);

	List<DentalLabDoctorAssociationLookupResponse> getDentalLabDoctorAssociations(String locationId, String doctorId,
			long page, int size, String searchTerm);

	/*
	 * List<DentalLabPickupResponse> getRequests(String dentalLabId, String
	 * doctorId, Long from, Long to, String searchTerm, String status, Boolean
	 * isAcceptedAtLab, Boolean isCompleted, int size, long page);
	 */
	List<Location> getDentalLabDoctorAssociationsForDoctor(String doctorId, long page, int size, String searchTerm);

	List<RateCardDentalWorkAssociation> getRateCardWorks(long page, int size, String searchTerm, String dentalLabId,
			String doctorId, Boolean discarded);

	// Boolean changeStatus(String dentalLabPickupId, String status);

	// Boolean changeStatus(String dentalLabPickupId, String status, Boolean
	// isCollectedAtLab);

	/*
	 * List<DentalLabPickupResponse> getRequests(String dentalLabId, String
	 * doctorId, Long from, Long to, String searchTerm, String status, Boolean
	 * isAcceptedAtLab, Boolean isCompleted, Boolean isCollectedAtDoctor, int size,
	 * long page);
	 */

	/*
	 * Boolean changeStatus(String dentalLabPickupId, String status, Boolean
	 * isCollectedAtDoctor, Boolean isCompleted, Boolean isAcceptedAtLab);
	 */

	ImageURLResponse addDentalImage(FormDataBodyPart file);

	ImageURLResponse addDentalImageBase64(FileDetails fileDetails);

	Boolean updateDentalStageForDoctor(UpdateDentalStagingRequest request);

	Boolean updateDentalStageForLab(UpdateDentalStagingRequest request);

	Boolean updateETA(UpdateETARequest request);

	List<RateCardDentalWorkAssociation> getRateCardWorks(long page, int size, String searchTerm, String rateCardId,
			Boolean discarded);

	Boolean cancelRequest(String requestId, String reasonOfCancellation, String cancelledBy);

	Boolean discardRequest(String requestId, Boolean discarded);

	DentalLabPickupResponse getRequestById(String id);

	List<DentalLabPickupResponse> getRequests(String dentalLabId, String doctorId, Long from, Long to,
			String searchTerm, String status, Boolean isAcceptedAtLab, Boolean isCompleted, Boolean isCollectedAtDoctor,
			int size, long page, Long fromETA, Long toETA, Boolean isTrailsRequired);

	Boolean changeStatus(DentalLabPickupChangeStatusRequest request);

	String downloadDentalLabReportPrint(String id, Boolean isInspectionReport);

	public List<DentalLabPickupResponse> getRequestByIds(List<ObjectId> ids);

	public String downloadMultipleInspectionReportPrint(List<String> requestId);

	TaxResponse addEditTax(AddEditTaxRequest request);

	Boolean dentalLabDoctorRegistration(DentalLabDoctorRegistrationRequest request);

	DentalWorksInvoice addEditInvoice(DentalWorksInvoice request);

	List<DentalWorksInvoiceResponse> getInvoices(String doctorId, String locationId, String hospitalId,
			String dentalLabLocationId, String dentalLabHospitalId, Long from, Long to, String searchTerm, int size,
			long page);

	DentalWorksReceiptResponse getReceiptById(String id);

	DentalWorksInvoiceResponse getInvoiceById(String id);

	DentalWorksReceipt addEditReceipt(DentalWorksReceipt request);

	List<DentalWorksReceiptResponse> getReceipts(String doctorId, String locationId, String hospitalId,
			String dentalLabLocationId, String dentalLabHospitalId, Long from, Long to, String searchTerm, int size,
			long page);

	DentalWorksInvoice discardInvoice(String id, Boolean discarded);

	DentalWorksReceipt discardReceipt(String id, Boolean discarded);

	DentalWorksAmount getAmount(String doctorId, String locationId, String hospitalId, String dentalLabLocationId,
			String dentalLabHospitalId);

	public String downloadDentalWorkInvoice(String invoiceId);

	public String downloadDentalLabReceipt(String receiptId);

	/*
	 * DentalLabDoctorAssociation
	 * addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request);
	 * 
	 * List<DentalLabDoctorAssociationLookupResponse>
	 * getDentalLabDoctorAssociations(String locationId, long page, int size, String
	 * searchTerm);
	 */

}

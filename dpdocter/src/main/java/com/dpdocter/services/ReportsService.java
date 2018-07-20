package com.dpdocter.services;


import java.util.List;

import com.dpdocter.beans.BrokenAppointment;
import com.dpdocter.beans.ClinicalIndicator;
import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.EquipmentLogAMCAndServicingRegister;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.beans.RepairRecordsOrComplianceBook;
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsResponse;

public interface ReportsService {

	public IPDReports submitIPDReport(IPDReports ipdReports);

	public OPDReports submitOPDReport(OPDReports opdReports);

	public OTReports submitOTReport(OTReports otReports);

	public DeliveryReports submitDeliveryReport(DeliveryReports deliveryReports);

	IPDReportsResponse getIPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	OPDReportsResponse getOPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	OTReportsResponse getOTReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	DeliveryReportsResponse getDeliveryReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime);

	public Boolean addPrescriptionOPDReports();

	OPDReports getOPDReportByVisitId(String visitId);

	public ClinicalIndicator discardClinicalIndicators(String indicatorId, boolean discarded);

	public List<ClinicalIndicator> getClinicalIndicators(int size, int page, String doctorId, String locationId,
			String hospitalId, boolean discarded, String type);

	public ClinicalIndicator getClinicalIndicatorById(String indicatorId);

	public ClinicalIndicator addClinicalIndicator(ClinicalIndicator request);

	public EquipmentLogAMCAndServicingRegister discardEquipmentLogAMCAndServicingRegister(String registerId,
			boolean discarded);

	public List<EquipmentLogAMCAndServicingRegister> getEquipmentLogAMCAndServicingRegisters(int size, int page,
			String doctorId, String locationId, String hospitalId, boolean discarded);

	public EquipmentLogAMCAndServicingRegister addEquipmentLogAMCAndServicingRegister(
			EquipmentLogAMCAndServicingRegister request);

	public EquipmentLogAMCAndServicingRegister getEquipmentLogAMCAndServicingRegisterById(String registerid);

	public List<RepairRecordsOrComplianceBook> getRepairRecordsOrComplianceBooks(int size, int page, String doctorId,
			String locationId, String hospitalId, boolean discarded);

	public RepairRecordsOrComplianceBook discardrepairRecordsOrComplianceBook(String bookId, boolean discarded);

	public RepairRecordsOrComplianceBook getRepairRecordsOrComplianceBookById(String bookid);

	public RepairRecordsOrComplianceBook addRepairRecordsOrComplianceBook(RepairRecordsOrComplianceBook request);

	public BrokenAppointment discardBrokenAppointment(String appointmentId, boolean discarded);

	public List<BrokenAppointment> getBrokenAppointments(int size, int page, String doctorId, String locationId,
			String hospitalId, boolean discarded);

	public BrokenAppointment getBrokenAppointment(String appointmentId);

	public BrokenAppointment addBrokenAppointment(BrokenAppointment request);

	public String getOTReportsFile(String otId);

	public String getDeliveryReportsFile(String reportId);

	Boolean updateOTReports();
}

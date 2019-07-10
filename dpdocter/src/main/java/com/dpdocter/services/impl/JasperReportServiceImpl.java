
package com.dpdocter.services.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.PageSize;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.services.JasperReportService;
import com.jaspersoft.mongodb.connection.MongoDbConnection;

import common.util.web.DPDoctorUtils;
import net.sf.jasperreports.components.html.HtmlComponent;
import net.sf.jasperreports.components.list.DesignListContents;
import net.sf.jasperreports.components.list.StandardListComponent;
import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDatasetParameter;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignSubreportParameter;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
public class JasperReportServiceImpl implements JasperReportService {

	private static Logger logger = Logger.getLogger(JasperReportServiceImpl.class.getName());

	@Value(value = "${mongo.host.uri}")
	private String MONGO_HOST_URI;

	@Value(value = "${jasper.templates.resource}")
	private String JASPER_TEMPLATES_RESOURCE;

	@Value(value = "${jasper.templates.root.path}")
	private String JASPER_TEMPLATES_ROOT_PATH;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	private JRDesignBand band = new JRDesignBand();

	private JRDesignTextField jrDesignTextField = new JRDesignTextField();

	private JRDesignLine jrDesignLine = new JRDesignLine();

	private JRDesignExpression expression = new JRDesignExpression();

	private JRDesignDatasetParameter param = new JRDesignDatasetParameter();

	@Override
	@Transactional
	public JasperReportResponse createPDF(ComponentType componentType, Map<String, Object> parameters, String fileName,
			String layout, String pageSize, Integer topMargin, Integer bottonMargin, Integer leftMargin,
			Integer rightMargin, Integer contentFontSize, String pdfName, String... subReportFileName) {
		JasperReportResponse jasperReportResponse = null;
		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);

		try {
			MongoDbConnection mongoConnection = new MongoDbConnection(MONGO_HOST_URI, null, null);
			parameters.put("REPORT_CONNECTION", mongoConnection);
			parameters.put("SUBREPORT_DIR", JASPER_TEMPLATES_RESOURCE);
			JasperDesign design = createDesign(parameters, pageSize, contentFontSize, topMargin + 45, bottonMargin,
					leftMargin + 28, rightMargin + 28, componentType, fileName);
			JasperReport jasperReport = JasperCompileManager.compileReport(design);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			JasperExportManager.exportReportToPdfFile(jasperPrint, JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
			jasperReportResponse = new JasperReportResponse();
			jasperReportResponse.setPath(JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf");
			FileSystemResource fileSystemResource = new FileSystemResource(
					JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
			jasperReportResponse.setFileSystemResource(fileSystemResource);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentEncoding("pdf");
			metadata.setContentType("application/pdf");
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
					JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf",
					jasperReportResponse.getFileSystemResource().getFile());
			putObjectRequest.setMetadata(metadata);
			s3client.putObject(putObjectRequest);
			return jasperReportResponse;
		} catch (JRException e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	public JasperDesign createDesign(Map<String, Object> parameters, String pageSize, Integer contentFontSize,
			Integer topMargin, Integer bottonMargin, Integer leftMargin, Integer rightMargin,
			ComponentType componentType, String fileName) throws JRException {
		JasperDesign jasperDesign = null;
		if (componentType.getType().equalsIgnoreCase(ComponentType.EYE_PRESCRIPTION.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-optho-prescription.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.RECEIPT.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-receipt.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.CONSENT_FORM.getType()))
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-consent-form.jrxml");
		else if (componentType.getType().equalsIgnoreCase(ComponentType.ADMIT_CARD.getType()))
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-admit-card.jrxml");
		else if (componentType.getType().equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-discharge-summary.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.OT_REPORTS.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + fileName);
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DELIVERY_REPORTS.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + fileName);
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-lab-dental-works-a4.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_LAB_INSPECTION_REPORT.getType())) {
			jasperDesign = JRXmlLoader
					.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-dental-inspection-reports-a4.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.CERTIFICATE.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + fileName);
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_RECEIPT.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + fileName);
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.LAB_REQUISATION_FORM.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-a4.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.PATIENT_CARD.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo_patient_card.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.CERTIFICATE.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + fileName);
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.CALENDER_APPOINTMENT.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-calender-appointments-A4.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.FLOW_SHEET.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-discharge-summary.jrxml");
		} else if (componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_INSPECTION_REPORT.getType())) {
			jasperDesign = JRXmlLoader
					.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-dental-inspection-reports-a4.jrxml");

		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORK_INVOICE.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-dental-work-invoice-A4.jrxml");

		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORK_RECEIPT.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-receipt.jrxml");

		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_IMAGE_INVOICE.getType())) {
			jasperDesign = JRXmlLoader
					.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-dental-imaging-invoice-A4.jrxml");

		} else if (componentType.getType().equalsIgnoreCase(ComponentType.PROCEDURE_SHEET.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-procedure-sheet-A4.jrxml");

		} else if (componentType.getType().equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/" + "mongo-doctor-lab-A4.jrxml");
		} else {
			jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-data-A4.jrxml");
		}
		jasperDesign.setName("sampleDynamicJasperDesign");
		jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.NO_PAGES);
		int pageWidth = 595, pageHeight = 842;
		if (pageSize != null && pageSize.equalsIgnoreCase(PageSize.A5.name())) {
			topMargin = topMargin - 25;
			pageWidth = 420;
			pageHeight = 595;
		}

		if (ComponentType.DOCTOR_LAB_REPORTS.getType().equals(componentType.getType())) {
			leftMargin = 0;
			rightMargin = 0;
			topMargin = 0;
		}
		int columnWidth = pageWidth - leftMargin - rightMargin;
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setLeftMargin(leftMargin);
		jasperDesign.setRightMargin(rightMargin);
		if (topMargin != null) {
			jasperDesign.setTopMargin(topMargin);
			pageHeight = pageHeight - topMargin;
		}
		if (bottonMargin != null) {
			jasperDesign.setBottomMargin(bottonMargin);
			pageHeight = pageHeight - bottonMargin;
		}

		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName("Noto Sans");
		normalStyle.setDefault(true);
		normalStyle.setFontName("Noto Sans");
		normalStyle.setFontSize(new Float(contentFontSize));
		normalStyle.setPdfFontName("Helvetica");
		normalStyle.setPdfEncoding("Cp1252");
		normalStyle.setPdfEmbedded(false);
		jasperDesign.addStyle(normalStyle);

		JRDesignDatasetRun dsr = new JRDesignDatasetRun();
		if (!componentType.getType().equalsIgnoreCase(ComponentType.CONSENT_FORM.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.LAB_REQUISATION_FORM.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_LAB_INSPECTION_REPORT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.CALENDER_APPOINTMENT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_INSPECTION_REPORT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.PROCEDURE_SHEET.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
			dsr.setDatasetName("mongo-print-settings-dataset_1");

			expression = new JRDesignExpression();
			expression.setText("new net.sf.jasperreports.engine.JREmptyDataSource(1)");
			dsr.setDataSourceExpression(expression);

			Boolean showTableOne = (Boolean) parameters.get("showTableOne");
			jasperDesign.setPageHeader(createPageHeader(dsr, columnWidth, showTableOne, parameters));

			if (parameters.get("headerHtml") != null && !String.valueOf(parameters.get("headerHtml")).trim().isEmpty())

				((JRDesignSection) jasperDesign.getDetailSection())
						.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

			if (parameters.get("patientLeftText") != null && parameters.get("patientRightText") != null) {
				((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPatienDetailBand(dsr, jasperDesign,
						columnWidth, showTableOne, parameters.get("headerHtml")));
				((JRDesignSection) jasperDesign.getDetailSection())
						.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
			}
		}

		if (parameters.get("showHistory") != null && (boolean) parameters.get("showHistory"))
			createHistory(jasperDesign, parameters, contentFontSize, normalStyle, columnWidth);

		if (componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())) {
			if (parameters.get("clinicalNotes") != null)
				((JRDesignSection) jasperDesign.getDetailSection()).addBand(createClinicalNotesSubreport(parameters,
						contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));

			if (parameters.get("clinicalNotes") != null)
				((JRDesignSection) jasperDesign.getDetailSection()).addBand(createDiagramsSubreport(parameters, dsr,
						contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));

			if (parameters.get("prescriptions") != null)
				((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPrescriptionSubreport(parameters,
						contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));

			if (parameters.get("treatments") != null)
				((JRDesignSection) jasperDesign.getDetailSection()).addBand(createTreatmentServicesSubreport(parameters,
						contentFontSize, columnWidth, pageWidth, pageHeight, normalStyle));
		}

		else if (componentType.getType().equalsIgnoreCase(ComponentType.CLINICAL_NOTES.getType()))
			createClinicalNotes(parameters, jasperDesign, columnWidth, contentFontSize);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType()))
			createPrescription(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.TREATMENT.getType()))
			createTreatmentServices(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth, pageHeight,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.INVOICE.getType()))
			createInvoiceSubreport(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth, pageHeight,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.RECEIPT.getType()))
			createReceipt(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.CONSENT_FORM.getType()))
			createConsentForm(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.LAB_REQUISATION_FORM.getType()))
			createLabRequisationForm(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY.getType()))
			createDischargeSummary(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.ADMIT_CARD.getType()))
			createAdmitCard(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.OT_REPORTS.getType()))
			createOTReports(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.DELIVERY_REPORTS.getType()))
			createDeliveryReports(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType()))
			createDentalWorkReport(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_RECEIPT.getType()))
			createMultipleReceipt(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_LAB_INSPECTION_REPORT.getType()))
			createDentalInspectionReport(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.PATIENT_CARD.getType()))
			createPatientCardPrint(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.CERTIFICATE.getType()))
			createPatientCertificate(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.CALENDER_APPOINTMENT.getType()))
			createCalenderAppointment(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		if (parameters.get("eyePrescriptions") != null
				|| componentType.getType().equalsIgnoreCase(ComponentType.EYE_PRESCRIPTION.getType()))
			createEyePrescription(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.FLOW_SHEET.getType()))
			createFlowSheet(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_INSPECTION_REPORT.getType()))
			createMultipleInspectionReport(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight,
					columnWidth, normalStyle);

		else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORK_INVOICE.getType()))
			createDentalWorkInvoiceSubreport(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth,
					pageHeight, normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORK_RECEIPT.getType()))
			createDentalLabReceipt(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_IMAGE_INVOICE.getType()))
			createDentalImagingInvoiceSubreport(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth,
					pageHeight, normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.PROCEDURE_SHEET.getType()))
			createprocedureSheet(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth, pageHeight,
					normalStyle);
		else if (componentType.getType().equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType()))
			createDoctorLabReport(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth, pageHeight,
					normalStyle);
		if (parameters.get("followUpAppointment") != null
				&& !componentType.getType().equalsIgnoreCase(ComponentType.CONSENT_FORM.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.LAB_REQUISATION_FORM.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_LAB_INSPECTION_REPORT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.CALENDER_APPOINTMENT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_INSPECTION_REPORT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.PROCEDURE_SHEET.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
			band = new JRDesignBand();
			band.setHeight(21);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{followUpAppointment}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		if (!componentType.getType().equalsIgnoreCase(ComponentType.CONSENT_FORM.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.LAB_REQUISATION_FORM.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_LAB_INSPECTION_REPORT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.PATIENT_CARD.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.CALENDER_APPOINTMENT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.MULTIPLE_INSPECTION_REPORT.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DENTAL_IMAGE_INVOICE.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.PROCEDURE_SHEET.getType())
				&& !componentType.getType().equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType()))

			jasperDesign.setPageFooter(createPageFooter(columnWidth, parameters, contentFontSize));
		// dsr.setDataSourceExpression(new JRDesignExpression("new
		// net.sf.jasperreports.engine.JREmptyDataSource(1)"));
		// String logoURL = (String) parameters.get("logoURL");
		// parameters.put("headerLeftText", "<table width='10000px'
		// height='2000px' style='background-color:#E5DD6F;'>"
		// + "<tr width='10000px' height='2000px' border='2px'>"+
		// "<td width='100%' style='font-family:Noto
		// Sans;font-size:100px;background-color:#E6E6FA;'>"
		// + "<p><b>Dr. (Mrs.) R.K. KANDHARI</b></p>"+
		// "<p style='text-align:right;'>M.D., D.G.O.</p>"
		// + "<p>Consulting Obstetrician & Gynaecologist</p>"+
		// "<p>0712 - 2286215, &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		// Mob- 9822203359</p>"+
		// "<hr><p>MVC REGD. NO. 48001 HOSPITAL REGD. NO. 195</p></td>"
		// + "<td width='30%'><td width='30%'><img
		// style='width:200px;height:200px;' src='"+logoURL+"'/></td>"
		// + "<td width='100%' style='font-family:Noto
		// Sans;font-size:100px;background-color:red;' border='1px'>"+
		// "<p><b>KANDHARI <br>MATERNITY & <br>NURSING HOME</b></p>"
		// + "<p>plot no.5 behind gulmohar hall</p>"+
		// "<p>Pandey Layout, Khamla, Nagpur - 440025</p></td>"
		// + "</tr></table>");
		// jasperDesign.setPageHeader(createPageHeader(dsr, columnWidth));
		// ((JRDesignSection)
		// jasperDesign.getDetailSection()).addBand(createLine(0, columnWidth,
		// PositionTypeEnum.FIX_RELATIVE_TO_TOP));
		// ((JRDesignSection)
		// jasperDesign.getDetailSection()).addBand(createPatienDetailBand(dsr,
		// jasperDesign, columnWidth));
		// ((JRDesignSection)
		// jasperDesign.getDetailSection()).addBand(createLine(0, columnWidth,
		// PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		return jasperDesign;

	}

	private void createHistory(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			JRDesignStyle normalStyle, int columnWidth) {
		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 150;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{FamilyHistoryTitle}", "$P{FH}", fieldWidth, false, 0, false);

		if (parameters.get("showPLH") != null && (boolean) parameters.get("showPLH")) {
			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{PersonalHistoryTitle}"));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth(fieldWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
			addItems(jasperDesign, columnWidth, "$P{DietTitle}", "$P{diet}", fieldWidth, false, 15, false);
			addItems(jasperDesign, columnWidth, "$P{AddictionsTitle}", "$P{addictions}", fieldWidth, false, 15, false);
			addItems(jasperDesign, columnWidth, "$P{BowelHabitTitle}", "$P{bowelHabit}", fieldWidth, false, 15, false);
			addItems(jasperDesign, columnWidth, "$P{BladderHabitTitle}", "$P{bladderHabit}", fieldWidth, false, 15,
					false);
		}

		addItems(jasperDesign, columnWidth, "$P{OngoingDrugsTitle}", "$P{ongoingDrugs}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{AllergiesTitle}", "$P{allergies}", fieldWidth, false, 0, false);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
	}

	private void createClinicalNotes(Map<String, Object> parameters, JasperDesign jasperDesign, int columnWidth,
			Integer contentFontSize) {
		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 145;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		Boolean showTitle = (Boolean) parameters.get("showPCTitle");

		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{indicationOfUSG}.equals( null ) && !$P{indicationOfUSG}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{USGTITLE}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$P{vitalSigns}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$P{presentComplaint}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{PastHistoryTitle}", "$P{pastHistory}", fieldWidth, false, 0,
				false);

		if (showTitle) {
			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{ComplaintsTitle}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setUnderline(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		addItems(jasperDesign, columnWidth, "$P{PCNose}", "$P{pcNose}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PCOralCavity}", "$P{pcOralCavity}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PCThroat}", "$P{pcThroat}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PCEars}", "$P{pcEars}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PastHistoryTitle}", "$P{PH}", fieldWidth, false, 0, false);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression(
				"(!$P{personalHistoryAlcohol}.equals( null ) && ! $P{personalHistoryAlcohol} .isEmpty()) || (!$P{personalHistoryTobacco}.equals( null ) && ! $P{personalHistoryTobacco} .isEmpty())"
						+ "|| (!$P{personalHistorySmoking}.equals( null ) && ! $P{personalHistorySmoking} .isEmpty()) || (!$P{personalHistoryDiet}.equals( null ) && ! $P{personalHistoryDiet} .isEmpty())"
						+ "|| (!$P{personalHistoryOccupation}.equals( null ) && ! $P{personalHistoryOccupation} .isEmpty())"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PersonalHistoryTitle}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setUnderline(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryTobacco}", "$P{personalHistoryTobacco}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryAlcohol}", "$P{personalHistoryAlcohol}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistorySmoking}", "$P{personalHistorySmoking}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryDiet}", "$P{personalHistoryDiet}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryOccupation}", "$P{personalHistoryOccupation}",
				fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{FamilyHistoryTitle}", "$P{familyHistory}", fieldWidth, false, 0, false);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression(
				"!$P{generalHistoryDrugs}.equals( null ) && ! $P{generalHistoryDrugs} .isEmpty() || !$P{generalHistoryMedicine}.equals( null ) && ! $P{generalHistoryMedicine} .isEmpty()"
						+ "|| !$P{generalHistoryAllergies}.equals( null ) && ! $P{generalHistoryAllergies} .isEmpty() || !$P{generalHistorySurgical}.equals( null ) && ! $P{generalHistorySurgical} .isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{GeneralHistoryTitle}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setUnderline(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{GeneralHistoryMedicine}", "$P{generalHistoryMedicine}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{GeneralHistorySurgical}", "$P{generalHistorySurgical}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{GeneralHistoryDrugs}", "$P{generalHistoryDrugs}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{GeneralHistoryAllergies}", "$P{generalHistoryAllergies}", fieldWidth,
				false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Complaints}", "$P{complaints}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$P{presentComplaintHistory}", fieldWidth,
				false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$P{menstrualHistory}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$P{obstetricHistory}", fieldWidth, false, 0,
				false);

		addItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$P{generalExam}", fieldWidth, false, 0, false);

		showTitle = (Boolean) parameters.get("showExamTitle");
		if (showTitle) {
			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Examination}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setUnderline(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		addItems(jasperDesign, columnWidth, "$P{NoseExam}", "$P{noseExam}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{OralCavityThroatExam}", "$P{oralCavityThroatExam}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{IndirectLarygoscopyExam}", "$P{indirectLarygoscopyExam}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{NeckExam}", "$P{neckExam}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{EarsExam}", "$P{earsExam}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{SystemExam}", "$P{systemExam}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{NoOfChildren}", "$P{noOfChildren}", fieldWidth, false, 0, false);
		addLMPAndEDD(jasperDesign, columnWidth, fieldWidth, 0, "$P{LMP}", "$P{lmp}", "$P{EDD}", "$P{edd}");

		addItems(jasperDesign, columnWidth, "$P{IndicationOfUSG}", "$P{indicationOfUSG}", fieldWidth, true, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PA}", "$P{pa}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PS}", "$P{ps}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PV}", "$P{pv}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{EcgDetails}", "$P{ecgDetails}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Echo}", "$P{echo}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{XRayDetails}", "$P{xRayDetails}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Holter}", "$P{holter}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Observations}", "$P{observations}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PainScale}", "$P{painScale}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$P{provisionalDiagnosis}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$P{diagnosis}", fieldWidth, false, 0, true);

		addItems(jasperDesign, columnWidth, "$P{ProcedureNote}", "$P{procedureNote}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Investigations}", "$P{investigations}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Notes}", "$P{notes}", fieldWidth, false, 0, false);

		JRDesignDatasetRun dsr = new JRDesignDatasetRun();
		dsr.setDatasetName("dataset1");
		dsr.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{diagrams})"));
		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$P{diagrams}.isEmpty()"));
		band.setHeight(158);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Diagrams}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(8);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(80);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		StandardListComponent listComponent = new StandardListComponent();
		listComponent.setPrintOrderValue(PrintOrderEnum.HORIZONTAL);

		DesignListContents contents = new DesignListContents();
		contents.setHeight(130);
		contents.setWidth((columnWidth - 80) / 3);

		JRDesignImage jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
		jrDesignImage.setExpression(new JRDesignExpression("$F{url}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY(2);
		jrDesignImage.setHeight(100);
		jrDesignImage.setWidth((columnWidth - 80) / 4);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		contents.addElement(jrDesignImage);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{tags}"));
		jrDesignTextField.setY(110);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth((columnWidth - 80) / 4);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchWithOverflow(true);
		contents.addElement(jrDesignTextField);

		listComponent.setContents(contents);
		listComponent.setDatasetRun(dsr);
		JRDesignComponentElement reportElement = new JRDesignComponentElement();
		reportElement.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "list"));
		reportElement.setHeight(150);
		reportElement.setWidth(columnWidth - 80);
		reportElement.setX(80);
		reportElement.setY(8);
		reportElement.setComponent(listComponent);

		band.addElement(reportElement);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addLMPAndEDD(JasperDesign jasperDesign, int columnWidth, int fieldWidth, int xSpaceForTitle,
			String lmpKey, String lmpValue, String eddKey, String eddValue) {
		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression("!" + lmpValue + ".equals( null ) && !" + lmpValue
				+ ".isEmpty() && !" + eddValue + ".equals( null ) && !" + eddKey + ".isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(lmpKey));
		jrDesignTextField.setX(xSpaceForTitle);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(lmpValue));
		jrDesignTextField.setX(xSpaceForTitle + fieldWidth + 1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(eddKey));
		jrDesignTextField.setX(xSpaceForTitle + fieldWidth + 5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(lmpValue));
		jrDesignTextField.setX(xSpaceForTitle + fieldWidth + 5 + fieldWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void createPrescription(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {

		band = new JRDesignBand();
		band.setHeight(2);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDrugs(parameters, contentFontSize, columnWidth,
				pageWidth, pageHeight, "$P{prescriptionItems}", normalStyle));
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(addLabTest(jasperDesign, columnWidth, "$P{labTest}", contentFontSize));
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(addAdvice(jasperDesign, columnWidth, "$P{advice}", contentFontSize));

	}

	private JRBand createLine(int yPoint, int columnWidth, PositionTypeEnum positionTypeEnum) {
		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(yPoint);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(positionTypeEnum);
		band.addElement(jrDesignLine);
		return band;
	}

	private JRBand createPatienDetailBand(JRDesignDatasetRun dsr, JasperDesign jasperDesign, int columnWidth,
			Boolean showTableOne, Object headerHtml) throws JRException {

		band = new JRDesignBand();
		band.setHeight(10);

		param = new JRDesignDatasetParameter();
		param.setName("patientLeftText");
		param.setExpression(new JRDesignExpression("$P{patientLeftText}"));
		dsr.addParameter(param);

		param = new JRDesignDatasetParameter();
		param.setName("patientRightText");
		param.setExpression(new JRDesignExpression("$P{patientRightText}"));
		dsr.addParameter(param);

		DesignCell columnHeader = new DesignCell();
		columnHeader.setHeight(10);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(1);
		jrDesignTextField.setWidth((50 * columnWidth) / 100);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setExpression(new JRDesignExpression("$P{patientLeftText}"));
		columnHeader.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX((((62 * columnWidth) / 100)));
		jrDesignTextField.setY(1);
		jrDesignTextField.setWidth((38 * columnWidth) / 100);
		jrDesignTextField.setHeight(9);

		jrDesignTextField.setMarkup("html");

		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setExpression(new JRDesignExpression("$P{patientRightText}"));
		columnHeader.addElement(jrDesignTextField);

		StandardColumn column = new StandardColumn();
		column.setDetailCell(columnHeader);
		column.setWidth(columnWidth);
		StandardTable table = new StandardTable();
		table.addColumn(column);
		table.setDatasetRun(dsr);
		JRDesignComponentElement reportElement = new JRDesignComponentElement();
		reportElement.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "table"));
		reportElement.setHeight(10);
		reportElement.setWidth(columnWidth);
		reportElement.setX(0);

		if (headerHtml != null && !String.valueOf(headerHtml).trim().isEmpty())
			reportElement.setY(0);
		else if (showTableOne)
			reportElement.setY(0);
		else
			reportElement.setY(0);
		reportElement.setComponent(table);

		band.addElement(reportElement);

		return band;
	}

	private JRBand createPageHeader(JRDesignDatasetRun dsr, int columnWidth, Boolean showTableOne,
			Map<String, Object> parameters) throws JRException {
		band = new JRDesignBand();
		band.setHeight(1);

		if (parameters.get("headerHtml") != null && !String.valueOf(parameters.get("headerHtml")).trim().isEmpty()) {
			param = new JRDesignDatasetParameter();
			param.setName("headerHtml");
			param.setExpression(new JRDesignExpression("$P{headerHtml}"));
			dsr.addParameter(param);

			HtmlComponent htmlComponent = new HtmlComponent();
			htmlComponent.setHtmlContentExpression(new JRDesignExpression("$P{headerHtml}"));
			htmlComponent.setScaleType(ScaleImageEnum.REAL_SIZE);

			JRDesignComponentElement reportElement = new JRDesignComponentElement();
			reportElement.setComponentKey(
					new ComponentKey("http://jasperreports.sourceforge.net/htmlcomponent", "hc", "html"));
			reportElement.setHeight(40);
			reportElement.setWidth(columnWidth);
			reportElement.setX(0);
			reportElement.setY(0);
			reportElement.setComponent(htmlComponent);
			htmlComponent.setScaleType(ScaleImageEnum.REAL_SIZE);
			band.addElement(reportElement);
			band.setHeight(40);
		} else {
			band.setPrintWhenExpression(new JRDesignExpression(
					"!$P{logoURL}.isEmpty() || !$P{headerLeftText}.isEmpty() || !$P{headerRightText}.isEmpty()"));
			param = new JRDesignDatasetParameter();
			param.setName("logoURL");

			param.setExpression(new JRDesignExpression("$P{logoURL}"));

			dsr.addParameter(param);

			param = new JRDesignDatasetParameter();
			param.setName("headerLeftText");
			param.setExpression(new JRDesignExpression("$P{headerLeftText}"));
			dsr.addParameter(param);

			param = new JRDesignDatasetParameter();
			param.setName("headerRightText");
			param.setExpression(new JRDesignExpression("$P{headerRightText}"));
			dsr.addParameter(param);

			DesignCell columnHeader = new DesignCell();
			columnHeader.setHeight(40);

			jrDesignTextField = new JRDesignTextField();
			expression = new JRDesignExpression();
			expression.setText("$P{headerLeftText}");
			jrDesignTextField.setExpression(expression);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth((38 * columnWidth) / 100);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setStretchWithOverflow(true);
			columnHeader.addElement(jrDesignTextField);

			JRDesignImage jrDesignImage = new JRDesignImage(null);
			jrDesignImage.setPrintWhenExpression(new JRDesignExpression("!$P{logoURL}.isEmpty()"));
			jrDesignImage.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
			expression = new JRDesignExpression();
			expression.setText("$P{logoURL}");
			jrDesignImage.setExpression(expression);
			jrDesignImage.setX(((38 * columnWidth) / 100) + 1);
			jrDesignImage.setY(0);
			jrDesignImage.setHeight(40);
			jrDesignImage.setWidth(50);
			jrDesignImage.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
			columnHeader.addElement(jrDesignImage);

			jrDesignTextField = new JRDesignTextField();
			expression = new JRDesignExpression();
			expression.setText("$P{headerRightText}");
			jrDesignTextField.setExpression(expression);
			jrDesignTextField.setX((((62 * columnWidth) / 100)));
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth((38 * columnWidth) / 100);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			columnHeader.addElement(jrDesignTextField);

			StandardColumn column = new StandardColumn();
			column.setDetailCell(columnHeader);
			column.setWidth(columnWidth);
			StandardTable table = new StandardTable();
			table.addColumn(column);
			table.setDatasetRun(dsr);

			JRDesignComponentElement reportElement = new JRDesignComponentElement();
			reportElement.setComponentKey(
					new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "table"));
			reportElement.setHeight(0);
			reportElement.setWidth(columnWidth);
			reportElement.setX(0);
			reportElement.setY(0);
			reportElement.setComponent(table);

			band.addElement(reportElement);
			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			if (showTableOne)
				jrDesignLine.setY(-10);
			else
				jrDesignLine.setY(0);

			jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
			band.addElement(jrDesignLine);
		}

		return band;
	}

	private JRBand createClinicalNotesSubreport(Map<String, Object> parameters, Integer contentFontSize, int pageWidth,
			int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {

		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-clinical-notes_subreport-A4.jrxml");
		jasperDesign.setName("clinical Notes");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);
		Boolean showTitle = (Boolean) parameters.get("showPCTitle");

		int fieldWidth = 108;
		if (contentFontSize > 13)
			fieldWidth = 135;
		else if (contentFontSize > 11)
			fieldWidth = 118;

		band = new JRDesignBand();
		band.setHeight(1);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{ClinicalNotes}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(2);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$F{indicationOfUSG}.equals( null ) && !$F{indicationOfUSG}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{USGTITLE}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		// add clinical notes items
		addItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$F{vitalSigns}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$F{presentComplaint}", fieldWidth, false, 0,
				false);

		if (showTitle) {

			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{ComplaintsTitle}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setUnderline(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		addItems(jasperDesign, columnWidth, "$P{PCNose}", "$F{pcNose}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PCOralCavity}", "$F{pcOralCavity}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PCThroat}", "$F{pcThroat}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PCEars}", "$F{pcEars}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PastHistory}", "$F{pastHistory}", fieldWidth, false, 0, false);
		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression(
				"(!$F{personalHistoryAlcohol}.equals( null ) && ! $F{personalHistoryAlcohol} .isEmpty()) || (!$F{personalHistoryTobacco}.equals( null ) && ! $F{personalHistoryTobacco} .isEmpty())"
						+ "|| (!$F{personalHistorySmoking}.equals( null ) && ! $F{personalHistorySmoking} .isEmpty()) || (!$F{personalHistoryDiet}.equals( null ) && ! $F{personalHistoryDiet} .isEmpty())"
						+ "|| (!$F{personalHistoryOccupation}.equals( null ) && ! $F{personalHistoryOccupation} .isEmpty())"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PersonalHistoryTitle}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setUnderline(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryTobacco}", "$F{personalHistoryTobacco}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryAlcohol}", "$F{personalHistoryAlcohol}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistorySmoking}", "$F{personalHistorySmoking}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryDiet}", "$F{personalHistoryDiet}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryOccupation}", "$F{personalHistoryOccupation}",
				fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{FamilyHistory}", "$F{familyHistory}", fieldWidth, false, 0, false);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression(
				"!$F{generalHistoryDrugs}.equals( null ) && ! $F{generalHistoryDrugs} .isEmpty() || !$F{generalHistoryMedicine}.equals( null ) && ! $F{generalHistoryMedicine} .isEmpty()"
						+ "|| !$F{generalHistoryAllergies}.equals( null ) && ! $F{generalHistoryAllergies} .isEmpty() || !$F{generalHistorySurgical}.equals( null ) && ! $F{generalHistorySurgical} .isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{GeneralHistoryTitle}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setUnderline(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{GeneralHistoryMedicine}", "$F{generalHistoryMedicine}", fieldWidth,
				false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{GeneralHistorySurgical}", "$F{generalHistorySurgical}", fieldWidth,
				false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{GeneralHistoryDrugs}", "$F{generalHistoryDrugs}", fieldWidth, false, 0,
				false);

		addItems(jasperDesign, columnWidth, "$P{GeneralHistoryAllergies}", "$F{generalHistoryAllergies}", fieldWidth,
				false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Complaints}", "$F{complaints}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$F{presentComplaintHistory}", fieldWidth,
				false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$F{menstrualHistory}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$F{obstetricHistory}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$F{generalExam}", fieldWidth, false, 0, false);

		showTitle = (Boolean) parameters.get("showExamTitle");
		if (showTitle) {
			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Examination}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setUnderline(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		addItems(jasperDesign, columnWidth, "$P{NoseExam}", "$F{noseExam}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{OralCavityThroatExam}", "$F{oralCavityThroatExam}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{IndirectLarygoscopyExam}", "$F{indirectLarygoscopyExam}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{NeckExam}", "$F{neckExam}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{EarsExam}", "$F{earsExam}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{SystemExam}", "$F{systemExam}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{NoOfChildren}", "$F{noOfChildren}", fieldWidth, false, 0, false);
		addLMPAndEDD(jasperDesign, columnWidth, fieldWidth, 0, "$P{LMP}", "$F{lmp}", "$P{EDD}", "$F{edd}");
		addItems(jasperDesign, columnWidth, "$P{IndicationOfUSG}", "$F{indicationOfUSG}", fieldWidth, true, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PA}", "$F{pa}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PS}", "$F{ps}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PV}", "$F{pv}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{EcgDetails}", "$F{ecgDetails}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Echo}", "$F{echo}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{XRayDetails}", "$F{xRayDetails}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Holter}", "$F{holter}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Observations}", "$F{observations}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{PainScale}", "$F{painScale}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$F{provisionalDiagnosis}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$F{diagnosis}", fieldWidth, false, 0, true);

		addItems(jasperDesign, columnWidth, "$P{ProcedureNote}", "$F{procedureNote}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Investigations}", "$F{investigations}", fieldWidth, false, 0, false);

		addItems(jasperDesign, columnWidth, "$P{Notes}", "$F{notes}", fieldWidth, false, 0, false);

		band = new JRDesignBand();
		band.setHeight(2);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-clinical-notes_subreport-A4.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		expression = new JRDesignExpression();
		expression.setText("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{clinicalNotes})");
		jSubreport.setDataSourceExpression(expression);

		expression = new JRDesignExpression();
		expression.setText("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-clinical-notes_subreport-A4.jasper\"");
		jSubreport.setExpression(expression);

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);
		return band;
	}

	private JRBand createDiagramsSubreport(Map<String, Object> parameters, JRDesignDatasetRun dsr,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-data_diagrams-A4.jrxml");
		jasperDesign.setName("clinical Notes diagrams");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		dsr = new JRDesignDatasetRun();
		dsr.setDatasetName("dataset1");
		dsr.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($F{diagrams})"));

		jasperDesign.addStyle(normalStyle);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{diagrams}.isEmpty()"));
		band.setHeight(158);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Diagrams}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(8);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(80);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		StandardListComponent listComponent = new StandardListComponent();
		listComponent.setPrintOrderValue(PrintOrderEnum.HORIZONTAL);

		DesignListContents contents = new DesignListContents();
		contents.setHeight(130);
		contents.setWidth((columnWidth - 80) / 3);

		JRDesignImage jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
		jrDesignImage.setExpression(new JRDesignExpression("$F{url}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY(2);
		jrDesignImage.setHeight(100);
		jrDesignImage.setWidth((columnWidth - 80) / 4);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		contents.addElement(jrDesignImage);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{tags}"));
		jrDesignTextField.setY(110);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth((columnWidth - 80) / 4);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchWithOverflow(true);
		contents.addElement(jrDesignTextField);

		listComponent.setContents(contents);
		listComponent.setDatasetRun(dsr);
		JRDesignComponentElement reportElement = new JRDesignComponentElement();
		reportElement.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "list"));
		reportElement.setHeight(150);
		reportElement.setWidth(columnWidth - 80);
		reportElement.setX(80);
		reportElement.setY(8);
		reportElement.setComponent(listComponent);

		band.addElement(reportElement);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-data_diagrams-A4.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{clinicalNotes})"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-data_diagrams-A4.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);
		return band;

	}

	private JRBand createPrescriptionSubreport(Map<String, Object> parameters, Integer contentFontSize, int pageWidth,
			int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-prescription-subreport-A4.jrxml");
		jasperDesign.setName("Prescription");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		band = new JRDesignBand();
		band.setHeight(1);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(
				addDrugs(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$F{items}", normalStyle));
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(addLabTest(jasperDesign, columnWidth, "$F{labTest}", contentFontSize));
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(addAdvice(jasperDesign, columnWidth, "$F{advice}", contentFontSize));

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-prescription-subreport-A4.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{prescriptions})"));
		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-prescription-subreport-A4.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);
		return band;
	}

	private JRDesignBand addAdvice(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 150;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		band = new JRDesignBand();
		band.setHeight(22);
		band.setPrintWhenExpression(
				new JRDesignExpression("!" + value + ".equals( null ) && !" + value + ".isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Advice}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(fieldWidth + 1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth - fieldWidth - 1);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		return band;
	}

	private JRDesignBand addLabTest(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 150;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		band = new JRDesignBand();
		band.setHeight(22);
		band.setPrintWhenExpression(new JRDesignExpression("!" + value + ".equals( null )"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{LabTest}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(fieldWidth + 1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth - fieldWidth - 1);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		return band;
	}

	private JRDesignBand addDrugs(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-prescription_items_subreport-A4.jrxml");
		jasperDesign.setName("Prescription Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		band = new JRDesignBand();
		band.setHeight(26);

		Boolean showIntructions = (Boolean) parameters.get("showIntructions") != null
				? (Boolean) parameters.get("showIntructions")
				: false;
		Boolean showDirection = (Boolean) parameters.get("showDirection") != null
				? (Boolean) parameters.get("showDirection")
				: false;
		String instructionAlign = parameters.get("instructionAlign") != null
				? (String) parameters.get("instructionAlign")
				: "HORIZONTAL";
		int drugWidth = 0, dosageWidth = 0, directionWidth = 0, durationWidth = 0, instructionWidth = 0;
		if (showDirection != null && showIntructions != null) {
			if (showDirection && showIntructions) {
				drugWidth = (30 * (columnWidth - 31)) / 100;
				dosageWidth = (16 * (columnWidth - 31) / 100);
				directionWidth = (23 * (columnWidth - 31) / 100);
				durationWidth = (13 * (columnWidth - 31) / 100);
				instructionWidth = (18 * (columnWidth - 31) / 100);
			} else if (showDirection) {
				drugWidth = (30 * (columnWidth - 31)) / 100;
				dosageWidth = (21 * (columnWidth - 31) / 100);
				directionWidth = (29 * (columnWidth - 31) / 100);
				durationWidth = (20 * (columnWidth - 31) / 100);
			} else if (showIntructions) {
				drugWidth = (30 * (columnWidth - 31)) / 100;
				dosageWidth = (21 * (columnWidth - 31) / 100);
				durationWidth = (20 * (columnWidth - 31) / 100);
				instructionWidth = (29 * (columnWidth - 31) / 100);
			} else {
				drugWidth = (40 * (columnWidth - 31)) / 100;
				dosageWidth = (30 * (columnWidth - 31) / 100);
				durationWidth = (30 * (columnWidth - 31) / 100);
			}
		}

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{DrugName}"));
		jrDesignTextField.setX(35);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(drugWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Frequency}"));
		jrDesignTextField.setX(35 + drugWidth);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(dosageWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		if (showDirection) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Direction}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(directionWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);
		}
		if (showDirection && showIntructions && instructionAlign.equalsIgnoreCase("VERTICAL")) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Duration}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + 15);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(durationWidth - 15 + 5);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(
					new JRDesignExpression("!$F{instruction}.equals(null) && !$F{instruction}.isEmpty() "));

			jrDesignTextField.setExpression(new JRDesignExpression("$P{Instruction}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + durationWidth + 10);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(instructionWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

		} else {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Duration}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + 15);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(durationWidth - 15);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);
			if (showIntructions && instructionAlign.equalsIgnoreCase("VERTICAL")) {
				jrDesignTextField = new JRDesignTextField();
				jrDesignTextField.setPrintWhenExpression(
						new JRDesignExpression("!$F{instruction}.equals(null) && !$F{instruction}.isEmpty() "));

				jrDesignTextField.setExpression(new JRDesignExpression("$P{Instruction}"));
				jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + durationWidth);
				jrDesignTextField.setY(4);
				jrDesignTextField.setHeight(15);
				jrDesignTextField.setWidth(instructionWidth);
				jrDesignTextField.setBold(true);
				jrDesignTextField.setStretchWithOverflow(true);
				jrDesignTextField.setFontSize(new Float(titleFontSize));
				band.addElement(jrDesignTextField);
			}
		}

		// if(showIntructions){
		// jrDesignTextField = new JRDesignTextField();
		// jrDesignTextField.setExpression(new
		// JRDesignExpression("$P{Instruction}"));
		// jrDesignTextField.setX(35+drugWidth+dosageWidth+directionWidth+durationWidth+2);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(instructionWidth-2);
		// jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new
		// Float(titleFontSize));
		// band.addElement(jrDesignTextField);
		// }
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.SMALL.name()))
			band.setHeight(22);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.MEDIUM.name()))
			band.setHeight(27);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.LARGE.name()))
			band.setHeight(32);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{drug}"));
		jrDesignTextField.setX(35);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(drugWidth - 3);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{dosage}"));
		jrDesignTextField.setX(38 + drugWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(dosageWidth - 3);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		if (showDirection)

		{
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{direction}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(directionWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}

		if (showDirection && showIntructions && instructionAlign.equalsIgnoreCase("VERTICAL")) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{duration}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + 15);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(durationWidth - 15 + 5);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			if (showIntructions && instructionAlign.equalsIgnoreCase("VERTICAL")) {
				jrDesignTextField = new JRDesignTextField();
				jrDesignTextField.setPrintWhenExpression(
						new JRDesignExpression("!$F{instruction}.equals(null) && !$F{instruction}.isEmpty() "));
				jrDesignTextField.setExpression(new JRDesignExpression("$F{instruction}"));
				jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + durationWidth + 10);
				jrDesignTextField.setY(0);
				jrDesignTextField.setHeight(18);
				jrDesignTextField.setWidth(instructionWidth);
				jrDesignTextField.setStretchWithOverflow(true);
				band.addElement(jrDesignTextField);
			}
		} else {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{duration}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + 15);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(durationWidth - 15);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			if (showIntructions && instructionAlign.equalsIgnoreCase("VERTICAL")) {
				jrDesignTextField = new JRDesignTextField();
				jrDesignTextField.setPrintWhenExpression(
						new JRDesignExpression("!$F{instruction}.equals(null) && !$F{instruction}.isEmpty() "));
				jrDesignTextField.setExpression(new JRDesignExpression("$F{instruction}"));
				jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + durationWidth);
				jrDesignTextField.setY(0);
				jrDesignTextField.setHeight(18);
				jrDesignTextField.setWidth(instructionWidth);
				jrDesignTextField.setStretchWithOverflow(true);
				band.addElement(jrDesignTextField);
			}
		}

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		if (showIntructions && instructionAlign.equalsIgnoreCase("HORIZONTAL")) {
			band = new JRDesignBand();
			band.setPrintWhenExpression(
					new JRDesignExpression("!$F{instruction}.equals(null) && !$F{instruction}.isEmpty() "));
			band.setSplitType(SplitTypeEnum.STRETCH);
			band.setHeight(18);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(
					new JRDesignExpression("!$F{instruction}.equals(null) && !$F{instruction}.isEmpty() "));
			jrDesignTextField.setExpression(new JRDesignExpression("$F{instruction}"));
			jrDesignTextField.setX(35);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setWidth(columnWidth - 40);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}
		/*
		 * band = new JRDesignBand(); band.setHeight(20); band.setPrintWhenExpression(
		 * new JRDesignExpression(
		 * "!$F{genericNames}.equals( null ) && !$F{genericNames}.isEmpty()"));
		 * jrDesignTextField = new JRDesignTextField();
		 * jrDesignTextField.setExpression(new JRDesignExpression("$F{genericNames}"));
		 * jrDesignTextField.setX(0); jrDesignTextField.setY(0);
		 * jrDesignTextField.setHeight(18); jrDesignTextField.setWidth(columnWidth);
		 * jrDesignTextField.setStretchWithOverflow(true);
		 * jrDesignTextField.setMarkup("html"); band.addElement(jrDesignTextField);
		 * ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		 */

		band = new JRDesignBand();
		band.setHeight(13);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(12);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setBlankWhenNull(true);
		band.addElement(jrDesignTextField);
		jasperDesign.setColumnFooter(band);
		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-prescription_items_subreport-A4.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-prescription_items_subreport-A4.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private void addItems(JasperDesign jasperDesign, int columnWidth, String key, String value, Integer fieldWidth,
			boolean isHTML, Integer xSpaceForTitle, boolean isBold) {

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(
				new JRDesignExpression("!" + value + ".equals( null ) && !" + value + ".isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(key));
		jrDesignTextField.setX(xSpaceForTitle);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		band.addElement(jrDesignTextField);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(xSpaceForTitle + fieldWidth + 1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth - fieldWidth - 1 - xSpaceForTitle);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setBold(isBold);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		if (isHTML)
			jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addItem(JasperDesign jasperDesign, int columnWidth, String value, Integer fieldWidth, boolean isHTML,
			Integer xSpaceForTitle, boolean isBold) {

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(
				new JRDesignExpression("!" + value + ".equals( null ) && !" + value + ".isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(xSpaceForTitle);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(fieldWidth);
		jrDesignTextField.setBold(isBold);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		if (isHTML) {
			jrDesignTextField.setMarkup("html");
		}
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addsignature(JasperDesign jasperDesign, Map<String, Object> parameter, Integer contentFontSize,
			JRDesignStyle normalStyle, int columnWidth) {

		band = new JRDesignBand();

		int Startwith = 2;
		band.setSplitType(SplitTypeEnum.IMMEDIATE);

		if (!DPDoctorUtils.anyStringEmpty(parameter.get("poweredBy").toString())) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{poweredBy}.isEmpty()"));
			jrDesignTextField.setExpression(new JRDesignExpression("$P{poweredBy}"));
			jrDesignTextField.setFontSize(new Float(9));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(2);
			jrDesignTextField.setHeight(22);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

		}
		band.setHeight(25);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private JRBand createPageFooter(int columnWidth, Map<String, Object> parameter, Integer contentFontSize)
			throws JRException {
		band = new JRDesignBand();
		int Startwith = 2;

		band.setSplitType(SplitTypeEnum.IMMEDIATE);

		if (!DPDoctorUtils.anyStringEmpty(parameter.get("footerSignature").toString())) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{footerSignature}"));
			jrDesignTextField.setBold(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize + 2));
			jrDesignTextField.setX(176);
			jrDesignTextField.setY(Startwith);
			jrDesignTextField.setHeight(22);
			jrDesignTextField.setWidth(columnWidth - 176);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}
		if (!DPDoctorUtils.anyStringEmpty(parameter.get("footerSignature").toString())
				|| !DPDoctorUtils.anyStringEmpty(parameter.get("poweredBy").toString())) {
			Startwith = Startwith + 25;
		}
		if (!DPDoctorUtils.anyStringEmpty(parameter.get("bottomSignText").toString())) {
			int count = parameter.get("bottomSignText").toString().split("\r\n|\r|\n").length;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{bottomSignText}"));
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(Startwith);
			jrDesignTextField.setHeight(18 * count);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			Startwith = Startwith + (count * 18) + 2;

		}
		if (!DPDoctorUtils.anyStringEmpty(parameter.get("footerBottomText").toString())) {

			jrDesignLine = new JRDesignLine();
			jrDesignLine.setPrintWhenExpression(new JRDesignExpression("!$P{footerBottomText}.isEmpty()"));
			jrDesignLine.setX(0);
			jrDesignLine.setY(Startwith);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			band.addElement(jrDesignLine);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{footerBottomText}.isEmpty()"));
			jrDesignTextField.setExpression(new JRDesignExpression("$P{footerBottomText}"));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(Startwith + 2);
			jrDesignTextField.setHeight(60);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			Startwith = Startwith + 62;
		}
		band.setHeight(Startwith);
		return band;
	}

	private void createTreatmentServices(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(1);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENT}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		if ((Boolean) parameters.get("isEnableTreatmentcost")) {
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentServices(parameters,
					contentFontSize, columnWidth, pageWidth, pageHeight, "$P{services}", normalStyle));
			band = new JRDesignBand();
			band.setHeight(18);
			band.setPrintWhenExpression(
					new JRDesignExpression("!$P{grandTotal}.equals(null) && !$P{grandTotal}.isEmpty()"));
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{grandTotal}"));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(1);
			band.setPrintWhenExpression(
					new JRDesignExpression("!$P{grandTotal}.equals(null) && !$P{grandTotal}.isEmpty()"));

			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setY(0);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
			band.addElement(jrDesignLine);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		} else {
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentPlan(parameters, contentFontSize,
					columnWidth, pageWidth, pageHeight, "$P{services}", normalStyle));
		}

		band = new JRDesignBand();
		band.setHeight(12);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(12);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setBlankWhenNull(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private void createInvoiceSubreport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(1);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{INVOICE}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(
				addInvoice(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));
		band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{grandTotal}.equals(null) && !$P{grandTotal}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{grandTotal}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{paid}.equals(null) && !$P{paid}.isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{paid}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{balance}.equals(null) && !$P{grandTotal}.isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{balance}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(1);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{grandTotal}.equals(null) && !$P{grandTotal}.isEmpty()"));
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(12);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(12);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setBlankWhenNull(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private JRBand addInvoice(Map<String, Object> parameters, Integer contentFontSize, int columnWidth, int pageWidth,
			int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-invoice-subreport.jrxml");
		jasperDesign.setName("INVOICE Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);
		Boolean showInvoiceItemQuantity = (Boolean) parameters.get("showInvoiceItemQuantity"),
				showDiscount = (Boolean) parameters.get("showDiscount"),
				showStatus = (Boolean) parameters.get("showStatus"), showTax = (Boolean) parameters.get("showTax");

		int serviceWidth, quantityWidth = 0, otherFieldsWidth, statusWidth = 0, discountWidth = 0, xSpace = 0,
				taxWidth = 0;

		if (showInvoiceItemQuantity && showDiscount && showStatus && showTax) {
			serviceWidth = (24 * (columnWidth - 30)) / 100;
			quantityWidth = (10 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (11 * (columnWidth - 30)) / 100;
			statusWidth = (14 * (columnWidth - 30)) / 100;
			discountWidth = (13 * (columnWidth - 30)) / 100;
			taxWidth = (8 * (columnWidth - 30)) / 100;
		} else if (showInvoiceItemQuantity && showStatus && showDiscount) {
			serviceWidth = (25 * (columnWidth - 30)) / 100;
			quantityWidth = (12 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (11 * (columnWidth - 30)) / 100;
			statusWidth = (16 * (columnWidth - 30)) / 100;
			discountWidth = (15 * (columnWidth - 30)) / 100;

		} else if (showInvoiceItemQuantity && showDiscount && showTax) {
			serviceWidth = (25 * (columnWidth - 30)) / 100;
			quantityWidth = (14 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			discountWidth = (15 * (columnWidth - 30)) / 100;
			taxWidth = (12 * (columnWidth - 30)) / 100;

		} else if (showInvoiceItemQuantity && showStatus && showTax) {
			serviceWidth = (25 * (columnWidth - 30)) / 100;
			quantityWidth = (14 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			statusWidth = (16 * (columnWidth - 30)) / 100;
			taxWidth = (12 * (columnWidth - 30)) / 100;

		} else if (showDiscount && showStatus && showTax) {
			serviceWidth = (25 * (columnWidth - 30)) / 100;
			discountWidth = (14 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			statusWidth = (16 * (columnWidth - 30)) / 100;
			taxWidth = (12 * (columnWidth - 30)) / 100;

		} else if (showInvoiceItemQuantity && showStatus) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			quantityWidth = (14 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			statusWidth = (18 * (columnWidth - 30)) / 100;
		} else if (showInvoiceItemQuantity && showTax) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			quantityWidth = (14 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			taxWidth = (18 * (columnWidth - 30)) / 100;
		} else if (showInvoiceItemQuantity && showDiscount) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			quantityWidth = (14 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			discountWidth = (16 * (columnWidth - 30)) / 100;
		} else if (showDiscount && showStatus) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (12 * (columnWidth - 30)) / 100;
			statusWidth = (21 * (columnWidth - 30)) / 100;
			discountWidth = (16 * (columnWidth - 30)) / 100;
		} else if (showDiscount && showTax) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (12 * (columnWidth - 30)) / 100;
			taxWidth = (18 * (columnWidth - 30)) / 100;
			discountWidth = (19 * (columnWidth - 30)) / 100;
		} else if (showTax && showStatus) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (12 * (columnWidth - 30)) / 100;
			statusWidth = (19 * (columnWidth - 30)) / 100;
			taxWidth = (18 * (columnWidth - 30)) / 100;
		} else if (showTax) {
			serviceWidth = (35 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			taxWidth = (25 * (columnWidth - 30)) / 100;
		} else if (showStatus) {
			serviceWidth = (35 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			statusWidth = (25 * (columnWidth - 30)) / 100;
		} else if (showDiscount) {
			serviceWidth = (33 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (15 * (columnWidth - 30)) / 100;
			discountWidth = (19 * (columnWidth - 30)) / 100;

		} else if (showInvoiceItemQuantity) {
			serviceWidth = (32 * (columnWidth - 30)) / 100;
			quantityWidth = (17 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (16 * (columnWidth - 30)) / 100;

		} else {
			serviceWidth = (47 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (23 * (columnWidth - 30)) / 100;
		}

		// if(showTreatmentQuantity){
		// serviceWidth = (50*(columnWidth-31))/100; quantityWidth =
		// (50*(columnWidth-31))/100;
		// }else{
		// serviceWidth = columnWidth;
		// }

		band = new JRDesignBand();
		band.setHeight(26);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		xSpace = 35;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Service}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(serviceWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + serviceWidth;

		if (showInvoiceItemQuantity) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Quantity}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(quantityWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + quantityWidth;
		}

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Cost}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + otherFieldsWidth;

		if (showDiscount) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Discount}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(discountWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + discountWidth;
		}
		if (showTax) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Tax}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(taxWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + otherFieldsWidth;
		}
		if (showStatus) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Status}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(statusWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + statusWidth;
		}
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Total}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = 35;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{serviceName}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(serviceWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		xSpace = xSpace + serviceWidth;

		if (showInvoiceItemQuantity) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{quantity}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(quantityWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + quantityWidth;
		}

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{cost}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + otherFieldsWidth;

		if (showDiscount) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{discount}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(discountWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + discountWidth;
		}
		if (showTax) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{tax}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(taxWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + otherFieldsWidth;
		}
		if (showStatus) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{status}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(statusWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + statusWidth;
		}
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{total}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-invoice-subreport.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(
				new JRDesignExpression("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-invoice-subreport.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private JRBand createTreatmentServicesSubreport(Map<String, Object> parameters, Integer contentFontSize,
			int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services.jrxml");
		jasperDesign.setName("Treatment");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		band = new JRDesignBand();
		band.setHeight(1);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENT}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		if ((Boolean) parameters.get("isEnableTreatmentcost")) {
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentServices(parameters,
					contentFontSize, columnWidth, pageWidth, pageHeight, "$F{services}", normalStyle));

			band = new JRDesignBand();
			band.setHeight(18);
			band.setPrintWhenExpression(
					new JRDesignExpression("!$F{grandTotal}.equals(null) && !$F{grandTotal}.isEmpty()"));

			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{grandTotal}"));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(1);
			band.setPrintWhenExpression(
					new JRDesignExpression("!$F{grandTotal}.equals(null) && !$F{grandTotal}.isEmpty()"));
			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setY(0);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
			band.addElement(jrDesignLine);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(12);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(12);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setBlankWhenNull(true);
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		} else {
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentPlan(parameters, contentFontSize,
					columnWidth, pageWidth, pageHeight, "$F{services}", normalStyle));
		}
		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{treatments})"));
		jSubreport.setExpression(
				new JRDesignExpression("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private JRBand addTreatmentServices(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services-subreport.jrxml");
		jasperDesign.setName("TREAMENT Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		Boolean showTreatmentQuantity = (Boolean) parameters.get("showTreatmentQuantity"),
				showTreatmentDiscount = (Boolean) parameters.get("showTreatmentDiscount");

		int serviceWidth, quantityWidth = 0, otherFieldsWidth, statusWidth, discountWidth = 0, xSpace = 0;

		if (showTreatmentDiscount && showTreatmentQuantity) {
			serviceWidth = (30 * (columnWidth - 30)) / 100;
			quantityWidth = (12 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (11 * (columnWidth - 30)) / 100;
			statusWidth = (20 * (columnWidth - 30)) / 100;
			discountWidth = (15 * (columnWidth - 30)) / 100;

		} else if (showTreatmentQuantity) {
			serviceWidth = (33 * (columnWidth - 30)) / 100;
			quantityWidth = (15 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (14 * (columnWidth - 30)) / 100;
			statusWidth = (23 * (columnWidth - 30)) / 100;

		} else if (showTreatmentDiscount) {
			serviceWidth = (33 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (12 * (columnWidth - 30)) / 100;
			statusWidth = (21 * (columnWidth - 30)) / 100;
			discountWidth = (16 * (columnWidth - 30)) / 100;

		} else {
			serviceWidth = (35 * (columnWidth - 30)) / 100;
			otherFieldsWidth = (13 * (columnWidth - 30)) / 100;
			statusWidth = (25 * (columnWidth - 30)) / 100;
		}

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;

		band = new JRDesignBand();
		band.setHeight(26);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		xSpace = 35;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Service}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(serviceWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + serviceWidth;

		if (showTreatmentQuantity) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Quantity}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(quantityWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + quantityWidth;
		}

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Cost}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + otherFieldsWidth;

		if (showTreatmentDiscount) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Discount}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth(discountWidth);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + discountWidth;
		}

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Status}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + statusWidth;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Total}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = 35;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{treatmentServiceName}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(serviceWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		xSpace = xSpace + serviceWidth;

		if (showTreatmentQuantity) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{quantity}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(quantityWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + quantityWidth;
		}

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{cost}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + otherFieldsWidth;

		if (showTreatmentDiscount) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{discount}"));
			jrDesignTextField.setX(xSpace);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(discountWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
			xSpace = xSpace + discountWidth;
		}
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{status}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + statusWidth;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{finalCost}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{note}.equals(null) && !$F{note}.isEmpty()"));
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{note}"));
		jrDesignTextField.setX(35);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services-subreport.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services-subreport.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private JRBand addTreatmentPlan(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services-subreport.jrxml");
		jasperDesign.setName("TREAMENT Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		int xSpace = 0;

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
		jrDesignTextField.setX(5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = 40;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{treatmentServiceName}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth - 40);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{note}.equals(null) && !$F{note}.isEmpty()"));
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{note}"));
		jrDesignTextField.setX(35);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth - 35);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services-subreport.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-treatment-services-subreport.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	@SuppressWarnings("deprecation")
	private void createEyePrescription(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) {

		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{LENSPRESCRIPTION}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{type}"));
		jrDesignTextField
				.setPrintWhenExpression(new JRDesignExpression("!$P{type}.equals(null) && !$P{type}.isEmpty()"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(61);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);

		JRDesignRectangle jrRectangle = new JRDesignRectangle();
		jrRectangle.setX(0);
		jrRectangle.setY(0);
		jrRectangle.setHeight(20);
		jrRectangle.setWidth(61);
		jrRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		band.addElement(jrRectangle);
		band.addElement(jrDesignTextField);

		int noOfFields = (int) parameters.get("noOfFields");
		int dataWidth = (columnWidth - 60) / (2 * noOfFields);

		int titleWidth = dataWidth * noOfFields;
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{RightEye}"));
		jrDesignTextField.setX(61);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(titleWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);

		jrRectangle = new JRDesignRectangle();
		jrRectangle.setX(61);
		jrRectangle.setY(0);
		jrRectangle.setHeight(20);
		jrRectangle.setWidth(titleWidth);
		jrRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		band.addElement(jrRectangle);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{LeftEye}"));
		jrDesignTextField.setX(titleWidth + 61);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(titleWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);

		jrRectangle = new JRDesignRectangle();
		jrRectangle.setX(titleWidth + 61);
		jrRectangle.setY(0);
		jrRectangle.setHeight(20);
		jrRectangle.setWidth(titleWidth);
		jrRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		band.addElement(jrRectangle);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		int fieldXPoint = 61;

		jrRectangle = new JRDesignRectangle();
		jrRectangle.setX(0);
		jrRectangle.setY(0);
		jrRectangle.setHeight(20);
		jrRectangle.setWidth(100);
		jrRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		band.addElement(jrRectangle);

		addEyePrescriptionItem("$P{SPH}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;
		addEyePrescriptionItem("$P{CYL}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{Axis}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		if (noOfFields == 4 || noOfFields == 6) {
			addEyePrescriptionItem("$P{VA}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}
		if (noOfFields > 4) {
			addEyePrescriptionItem("$P{BC}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;

			addEyePrescriptionItem("$P{DIA}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}

		addEyePrescriptionItem("$P{SPH}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{CYL}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{Axis}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		if (noOfFields == 4 || noOfFields == 6) {
			addEyePrescriptionItem("$P{VA}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}
		if (noOfFields > 4) {
			addEyePrescriptionItem("$P{BC}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;

			addEyePrescriptionItem("$P{DIA}", fieldXPoint, dataWidth, true, HorizontalTextAlignEnum.CENTER,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, fieldXPoint, dataWidth);
		}

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		addEyePrescriptionItem("$P{Distance}", 2, 61, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
				contentFontSize, band, titleWidth, 0, 61);

		fieldXPoint = 61;
		addEyePrescriptionItem("$P{rightEyeTest}.getDistanceSPH()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;
		addEyePrescriptionItem("$P{rightEyeTest}.getDistanceCylinder()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{rightEyeTest}.getDistanceAxis()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		if (noOfFields == 4 || noOfFields == 6) {
			addEyePrescriptionItem("$P{rightEyeTest}.getDistanceVA()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}
		if (noOfFields > 4) {
			addEyePrescriptionItem("$P{rightEyeTest}.getDistanceBaseCurve()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;

			addEyePrescriptionItem("$P{rightEyeTest}.getDistanceDiameter()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}

		addEyePrescriptionItem("$P{leftEyeTest}.getDistanceSPH()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;
		addEyePrescriptionItem("$P{leftEyeTest}.getDistanceCylinder()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{leftEyeTest}.getDistanceAxis()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		if (noOfFields == 4 || noOfFields == 6) {
			addEyePrescriptionItem("$P{leftEyeTest}.getDistanceVA()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}
		if (noOfFields > 4) {
			addEyePrescriptionItem("$P{leftEyeTest}.getDistanceBaseCurve()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;

			addEyePrescriptionItem("$P{leftEyeTest}.getDistanceDiameter()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
		}
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		fieldXPoint = 61;
		band = new JRDesignBand();
		band.setHeight(20);
		addEyePrescriptionItem("$P{Near}", 2, fieldXPoint, true, HorizontalTextAlignEnum.LEFT,
				VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 0, fieldXPoint);

		addEyePrescriptionItem("$P{rightEyeTest}.getNearSPH()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{rightEyeTest}.getNearCylinder()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{rightEyeTest}.getNearAxis()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		if (noOfFields == 4 || noOfFields == 6) {
			addEyePrescriptionItem("$P{rightEyeTest}.getNearVA()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}
		if (noOfFields > 4) {
			addEyePrescriptionItem("$P{rightEyeTest}.getNearBaseCurve()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;

			addEyePrescriptionItem("$P{rightEyeTest}.getNearDiameter()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}

		addEyePrescriptionItem("$P{leftEyeTest}.getNearSPH()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{leftEyeTest}.getNearCylinder()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		addEyePrescriptionItem("$P{leftEyeTest}.getNearAxis()", fieldXPoint, dataWidth, false,
				HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
				fieldXPoint, dataWidth);
		fieldXPoint = fieldXPoint + dataWidth;

		if (noOfFields == 4 || noOfFields == 6) {
			addEyePrescriptionItem("$P{leftEyeTest}.getNearVA()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;
		}
		if (noOfFields > 4) {
			addEyePrescriptionItem("$P{leftEyeTest}.getNearBaseCurve()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
			fieldXPoint = fieldXPoint + dataWidth;

			addEyePrescriptionItem("$P{leftEyeTest}.getNearDiameter()", fieldXPoint, dataWidth, false,
					HorizontalTextAlignEnum.CENTER, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth,
					fieldXPoint, dataWidth);
		}

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		if (parameters.get("pupilaryDistance") != null) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{PupilaryDistance}", 2, 125, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{pupilaryDistance}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		if (parameters.get("lensType") != null && !parameters.get("lensType").toString().isEmpty()) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{LensType}", 2, 125, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{lensType}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		if (parameters.get("usage") != null && !parameters.get("usage").toString().isEmpty()) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Usage}", 2, 125, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{usage}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		if (parameters.get("replacementInterval") != null) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{ReplacementInterval}", 2, 125, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{replacementInterval}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		if (parameters.get("lensColor") != null && !parameters.get("lensColor").toString().isEmpty()) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{LensColor}", 2, 125, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{lensColor}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		if (parameters.get("lensBrand") != null && !parameters.get("lensBrand").toString().isEmpty()) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{LensBrand}", 2, 125, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{lensBrand}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		if (parameters.get("quality") != null && !parameters.get("quality").toString().isEmpty()) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Quality}", 2, 125, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{quality}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		if (parameters.get("remarks") != null && !parameters.get("remarks").toString().isEmpty()) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Remarks}", 2, 125, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, titleWidth, 0, 125);
			addEyePrescription("$P{remarks}", 127, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, titleWidth, 125,
					titleWidth + titleWidth + 61 - 125);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		if (parameters.get("pupilaryDistance") != null && parameters.get("lensType") != null
				&& !parameters.get("lensType").toString().isEmpty() && parameters.get("usage") != null
				&& !parameters.get("usage").toString().isEmpty() && parameters.get("remarks") != null
				&& !parameters.get("remarks").toString().isEmpty() && parameters.get("quality") != null
				&& !parameters.get("quality").toString().isEmpty() && parameters.get("lensColor") != null
				&& !parameters.get("lensColor").toString().isEmpty() && parameters.get("lensBrand") != null
				&& !parameters.get("lensBrand").toString().isEmpty())
			((JRDesignSection) jasperDesign.getDetailSection())
					.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
	}

	@SuppressWarnings("deprecation")
	private void addEyePrescriptionItem(String value, int xPoint, int dataWidth, boolean isBold,
			HorizontalTextAlignEnum horzontalAlignEnum, VerticalTextAlignEnum verticalAlignEnum, int titleFontSize,
			JRDesignBand band, int titleWidth, int rectangleXPoint, int rectangleDataWidth) {
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(xPoint);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(dataWidth);
		jrDesignTextField.setBold(isBold);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(verticalAlignEnum);
		jrDesignTextField.setHorizontalTextAlign(horzontalAlignEnum);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);

		JRDesignRectangle jrRectangle = new JRDesignRectangle();
		jrRectangle.setX(rectangleXPoint);
		jrRectangle.setY(0);
		jrRectangle.setHeight(20);
		jrRectangle.setWidth(rectangleDataWidth);
		jrRectangle.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
		band.addElement(jrRectangle);
		band.addElement(jrDesignTextField);
	}

	@SuppressWarnings("deprecation")
	private void addEyePrescription(String value, int xPoint, int dataWidth, boolean isBold,
			HorizontalTextAlignEnum horzontalAlignEnum, VerticalTextAlignEnum verticalAlignEnum, int titleFontSize,
			JRDesignBand band, int titleWidth, int rectangleXPoint, int rectangleDataWidth) {
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(xPoint);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(dataWidth);
		jrDesignTextField.setBold(isBold);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(verticalAlignEnum);
		jrDesignTextField.setHorizontalTextAlign(horzontalAlignEnum);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);

		band.addElement(jrDesignTextField);
	}

	private void createReceipt(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		band = new JRDesignBand();
		band.setHeight(1);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{RECEIPT}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{content}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{paid}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private void createLabRequisationForm(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 2));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{date}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabReportsTest(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));

	}

	private JRDesignBand addLabReportsTest(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-subreport.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{from}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{doctor}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabTest(parameters, contentFontSize, columnWidth,
				pageWidth, pageHeight, "$F{details}", normalStyle));

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;

	}

	private JRDesignBand addDentalLabWork(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-subreport.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{from}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{doctor}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabTest(parameters, contentFontSize, columnWidth,
				pageWidth, pageHeight, "$F{details}", normalStyle));

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-requisation-form-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;

	}

	private JRDesignBand addLabTest(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String items, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-test-subreport.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;

		band = new JRDesignBand();
		band.setHeight(26);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(10);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(40);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PatientName}"));
		jrDesignTextField.setX(51);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Gender}"));
		jrDesignTextField.setX(201);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Test}"));
		jrDesignTextField.setX(351);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.SMALL.name()))
			band.setHeight(22);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.MEDIUM.name()))
			band.setHeight(27);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.LARGE.name()))
			band.setHeight(32);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
		jrDesignTextField.setX(10);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(40);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{patientName}"));
		jrDesignTextField.setX(51);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{gender}"));
		jrDesignTextField.setX(201);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{test}"));
		jrDesignTextField.setX(351);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(13);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-test-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + items + ")"));

		jSubreport.setExpression(
				new JRDesignExpression("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-lab-test-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;

	}

	private void createConsentForm(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{item}.getTitle()"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 2));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(30);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{personalDetail}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(30);
		jrDesignTextField.setWidth(251);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		Boolean show = (Boolean) parameters.get("showPID");
		if (show) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{item}.getPID()"));
			jrDesignTextField.setX(columnWidth - 120);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(30);
			jrDesignTextField.setWidth(120);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
			band.addElement(jrDesignTextField);
		}
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		show = (Boolean) parameters.get("showName");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Name}", 2, 100, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getName()", 102, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		show = (Boolean) parameters.get("showGender");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Gender}", 2, 100, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getGender()", 102, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		show = (Boolean) parameters.get("showDOB");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{BirthDate}", 2, 100, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getBirthDate()", 102, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Age}", 2, 100, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getAge()", 102, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		show = (Boolean) parameters.get("showBloodGroup");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{BloodGroup}", 2, 100, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getBloodGroup()", 102, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		band = new JRDesignBand();
		band.setHeight(30);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{contactDetail}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(30);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		show = (Boolean) parameters.get("showMbno");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{MobileNumber}", 2, 100, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getMobileNumber()", 102, columnWidth - 134, false,
					HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		show = (Boolean) parameters.get("showEmail");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{EmailAddress}", 2, 100, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getEmailAddress()", 102, columnWidth - 134, false,
					HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		show = (Boolean) parameters.get("showLandLineNo");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{LandLineNumber}", 2, 100, true, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getLandLineNumber()", 102, columnWidth - 134, false,
					HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		show = (Boolean) parameters.get("showAddress");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(20);
			addEyePrescription("$P{Address}", 2, 100, true, HorizontalTextAlignEnum.LEFT, VerticalTextAlignEnum.MIDDLE,
					contentFontSize, band, 125, 0, 125);
			addEyePrescription("$P{item}.getAddress()", 102, columnWidth - 134, false, HorizontalTextAlignEnum.LEFT,
					VerticalTextAlignEnum.MIDDLE, contentFontSize, band, 340, 125, 0);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		show = (Boolean) parameters.get("showMedicalHistory");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(30);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{medicalHistory}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(30);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{item}.getMedicalHistory()"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(20);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setBold(false);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}
		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		show = (Boolean) parameters.get("showDeclaration");
		if (show) {
			band = new JRDesignBand();
			band.setHeight(30);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{item}.getDeclaration()"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(columnWidth);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setBold(false);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		}

		show = (Boolean) parameters.get("showSignDate");
		band = new JRDesignBand();
		band.setHeight(50);
		if (show) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{item}.getDateOfSign()"));
			jrDesignTextField.setX(50);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(50);
			jrDesignTextField.setWidth(170);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.BOTTOM);
			jrDesignTextField.setBold(false);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
			band.addElement(jrDesignTextField);
		}

		show = (Boolean) parameters.get("showSignImage");
		if (show) {
			JRDesignImage jrDesignImage = new JRDesignImage(null);
			jrDesignImage.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
			expression = new JRDesignExpression();
			expression.setText("$P{item}.getSignImageUrl()");
			jrDesignImage.setExpression(expression);
			jrDesignImage.setX(250);
			jrDesignImage.setY(0);
			jrDesignImage.setHeight(50);
			jrDesignImage.setWidth(220);
			jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
			band.addElement(jrDesignImage);
		}
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{signDate}"));
		jrDesignTextField.setX(50);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(170);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{signature}"));
		jrDesignTextField.setX(250);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private void createDischargeSummary(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression("(!$P{dOA}.equals( null )  || !$P{dOD}.equals( null ))"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField
				.setPrintWhenExpression(new JRDesignExpression("(!$P{dOA}.equals( null ) && !$P{dOA}.isEmpty())"));
		jrDesignTextField.setExpression(new JRDesignExpression("$P{dOA}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(175);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField
				.setPrintWhenExpression(new JRDesignExpression("(!$P{dOD}.equals( null ) && !$P{dOD}.isEmpty())"));
		jrDesignTextField.setExpression(new JRDesignExpression("$P{dOD}"));
		jrDesignTextField.setX(177);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth - 175);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{operationDate}.equals( null ) && !$P{operationDate}.isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{operationDate}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression(
				"!$P{timeOfEntryAndExitFromOT}.equals( null ) && !$P{timeOfEntryAndExitFromOT}.isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{timeOfEntryAndExitFromOT}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 145;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		if (parameters.get("vitalSigns") != null) {
			addItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$P{vitalSigns}", fieldWidth, false, 0, false);
		}

		addItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$P{diagnosis}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PastHistoryTitle}", "$P{pastHistory}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{FamilyHistoryTitle}", "$P{familyHistory}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PersonalHistoryTitle}", "$P{pesonalHistory}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{Complaints}", "$P{complaints}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$P{presentComplaints}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$P{historyOfPresentComplaints}", fieldWidth,
				false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$P{menstrualHistory}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$P{obstetricHistory}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{SystemExam}", "$P{systemExam}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$P{generalExam}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Observations}", "$P{observations}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Investigations}", "$P{investigations}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{BloodLoss}", "$P{bloodLoss}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{IndicationOfUSG}", "$P{indicationOfUSG}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PA}", "$P{pa}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PS}", "$P{ps}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{PV}", "$P{pv}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{LabourNotes}", "$P{labourNotes}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{BabyNotes}", "$P{babyNotes}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{BabyWeight}", "$P{babyWeight}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{EcgDetails}", "$P{ecgDetails}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Echo}", "$P{echo}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{XRayDetails}", "$P{xRayDetails}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Holter}", "$P{holter}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{ProcedureNote}", "$P{procedureNote}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{OperationNotes}", "$P{operationNotes}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Condition}", "$P{condition}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{TreatmentGiven}", "$P{treatmentGiven}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{OperationName}", "$P{operationName}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{LMP}", "$P{lmp}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{EDD}", "$P{edd}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{SurgeryDate}", "$P{surgeryDate}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{SurgeryNotes}", "$P{surgeryNotes}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{TreatingConsultant}", "$P{treatingConsultant}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{Ansthesia}", "$P{ansthesia}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Consultant}", "$P{consultant}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Assistant}", "$P{assistant}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Pediatrician}", "$P{pediatrician}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{AdviceForMother}", "$P{adviceForMother}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{AdviceForBaby}", "$P{adviceForBaby}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Surgeon}", "$P{surgeon}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Anesthetist}", "$P{anesthetist}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Implant}", "$P{implant}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Cement}", "$P{cement}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Summary}", "$P{summary}", fieldWidth, false, 0, false);

		if (parameters.get("prescriptionItems") != null) {
			addDischargeitems(jasperDesign, columnWidth, "$P{PRESCRIPTION}", 18, contentFontSize - 1, true);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDrugs(parameters, contentFontSize - 1,
					columnWidth, pageWidth, pageHeight, "$P{prescriptionItems}", normalStyle));
		}
		addItems(jasperDesign, columnWidth, "$P{Advice}", "$P{advice}", fieldWidth, false, 0, false);

		band = new JRDesignBand();
		band.setHeight(20);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$P{flowsheet}.equals(null) && !$P{flowsheet}.isEmpty()"));
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{FlowSheetTitle}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(3);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addFlowSheet(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{flowsheet}", normalStyle));
	}

	private void createAdmitCard(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {

		Boolean show = false;

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		show = (Boolean) parameters.get("showDOA");
		band = new JRDesignBand();
		band.setHeight(18);
		if (show) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{dOA}"));
			jrDesignTextField.setX(1);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(175);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setBold(false);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
			band.addElement(jrDesignTextField);

		}

		show = (Boolean) parameters.get("showDOD");

		if (show) {

			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{dOD}"));
			jrDesignTextField.setX(177);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(columnWidth - 175);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
			jrDesignTextField.setBold(false);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
			band.addElement(jrDesignTextField);

		}
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		show = (Boolean) parameters.get("showOD");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{OperationDate}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{operationdate}", 18, contentFontSize - 1, false);
		}

		show = (Boolean) parameters.get("showNOfOp");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{NatureOfOperation}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{natureOfOperation}", 18, contentFontSize - 1, false);
		}

		show = (Boolean) parameters.get("showPH");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{PastHistoryTitle}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{pastHistory}", 18, contentFontSize - 1, false);
		}
		show = (Boolean) parameters.get("showFH");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{FamilyHistoryTitle}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{familyHistory}", 18, contentFontSize - 1, false);
		}
		show = (Boolean) parameters.get("showPersonalHistory");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{PersonalHistoryTitle}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{personalHistory}", 18, contentFontSize - 1, false);
		}
		show = (Boolean) parameters.get("showcompl");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{Complaints}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{complaints}", 18, contentFontSize - 1, false);
		}

		show = (Boolean) parameters.get("showEx");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{Examination}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{examination}", 18, contentFontSize - 1, false);
		}

		show = (Boolean) parameters.get("showJINV");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{JointInvolvement}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{jointInvolvement}", 18, contentFontSize - 1, false);
		}

		show = (Boolean) parameters.get("showXD");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{XRayDetails}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{xRayDetails}", 18, contentFontSize - 1, false);
		}
		show = (Boolean) parameters.get("showDiagnosis");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{Diagnosis}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{diagnosis}", 18, contentFontSize - 1, false);
		}

		show = (Boolean) parameters.get("showTP");
		if (show) {
			addDischargeitems(jasperDesign, columnWidth, "$P{TreatmentPlan}", 18, contentFontSize - 1, true);
			addDischargeitems(jasperDesign, columnWidth, "$P{treatmentPlan}", 18, contentFontSize - 1, false);
		}

	}

	private void addDischargeitems(JasperDesign jasperDesign, int columnWidth, String value, int height,
			Integer contentFontSize, boolean isBold) {
		band = new JRDesignBand();
		band.setHeight(height);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression(value));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(height);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(isBold);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void createOTReports(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) {
		int fieldWidth = 123;
		if (contentFontSize > 13)
			fieldWidth = 150;
		else if (contentFontSize > 11)
			fieldWidth = 133;

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{OTREPORTS}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{OperationDate}", "$P{operationDate}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{AnaesthesiaType}", "$P{anaesthesiaType}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{DateAndTimeOfSurgery}", "$P{dateAndTimeOfSurgery}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{DurationOfSurgery}", "$P{durationOfSurgery}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$P{provisionalDiagnosis}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{SurgeryTitle}", "$P{surgeryTitle}", fieldWidth, false,
				0, false);
		addItems(jasperDesign, columnWidth, "$P{OperationalNotes}", "$P{operationalNotes}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{FinalDiagnosis}", "$P{finalDiagnosis}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{OperatingSurgeon}", "$P{operatingSurgeon}", fieldWidth, false, 0,
				false);
		addItems(jasperDesign, columnWidth, "$P{Anaesthetist}", "$P{anaesthetist}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{AssistingDoctor}", "$P{assistingDoctor}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{AssistingNurse}", "$P{assistingNurse}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{MaterialForHPE}", "$P{materialForHPE}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Remarks}", "$P{remarks}", fieldWidth, false, 0, false);

	}

	private void createDeliveryReports(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) {
		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 145;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{DELIVERYREPORTS}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{DeliveryDate}", "$P{deliveryDate}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{DeliveryTime}", "$P{deliveryTime}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{BabyGender}", "$P{babyGender}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{DeliveryType}", "$P{deliveryType}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{FormNo}", "$P{formNo}", fieldWidth, false, 0, false);
		addItems(jasperDesign, columnWidth, "$P{Remarks}", "$P{remarks}", fieldWidth, false, 0, false);
	}

	private void createDentalWorkReport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{date}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{doctor}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{dentalLab}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{uniqueWorkId}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{eta}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int patientWidth = (18 * (columnWidth)) / 100;
		int toothNoWidth = (20 * (columnWidth) / 100);
		int workWidth = (20 * (columnWidth) / 100);
		int shadeWidth = (12 * (columnWidth) / 100);
		int materialWidth = (16 * (columnWidth) / 100);
		int statusWidth = (14 * (columnWidth) / 100);

		band = new JRDesignBand();
		band.setHeight(26);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PatientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(patientWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{ToothNo}"));
		jrDesignTextField.setX(patientWidth + 2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(toothNoWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Work}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(workWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Shade}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(shadeWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Material}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth + shadeWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(materialWidth - 3);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Status}"));
		jrDesignTextField.setX(patientWidth + 8 + toothNoWidth + shadeWidth + materialWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.SMALL.name()))
			band.setHeight(22);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.MEDIUM.name()))
			band.setHeight(27);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.LARGE.name()))
			band.setHeight(32);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{patientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(patientWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{toothNumbers}"));
		jrDesignTextField.setX(patientWidth + 2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(toothNoWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{dentalWork}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(workWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{shade}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(shadeWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{material}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + shadeWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(materialWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{status}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + shadeWidth + materialWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(15);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(15);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int fieldWidth = 250;

		addItem(jasperDesign, columnWidth, "$P{collarAndMetalDesign}", fieldWidth, true, 0, false);
		addItem(jasperDesign, columnWidth, "$P{occlusalStaining}", fieldWidth, true, 0, false);
		addItem(jasperDesign, columnWidth, "$P{instructions}", fieldWidth, true, 0, false);
		addItem(jasperDesign, columnWidth, "$P{ponticDesign}", fieldWidth, true, 0, false);
		addItem(jasperDesign, columnWidth, "$P{gender}", fieldWidth, true, 0, false);
		addItem(jasperDesign, columnWidth, "$P{age}", fieldWidth, true, 0, false);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addInspectionReportsItem(parameters,
				contentFontSize, columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));

		/*
		 * ((JRDesignSection)
		 * jasperDesign.getDetailSection()).addBand(addDentalWorkCardValue( parameters,
		 * contentFontSize, columnWidth, pageWidth, pageHeight, "$P{cardValues}",
		 * normalStyle));
		 */

		band = new JRDesignBand();
		int Startwith = 2;

		band.setSplitType(SplitTypeEnum.IMMEDIATE);
		if (!DPDoctorUtils.anyStringEmpty(parameters.get("poweredBy").toString())) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{poweredBy}.isEmpty()"));
			jrDesignTextField.setExpression(new JRDesignExpression("$P{poweredBy}"));
			jrDesignTextField.setFontSize(new Float(9));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(Startwith);
			jrDesignTextField.setHeight(22);
			jrDesignTextField.setWidth(175);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}
		band.setHeight(25);

		jasperDesign.setPageFooter(band);

	}

	private void createPatientCardPrint(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(10);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{patientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{patientId}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(10);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{mobileNumber}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{requestId}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(10);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{age}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{gender}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(10);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{fromDate}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(9);
		jrDesignTextField.setWidth(450);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);

		/*
		 * jrDesignTextField = new JRDesignTextField();
		 * jrDesignTextField.setExpression(new JRDesignExpression("$P{toDate}"));
		 * jrDesignTextField.setX(251); jrDesignTextField.setY(0);
		 * jrDesignTextField.setHeight(18); jrDesignTextField.setWidth(250);
		 * jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum. RIGHT);
		 * jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		 * jrDesignTextField.setBold(false);
		 * jrDesignTextField.setStretchWithOverflow(true);
		 * jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		 * jrDesignTextField.setMarkup("html"); band.addElement(jrDesignTextField);
		 */
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(30);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{generalNotes}.isEmpty()"));
		jrDesignTextField.setExpression(new JRDesignExpression("$P{generalNotes}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(5);
		jrDesignTextField.setHeight(25);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		int Startwith = 2;
		band.setSplitType(SplitTypeEnum.IMMEDIATE);
		if (!DPDoctorUtils.anyStringEmpty(parameters.get("poweredBy").toString())) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{poweredBy}.isEmpty()"));
			jrDesignTextField.setExpression(new JRDesignExpression("$P{poweredBy}"));
			jrDesignTextField.setFontSize(new Float(9));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(Startwith);
			jrDesignTextField.setHeight(22);
			jrDesignTextField.setWidth(175);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}

		band.setHeight(25);
		jasperDesign.setPageFooter(band);

	}

	private void createPatientCertificate(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) {
		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{htmlText}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private void createDentalInspectionReport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 2));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(30);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{date}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(10);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{requestId}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(10);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(245);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setPrintWhenExpression(
				new JRDesignExpression("!$P{requestId}.equals(null) && !$P{requestId}.isEmpty() "));
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{doctor}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{dentalLab}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(245);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int patientWidth = (18 * (columnWidth)) / 100;
		int toothNoWidth = (20 * (columnWidth) / 100);
		int workWidth = (20 * (columnWidth) / 100);
		int shadeWidth = (12 * (columnWidth) / 100);
		int materialWidth = (16 * (columnWidth) / 100);
		int statusWidth = (14 * (columnWidth) / 100);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PatientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(patientWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{ToothNo}"));
		jrDesignTextField.setX(patientWidth + 2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(toothNoWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Work}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(workWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Shade}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth + workWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(shadeWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Material}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth + shadeWidth + workWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(materialWidth - 3);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Status}"));
		jrDesignTextField.setX(patientWidth + 8 + toothNoWidth + shadeWidth + materialWidth + workWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(18);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(22);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{patientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(patientWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{toothNumbers}"));
		jrDesignTextField.setX(patientWidth + 2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(toothNoWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{dentalWork}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(workWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{shade}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(shadeWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{material}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + shadeWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(materialWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{status}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + shadeWidth + materialWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(2);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int copingWidth, bisqueWidth, finalWidth;
		copingWidth = (35 * (columnWidth - 40)) / 100;
		bisqueWidth = (35 * (columnWidth - 40)) / 100;
		finalWidth = (35 * (columnWidth - 40)) / 100;

		band = new JRDesignBand();
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{copingStage}"));
		jrDesignTextField.setX(20);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(copingWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{bisqueStage}"));
		jrDesignTextField.setX(copingWidth + 20);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(bisqueWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{finalStage}"));
		jrDesignTextField.setX(copingWidth + bisqueWidth + 20);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(finalWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addInspectionReportsItem(parameters,
				contentFontSize, columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		if (!DPDoctorUtils.anyStringEmpty(parameters.get("poweredBy").toString())) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{poweredBy}.isEmpty()"));
			jrDesignTextField.setExpression(new JRDesignExpression("$P{poweredBy}"));
			jrDesignTextField.setFontSize(new Float(13));
			jrDesignTextField.setX(0);
			jrDesignTextField.setY(2);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(175);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}
		band.setHeight(20);
		jasperDesign.setPageFooter(band);

	}

	private JRDesignBand addInspectionReportsItem(Map<String, Object> parameters, Integer contentFontSize,
			int columnWidth, int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle)
			throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-inspection-reports-subreport.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);
		jasperDesign.addStyle(normalStyle);

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(20);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth - 40);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Process}"));
		jrDesignTextField.setX(22);
		jrDesignTextField.setY(1);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(175);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{InspectedBy}"));
		jrDesignTextField.setX(197);
		jrDesignTextField.setY(1);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Date}"));
		jrDesignTextField.setX(347);
		jrDesignTextField.setY(1);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(135);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(20);
		jrDesignLine.setY(17);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth - 40);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{process}"));
		jrDesignTextField.setX(22);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(175);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{inspectedBy}"));
		jrDesignTextField.setX(197);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(150);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{date}"));
		jrDesignTextField.setX(347);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(135);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(5);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(20);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth - 40);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-inspection-reports-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-inspection-reports-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;

	}

	private void createMultipleReceipt(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {
		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENTRECOMMENDED}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		if (parameters.get("receipts") != null) {
			JasperDesign jasperDesignForSubreport = JRXmlLoader
					.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-receipts_subreport.jrxml");
			jasperDesignForSubreport.setName("Receipts");
			jasperDesignForSubreport.setPageWidth(pageWidth);
			jasperDesignForSubreport.setPageHeight(pageHeight);
			jasperDesignForSubreport.setColumnWidth(columnWidth);
			jasperDesignForSubreport.setColumnSpacing(0);
			jasperDesignForSubreport.setBottomMargin(0);
			jasperDesignForSubreport.setLeftMargin(0);
			jasperDesignForSubreport.setRightMargin(0);
			jasperDesignForSubreport.setTopMargin(0);

			jasperDesignForSubreport.addStyle(normalStyle);

			band = new JRDesignBand();
			band.setHeight(26);

			Integer titleFontSize = contentFontSize;
			if (contentFontSize > 13)
				titleFontSize = 13;

			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setY(0);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
			band.addElement(jrDesignLine);

			int xPoint = 1;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Date}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth((15 * (columnWidth)) / 100);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (15 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Procedure}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth((47 * (columnWidth)) / 100);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (47 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Total}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth((11 * (columnWidth)) / 100);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (11 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Paid}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth((15 * (columnWidth)) / 100);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (15 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Balance}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(4);
			jrDesignTextField.setHeight(15);
			jrDesignTextField.setWidth((12 * (columnWidth)) / 100);
			jrDesignTextField.setBold(true);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setY(22);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
			band.addElement(jrDesignLine);

			jasperDesignForSubreport.setColumnHeader(band);

			band = new JRDesignBand();
			band.setSplitType(SplitTypeEnum.STRETCH);
			band.setHeight(22);

			xPoint = 1;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{date}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((15 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (15 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{procedure}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((47 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (47 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{total}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((11 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (11 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{paid}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((15 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (15 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{balance}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((15 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			((JRDesignSection) jasperDesignForSubreport.getDetailSection()).addBand(band);

			JasperCompileManager.compileReportToFile(jasperDesignForSubreport,
					JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-receipts_subreport.jasper");
			JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesignForSubreport);
			jSubreport.setUsingCache(false);
			jSubreport.setRemoveLineWhenBlank(true);
			jSubreport.setPrintRepeatedValues(false);
			jSubreport.setWidth(columnWidth);
			jSubreport.setHeight(0);
			jSubreport.setX(0);
			jSubreport.setY(0);

			jSubreport.setDataSourceExpression(new JRDesignExpression(
					"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{receipts})"));

			jSubreport.setExpression(new JRDesignExpression(
					"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-receipts_subreport.jasper\""));
			JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
			designSubreportParameter.setName("REPORT_CONNECTION");
			designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
			band = new JRDesignBand();
			band.setHeight(0);
			band.addElement(jSubreport);

			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(20);

			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setY(0);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			band.addElement(jrDesignLine);

			xPoint = 1 + (51 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{Total}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(2);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((11 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setBold(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + ((11 * (columnWidth)) / 100);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{grandTotal}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(2);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((11 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (11 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{totalPaid}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(2);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((15 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			xPoint = xPoint + (15 * (columnWidth)) / 100;
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$P{totalBalance}"));
			jrDesignTextField.setX(xPoint);
			jrDesignTextField.setY(2);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth((12 * (columnWidth)) / 100);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

			band = new JRDesignBand();
			band.setHeight(2);
			jrDesignLine = new JRDesignLine();
			jrDesignLine.setX(0);
			jrDesignLine.setY(0);
			jrDesignLine.setHeight(1);
			jrDesignLine.setWidth(columnWidth);
			band.addElement(jrDesignLine);

			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		}
	}

	private void createCalenderAppointment(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Title}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 2));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		band = new JRDesignBand();
		band.setHeight(30);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{date}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(10);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addCalenderAppointments(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));

	}

	private JRDesignBand addCalenderAppointments(Map<String, Object> parameters, Integer contentFontSize,
			int columnWidth, int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle)
			throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-calender-appointments-subreport.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;

		band = new JRDesignBand();
		band.setHeight(40);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{doctor}"));
		jrDesignTextField.setX(10);
		jrDesignTextField.setY(15);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(titleFontSize));

		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(37);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addAppointmentItem(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$F{calenders}", normalStyle));

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-calender-appointments-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-calender-appointments-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;

	}

	private JRDesignBand addAppointmentItem(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-appointment-items.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		Boolean showMobileNo = (Boolean) parameters.get("showMobileNo") != null
				? (Boolean) parameters.get("showMobileNo")
				: false;
		Boolean showStatus = (Boolean) parameters.get("showStatus") != null ? (Boolean) parameters.get("showStatus")
				: false;
		Boolean showNotes = (Boolean) parameters.get("showNotes") != null ? (Boolean) parameters.get("showNotes")
				: false;
		Boolean showGroups = (Boolean) parameters.get("showGroups") != null ? (Boolean) parameters.get("showGroups")
				: false;

		int timeWidth = 0, nameWidth = 0, mNoWidth = 0, statusWidth = 0, groupWidth = 0;

		if (showMobileNo && showStatus && showGroups) {
			timeWidth = 120;
			nameWidth = 130;
			mNoWidth = 90;
			statusWidth = 80;
			groupWidth = 80;
		} else if (showMobileNo && showStatus) {
			timeWidth = 130;
			nameWidth = 170;
			mNoWidth = 100;
			statusWidth = 120;
		} else if (showMobileNo && showGroups) {
			timeWidth = 120;
			nameWidth = 130;
			mNoWidth = 100;
			groupWidth = 180;
		} else if (showStatus && showGroups) {
			timeWidth = 120;
			nameWidth = 130;
			statusWidth = 120;
			groupWidth = 160;
		} else if (showGroups) {
			timeWidth = 140;
			nameWidth = 160;
			groupWidth = 230;
		} else if (showStatus) {
			timeWidth = 140;
			nameWidth = 150;
			statusWidth = 120;
		} else if (showMobileNo) {
			timeWidth = 140;
			nameWidth = 160;
			mNoWidth = 100;
		} else {
			timeWidth = 230;
			nameWidth = 230;

		}

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(23);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{timing}"));
		jrDesignTextField
				.setPrintWhenExpression(new JRDesignExpression("!$F{timing}.equals(null) && !$F{timing}.isEmpty() "));
		jrDesignTextField.setX(5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(timeWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);
		timeWidth = timeWidth + 5;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{patientName}"));
		jrDesignTextField.setPrintWhenExpression(
				new JRDesignExpression("!$F{patientName}.equals(null) && !$F{patientName}.isEmpty() "));
		jrDesignTextField.setX(timeWidth + 2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(nameWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);
		timeWidth = timeWidth + nameWidth + 2;

		if (showMobileNo) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{mobileNumber}"));
			jrDesignTextField.setPrintWhenExpression(
					new JRDesignExpression("!$F{mobileNumber}.equals(null) && !$F{mobileNumber}.isEmpty() "));
			jrDesignTextField.setX(2 + timeWidth);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(mNoWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);
			timeWidth = timeWidth + mNoWidth + 2;
		}

		if (showGroups) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{groupName}"));
			jrDesignTextField.setPrintWhenExpression(
					new JRDesignExpression("!$F{groupName}.equals(null) && !$F{groupName}.isEmpty() "));
			jrDesignTextField.setX(timeWidth);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(groupWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);
			timeWidth = timeWidth + groupWidth;
		}
		if (showStatus) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{status}"));
			jrDesignTextField.setPrintWhenExpression(
					new JRDesignExpression("!$F{status}.equals(null) && !$F{status}.isEmpty() "));
			jrDesignTextField.setX(timeWidth);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(statusWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);

		}
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		if (showNotes) {
			band = new JRDesignBand();
			band.setHeight(20);
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{notes}"));
			jrDesignTextField
					.setPrintWhenExpression(new JRDesignExpression("!$F{notes}.equals(null) && !$F{notes}.isEmpty() "));
			jrDesignTextField.setX(timeWidth);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(columnWidth - 20);
			jrDesignTextField.setStretchWithOverflow(true);
			jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
			jrDesignTextField.setMarkup("html");
			jrDesignTextField.setFontSize(new Float(titleFontSize));
			band.addElement(jrDesignTextField);
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		}

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-appointment-items.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(
				new JRDesignExpression("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-appointment-items.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;

	}

	private void createFlowSheet(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize,
			int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(20);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{FlowSheetTitle}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(5);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addFlowSheet(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{flowsheet}", normalStyle));

	}

	private JRDesignBand addFlowSheet(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-flow-sheet-subreport.jrxml");
		jasperDesign.setName("Flow Sheet");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		jasperDesign.addStyle(normalStyle);

		band = new JRDesignBand();
		band.setHeight(26);

		Integer titleFontSize = contentFontSize;
		if (contentFontSize > 13)
			titleFontSize = 13;
		int width = 0;
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(5);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);
		width = 40;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Date}"));
		jrDesignTextField.setX(width + 5);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(110);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);
		width = width + 115;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Examination}"));
		jrDesignTextField.setX(width);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(columnWidth - width);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(titleFontSize));
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.SMALL.name()))
			band.setHeight(22);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.MEDIUM.name()))
			band.setHeight(27);
		else if (parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.LARGE.name()))
			band.setHeight(32);
		width = 0;
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
		jrDesignTextField.setX(5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		width = 40;
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{date}"));
		jrDesignTextField.setX(width + 5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(110);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		width = width + 115;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{examination}"));
		jrDesignTextField.setPrintWhenExpression(
				new JRDesignExpression("!$F{examination}.equals(null) && !$F{examination}.isEmpty() "));
		jrDesignTextField.setX(width + 10);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth - width);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{complaint}.equals(null) && !$F{complaint}.isEmpty() "));
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(
				new JRDesignExpression("!$F{complaint}.equals(null) && !$F{complaint}.isEmpty() "));
		jrDesignTextField.setExpression(new JRDesignExpression("$F{complaint}"));
		jrDesignTextField.setX(40);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{advice}.equals(null) && !$F{advice}.isEmpty() "));
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField
				.setPrintWhenExpression(new JRDesignExpression("!$F{advice}.equals(null) && !$F{advice}.isEmpty() "));
		jrDesignTextField.setExpression(new JRDesignExpression("$F{advice}"));
		jrDesignTextField.setX(40);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(13);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(12);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setBlankWhenNull(true);
		band.addElement(jrDesignTextField);
		jasperDesign.setColumnFooter(band);
		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-flow-sheet-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(
				new JRDesignExpression("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-flow-sheet-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private void createMultipleInspectionReport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDentallabInspectionReports(parameters,
				contentFontSize, columnWidth, pageWidth, pageHeight, "$P{details}", normalStyle));

	}

	private JRDesignBand addDentallabInspectionReports(Map<String, Object> parameters, Integer contentFontSize,
			int columnWidth, int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle)
			throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-inspection-subreport.jrxml");
		jasperDesign.setName("Lab Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);
		jasperDesign.addStyle(normalStyle);
		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{date}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{requestId}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(245);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setPrintWhenExpression(
				new JRDesignExpression("!$F{requestId}.equals(null) && !$F{requestId}.isEmpty() "));
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{doctor}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{dentalLab}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(245);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int patientWidth = (18 * (columnWidth)) / 100;
		int toothNoWidth = (20 * (columnWidth) / 100);
		int workWidth = (20 * (columnWidth) / 100);
		int shadeWidth = (12 * (columnWidth) / 100);
		int materialWidth = (16 * (columnWidth) / 100);
		int statusWidth = (14 * (columnWidth) / 100);

		band = new JRDesignBand();
		band.setHeight(18);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PatientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(patientWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{ToothNo}"));
		jrDesignTextField.setX(patientWidth + 2);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(toothNoWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Work}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(workWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Shade}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth + workWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(shadeWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Material}"));
		jrDesignTextField.setX(patientWidth + 4 + toothNoWidth + shadeWidth + workWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(materialWidth - 3);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Status}"));
		jrDesignTextField.setX(patientWidth + 8 + toothNoWidth + shadeWidth + materialWidth + workWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(2);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(18);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{patientName}"));
		jrDesignTextField.setX(2);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(patientWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{toothNumbers}"));
		jrDesignTextField.setX(patientWidth + 2);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(toothNoWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{dentalWork}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(workWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{shade}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(shadeWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{material}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + shadeWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(materialWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{status}"));
		jrDesignTextField.setX(patientWidth + 2 + toothNoWidth + shadeWidth + materialWidth + workWidth);
		jrDesignTextField.setY(2);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(statusWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setBold(false);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(2);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		int copingWidth, bisqueWidth, finalWidth;
		copingWidth = (35 * (columnWidth - 40)) / 100;
		bisqueWidth = (35 * (columnWidth - 40)) / 100;
		finalWidth = (35 * (columnWidth - 40)) / 100;

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setX(20);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setExpression(new JRDesignExpression("$F{copingStage}"));
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(copingWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{bisqueStage}"));
		jrDesignTextField.setX(copingWidth + 20);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(bisqueWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{finalStage}"));
		jrDesignTextField.setX(copingWidth + bisqueWidth + 20);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setWidth(finalWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addInspectionReportsItem(parameters,
				contentFontSize, columnWidth, pageWidth, pageHeight, "$F{items}", normalStyle));

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-inspection-subreport.jasper");
		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + itemsValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-inspection-subreport.jasper\""));
		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private void createDentalWorkInvoiceSubreport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);

		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(32);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{doctor}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(205);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{invoiceId}"));
		jrDesignTextField.setX(205);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(columnWidth - 205);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{date}"));
		jrDesignTextField.setX(205);
		jrDesignTextField.setY(16);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(columnWidth - 205);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDentalWorkInvoice(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));
		band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{grandTotal}.equals(null) && !$P{grandTotal}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{grandTotal}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth - 10);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(1);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{grandTotal}.equals(null) && !$P{grandTotal}.isEmpty()"));
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(100);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{signature}.equals(null) && !$P{signature}.isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{signature}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(100);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.BOTTOM);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth - 10);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private JRBand addDentalWorkInvoice(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-work-invoice-subreports.jrxml");
		jasperDesign.setName("INVOICE Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		int xSpace = 0;

		band = new JRDesignBand();
		band.setHeight(26);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		xSpace = 35;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{OrderDate}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(90);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 90;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PatientName}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 100;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Material}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 100;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TeethNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 100;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TotalAmount}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(70);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setHeight(30);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{sNo}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setWidth(35);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = 35;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{orderDate}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(90);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 90;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{patientName}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 100;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{material}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 100;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{teethNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 100;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{total}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(70);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 70;
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-work-invoice-subreports.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-work-invoice-subreports.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private void createDentalLabReceipt(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle)
			throws JRException {
		band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(32);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{doctor}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(205);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{receiptId}"));
		jrDesignTextField.setX(205);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(columnWidth - 205);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{date}"));
		jrDesignTextField.setX(205);
		jrDesignTextField.setY(16);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(columnWidth - 205);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{content}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(18);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{paid}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(100);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{signature}.equals(null) && !$P{signature}.isEmpty()"));
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{signature}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(100);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.BOTTOM);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth - 10);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private void createDentalImagingInvoiceSubreport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);

		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDentalImagingItem(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle));

		band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{total}.equals(null) && !$P{total}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{total}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth - 10);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(1);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{total}.equals(null) && !$P{total}.isEmpty()"));
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(100);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{signature}.equals(null) && !$P{signature}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{poweredBy}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(100);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.BOTTOM);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(250);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{signature}"));
		jrDesignTextField.setX(251);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(100);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.BOTTOM);
		jrDesignTextField.setFontSize(12);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		jrDesignTextField.setWidth(columnWidth - 275);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

	}

	private JRBand addDentalImagingItem(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-imaging-invoice-subreports.jrxml");
		jasperDesign.setName("INVOICE Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		int xSpace = 0;

		band = new JRDesignBand();
		band.setHeight(26);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(45);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		xSpace = 45;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Service}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(175);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 175;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Quadrant}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(175);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 175;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TeethNo}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(175);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(22);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnHeader(band);

		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);

		band.setHeight(25);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{sNo}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setWidth(45);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = 45;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{serviceName}"));
		jrDesignTextField.setX(xSpace + 5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(170);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 175;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{quadrant}"));
		jrDesignTextField.setX(xSpace + 5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(170);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + 175;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{toothNumber}"));
		jrDesignTextField.setX(xSpace + 5);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(170);
		jrDesignTextField.setStretchWithOverflow(true);

		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(1);
		jrDesignLine = new JRDesignLine();
		jrDesignLine.setX(0);
		jrDesignLine.setY(0);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jasperDesign.setColumnFooter(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-imaging-invoice-subreports.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-dental-imaging-invoice-subreports.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private void createprocedureSheet(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(21);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{title}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{headerField}.equals(null) && !$P{headerField}.isEmpty()"));
		band.setHeight(21);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{headerField}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$P{body}.equals(null) && !$P{body}.isEmpty()"));
		band.setHeight(21);
		band.setSplitType(SplitTypeEnum.STRETCH);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{body}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		/*
		 * band = new JRDesignBand(); band.setPrintWhenExpression( new
		 * JRDesignExpression("!$P{footerField}.equals(null) && !$P{footerField}.isEmpty()"
		 * )); band.setHeight(21); band.setSplitType(SplitTypeEnum.STRETCH);
		 * 
		 * jrDesignTextField = new JRDesignTextField();
		 * jrDesignTextField.setExpression(new JRDesignExpression("$P{footerField}"));
		 * jrDesignTextField.setX(1); jrDesignTextField.setY(0);
		 * jrDesignTextField.setHeight(20); jrDesignTextField.setWidth(columnWidth);
		 * jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		 * jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		 * jrDesignTextField.setStretchWithOverflow(true);
		 * jrDesignTextField.setFontSize(new Float(contentFontSize));
		 * jrDesignTextField.setMarkup("html"); band.addElement(jrDesignTextField);
		 */

		JRDesignDatasetRun dsr = new JRDesignDatasetRun();
		dsr.setDatasetName("dataset1");
		dsr.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{footerFields})"));
		band = new JRDesignBand();
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{footerFields}.equals(null)&&!$P{footerFields}.isEmpty()"));
		band.setHeight(40);
		band.setSplitType(SplitTypeEnum.STRETCH);

		StandardListComponent listComponent = new StandardListComponent();
		listComponent.setPrintOrderValue(PrintOrderEnum.HORIZONTAL);

		DesignListContents contents = new DesignListContents();
		contents.setHeight(40);
		contents.setWidth((columnWidth) / 2);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{key}"));
		jrDesignTextField.setY(0);
		jrDesignTextField.setX(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(200);
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize - 1));
		jrDesignTextField.setStretchWithOverflow(true);
		contents.addElement(jrDesignTextField);

		JRDesignImage jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		jrDesignImage.setPrintWhenExpression(new JRDesignExpression("$F{isImage}"));
		jrDesignImage.setExpression(new JRDesignExpression("$F{value}"));
		jrDesignImage.setX(50);
		jrDesignImage.setY(20);
		jrDesignImage.setHeight(20);
		jrDesignImage.setWidth(100);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		contents.addElement(jrDesignImage);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$F{isImage}"));
		jrDesignTextField.setExpression(new JRDesignExpression("$F{value}"));
		jrDesignTextField.setY(20);
		jrDesignTextField.setX(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(200);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setStretchWithOverflow(true);
		contents.addElement(jrDesignTextField);

		listComponent.setContents(contents);
		listComponent.setDatasetRun(dsr);
		JRDesignComponentElement reportElement = new JRDesignComponentElement();
		reportElement.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "list"));
		reportElement.setHeight(40);
		reportElement.setWidth(columnWidth);
		reportElement.setX(0);
		reportElement.setY(0);
		reportElement.setComponent(listComponent);

		band.addElement(reportElement);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addProcedureSheetDiagram(parameters,
				contentFontSize, columnWidth, pageWidth, pageHeight, "$P{diagram}", normalStyle));
		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$P{item}.equals(null) && !$P{item}.isEmpty()"));
		band.setHeight(30);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{ProcedureTitle}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);

		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addProcedureSheetItem(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{item}", normalStyle));

	}

	private JRBand addProcedureSheetItem(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-procedure-sheet-subreport.jrxml");
		jasperDesign.setName("INVOICE Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{fieldOne}.equals(null)"));
		band.setHeight(20);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{fieldOne}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(120);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$F{fieldTwo}.equals(null)"));
		jrDesignTextField.setExpression(new JRDesignExpression("$F{fieldTwo}"));
		jrDesignTextField.setX(120);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(120);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{fieldThree}"));
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$F{fieldThree}.equals(null)"));
		jrDesignTextField.setX(240);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(120);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$F{fieldFour}.equals(null)"));
		jrDesignTextField.setExpression(new JRDesignExpression("$F{fieldFour}"));
		jrDesignTextField.setX(360);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(120);
		jrDesignTextField.setFontSize(new Float(contentFontSize));
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);

		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-procedure-sheet-subreport.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);
		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-procedure-sheet-subreport.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private JRBand addProcedureSheetDiagram(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-procedure-diagram-subreport.jrxml");
		jasperDesign.setName("INVOICE Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{name}.equals(null) && !$F{name}.isEmpty()"));
		band.setHeight(pageHeight);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{name}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);

		band.addElement(jrDesignTextField);

		JRDesignImage jrDesignImage = new JRDesignImage(null);

		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		jrDesignImage.setExpression(new JRDesignExpression("$F{imageUrl}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY(20);
		jrDesignImage.setHeight(pageHeight - 20);
		jrDesignImage.setWidth(columnWidth);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		band.addElement(jrDesignImage);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-procedure-diagram-subreport.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(new JRDesignExpression(
				"\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-procedure-diagram-subreport.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

	private void createDoctorLabReport(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {
		int headerHeight = parameters.get("headerHeight") != null
				? Integer.parseInt(parameters.get("headerHeight").toString())
				: 0;
		int footerHeight = parameters.get("footerHeight") != null
				? Integer.parseInt(parameters.get("footerHeight").toString())
				: 0;
		band = new JRDesignBand();
		band.setHeight(headerHeight);
		band.setSplitType(SplitTypeEnum.STRETCH);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{headerImg}.equals(null) && !$P{headerImg}.isEmpty()"));
		JRDesignImage jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		jrDesignImage.setExpression(new JRDesignExpression("$P{headerImg}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY(0);
		jrDesignImage.setHeight(headerHeight);
		jrDesignImage.setWidth(columnWidth);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		band.addElement(jrDesignImage);
		jasperDesign.setPageHeader(band);

		((JRDesignSection) jasperDesign.getDetailSection()).addBand(adddoctorLabDiagram(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{items}", normalStyle, headerHeight, footerHeight));

		band = new JRDesignBand();
		band.setHeight(footerHeight);
		band.setPrintWhenExpression(new JRDesignExpression("!$P{footerImg}.equals(null) && !$P{footerImg}.isEmpty()"));
		band.setSplitType(SplitTypeEnum.STRETCH);
		jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		jrDesignImage.setExpression(new JRDesignExpression("$P{footerImg}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY((0));
		jrDesignImage.setHeight(footerHeight);
		jrDesignImage.setWidth(columnWidth);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		band.addElement(jrDesignImage);
		jasperDesign.setPageFooter(band);

	}

	private JRBand adddoctorLabDiagram(Map<String, Object> parameters, Integer contentFontSize, int columnWidth,
			int pageWidth, int pageHeight, String servicesValue, JRDesignStyle normalStyleinth, int headerHeight,
			int footerHeight) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader
				.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-doctor-lab-subreport.jrxml");
		jasperDesign.setName("INVOICE Items");
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setBottomMargin(0);
		jasperDesign.setLeftMargin(0);
		jasperDesign.setRightMargin(0);
		jasperDesign.setTopMargin(0);

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$F{item}.equals(null) && !$F{item}.isEmpty()"));
		band.setHeight(820 - headerHeight - footerHeight);
		band.setSplitType(SplitTypeEnum.STRETCH);

		JRDesignImage jrDesignImage = new JRDesignImage(null);

		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		jrDesignImage.setExpression(new JRDesignExpression("$F{item}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY(0);
		jrDesignImage.setHeight(820 - headerHeight - footerHeight);
		jrDesignImage.setWidth(columnWidth);
		jrDesignImage.setHorizontalImageAlign(HorizontalImageAlignEnum.CENTER);
		band.addElement(jrDesignImage);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JasperCompileManager.compileReportToFile(jasperDesign,
				JASPER_TEMPLATES_RESOURCE + "new/mongo-doctor-lab-subreport.jasper");

		JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign);
		jSubreport.setUsingCache(false);
		jSubreport.setRemoveLineWhenBlank(true);
		jSubreport.setPrintRepeatedValues(false);
		jSubreport.setWidth(columnWidth);
		jSubreport.setHeight(0);
		jSubreport.setX(0);
		jSubreport.setY(0);

		jSubreport.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(" + servicesValue + ")"));

		jSubreport.setExpression(
				new JRDesignExpression("\"" + JASPER_TEMPLATES_RESOURCE + "new/mongo-doctor-lab-subreport.jasper\""));

		JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();
		designSubreportParameter.setName("REPORT_CONNECTION");
		designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));

		band = new JRDesignBand();
		band.setHeight(0);
		band.addElement(jSubreport);

		return band;
	}

}

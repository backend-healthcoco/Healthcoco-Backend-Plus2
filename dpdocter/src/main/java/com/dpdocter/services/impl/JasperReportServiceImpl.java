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
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignSubreportParameter;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
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
					leftMargin + 28, rightMargin + 28, componentType);
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
			ComponentType componentType) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE + "new/mongo-multiple-data-A4.jrxml");
		jasperDesign.setName("sampleDynamicJasperDesign");
		jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.NO_PAGES);
		int pageWidth = 595, pageHeight = 842;
		if (pageSize.equalsIgnoreCase(PageSize.A5.name())) {
			topMargin = topMargin - 25;
			pageWidth = 420;
			pageHeight = 595;
		}
		int columnWidth = pageWidth - leftMargin - rightMargin;
		jasperDesign.setPageWidth(pageWidth);
		jasperDesign.setPageHeight(pageHeight);
		jasperDesign.setColumnWidth(columnWidth);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setLeftMargin(leftMargin);
		jasperDesign.setRightMargin(rightMargin);
		if (topMargin != null)
			jasperDesign.setTopMargin(topMargin);
		if (bottonMargin != null)
			jasperDesign.setBottomMargin(bottonMargin);

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
		dsr.setDatasetName("mongo-print-settings-dataset_1");

		expression = new JRDesignExpression();
		expression.setText("new net.sf.jasperreports.engine.JREmptyDataSource(1)");
		dsr.setDataSourceExpression(expression);

		Boolean showTableOne = (Boolean) parameters.get("showTableOne");
		jasperDesign.setPageHeader(createPageHeader(dsr, columnWidth, showTableOne, parameters));
		if (parameters.get("headerHtml") != null)
			((JRDesignSection) jasperDesign.getDetailSection())
					.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(
				createPatienDetailBand(dsr, jasperDesign, columnWidth, showTableOne, parameters.get("headerHtml")));
		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));

		if (parameters.get("showHistory") != null && (boolean) parameters.get("showHistory"))
			createHistory(jasperDesign, parameters, contentFontSize, normalStyle, columnWidth);

		if (componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
				&& parameters.get("clinicalNotes") != null)
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(createClinicalNotesSubreport(parameters,
					contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
		else if (componentType.getType().equalsIgnoreCase(ComponentType.CLINICAL_NOTES.getType()))
			createClinicalNotes(jasperDesign, columnWidth, contentFontSize);

		if (componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
				&& parameters.get("prescriptions") != null)
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPrescriptionSubreport(parameters,
					contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
		else if (componentType.getType().equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType()))
			createPrescription(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
					normalStyle);

		if (componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
				&& parameters.get("clinicalNotes") != null)
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(createDiagramsSubreport(parameters, dsr,
					contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));

		if (componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
				&& parameters.get("treatments") != null)
			((JRDesignSection) jasperDesign.getDetailSection()).addBand(createTreatmentServicesSubreport(parameters,
					contentFontSize, columnWidth, pageWidth, pageHeight, normalStyle));
		else if (componentType.getType().equalsIgnoreCase(ComponentType.TREATMENT.getType()))
			createTreatmentServices(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth, pageHeight,
					normalStyle);
		if (componentType.getType().equalsIgnoreCase(ComponentType.INVOICE.getType()))
			createInvoiceSubreport(jasperDesign, parameters, contentFontSize, columnWidth, pageWidth, pageHeight,
					normalStyle);

		if (parameters.get("followUpAppointment") != null) {
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
		jasperDesign.setPageFooter(createPageFooter(columnWidth, contentFontSize));
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
		//
		// if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
		// && parameters.get("clinicalNotes") != null)((JRDesignSection)
		// jasperDesign.getDetailSection()).addBand(createClinicalNotesSubreport(parameters,
		// contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
		// else
		// if(componentType.getType().equalsIgnoreCase(ComponentType.CLINICAL_NOTES.getType()))createClinicalNotes(jasperDesign,
		// columnWidth, contentFontSize);
		//
		// if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
		// && parameters.get("prescriptions") != null)((JRDesignSection)
		// jasperDesign.getDetailSection()).addBand(createPrescriptionSubreport(parameters,
		// contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
		// else if
		// (componentType.getType().equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType()))createPrescription(jasperDesign,
		// parameters, contentFontSize, pageWidth, pageHeight, columnWidth,
		// normalStyle);
		//
		// if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType())
		// && parameters.get("clinicalNotes") != null)((JRDesignSection)
		// jasperDesign.getDetailSection()).addBand(createDiagramsSubreport(parameters,
		// dsr, contentFontSize, pageWidth, pageHeight, columnWidth,
		// normalStyle));
		////
		// jasperDesign.setPageFooter(createPageFooter(columnWidth));
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

		addItems(jasperDesign, columnWidth, "$P{PastHistoryTitle}", "$P{PH}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{FamilyHistoryTitle}", "$P{FH}", fieldWidth, false, 0);

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
			addItems(jasperDesign, columnWidth, "$P{DietTitle}", "$P{diet}", fieldWidth, false, 15);
			addItems(jasperDesign, columnWidth, "$P{AddictionsTitle}", "$P{addictions}", fieldWidth, false, 15);
			addItems(jasperDesign, columnWidth, "$P{BowelHabitTitle}", "$P{bowelHabit}", fieldWidth, false, 15);
			addItems(jasperDesign, columnWidth, "$P{BladderHabitTitle}", "$P{bladderHabit}", fieldWidth, false, 15);
		}

		addItems(jasperDesign, columnWidth, "$P{OngoingDrugsTitle}", "$P{ongoingDrugs}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{AllergiesTitle}", "$P{allergies}", fieldWidth, false, 0);

		((JRDesignSection) jasperDesign.getDetailSection())
				.addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
	}

	private void createClinicalNotes(JasperDesign jasperDesign, int columnWidth, Integer contentFontSize) {
		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 145;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(
				new JRDesignExpression("!$P{indicationOfUSG}.equals( null ) && !$P{indicationOfUSG}.isEmpty()"));

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{USGTITLE}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 1));
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		addItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$P{vitalSigns}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$P{presentComplaint}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Complaints}", "$P{complaints}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$P{presentComplaintHistory}", fieldWidth,
				false, 0);
		addItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$P{menstrualHistory}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$P{obstetricHistory}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$P{generalExam}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{SystemExam}", "$P{systemExam}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{NoOfChildren}", "$P{noOfChildren}", fieldWidth, false, 0);

		addLMPAndEDD(jasperDesign, columnWidth, fieldWidth, 0, "$P{LMP}", "$P{lmp}", "$P{EDD}", "$P{edd}");

		addItems(jasperDesign, columnWidth, "$P{IndicationOfUSG}", "$P{indicationOfUSG}", fieldWidth, true, 0);
		addItems(jasperDesign, columnWidth, "$P{PA}", "$P{pa}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PS}", "$P{ps}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PV}", "$P{pv}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{EcgDetails}", "$P{ecgDetails}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Echo}", "$P{echo}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{XRayDetails}", "$P{xRayDetails}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Holter}", "$P{holter}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Observations}", "$P{observations}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Investigations}", "$P{investigations}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$P{provisionalDiagnosis}", fieldWidth, false,
				0);
		addItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$P{diagnosis}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Notes}", "$P{notes}", fieldWidth, false, 0);

		JRDesignDatasetRun dsr = new JRDesignDatasetRun();
		dsr.setDatasetName("dataset1");
		dsr.setDataSourceExpression(new JRDesignExpression(
				"new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{diagrams})"));

		band = new JRDesignBand();
		band.setPrintWhenExpression(new JRDesignExpression("!$P{diagrams}.isEmpty()"));
		band.setHeight(135);
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
		contents.setHeight(126);
		contents.setWidth(150);
		JRDesignImage jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		jrDesignImage.setExpression(new JRDesignExpression("$F{url}"));
		jrDesignImage.setX(0);
		jrDesignImage.setY(2);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignImage.setHeight(100);
		jrDesignImage.setWidth(120);
		contents.addElement(jrDesignImage);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{tags}"));
		jrDesignTextField.setY(108);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setStretchWithOverflow(true);
		contents.addElement(jrDesignTextField);

		listComponent.setContents(contents);
		listComponent.setDatasetRun(dsr);
		JRDesignComponentElement reportElement = new JRDesignComponentElement();
		reportElement.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "list"));
		reportElement.setHeight(126);
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
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
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
		if (headerHtml != null)
			reportElement.setY(0);
		else if (showTableOne)
			reportElement.setY(-20);
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

		if (parameters.get("headerHtml") != null) {
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
					"!$P{logoURL}.isEmpty() && !$P{headerLeftText}.isEmpty() && !$P{headerRightText}.isEmpty()"));

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
			columnHeader.setHeight(60);

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
			jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
			expression = new JRDesignExpression();
			expression.setText("$P{logoURL}");
			jrDesignImage.setExpression(expression);
			jrDesignImage.setX(((38 * columnWidth) / 100) + 1);
			jrDesignImage.setY(0);
			jrDesignImage.setHeight(50);
			jrDesignImage.setWidth(50);
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
				jrDesignLine.setY(-27);
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

		int fieldWidth = 118;
		if (contentFontSize > 13)
			fieldWidth = 145;
		else if (contentFontSize > 11)
			fieldWidth = 128;

		band = new JRDesignBand();
		band.setHeight(10);
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
		addItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$F{vitalSigns}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$F{presentComplaint}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Complaints}", "$F{complaints}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$F{presentComplaintHistory}", fieldWidth,
				false, 0);
		addItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$F{menstrualHistory}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$F{obstetricHistory}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$F{generalExam}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{SystemExam}", "$F{systemExam}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{NoOfChildren}", "$F{noOfChildren}", fieldWidth, false, 0);
		addLMPAndEDD(jasperDesign, columnWidth, fieldWidth, 0, "$P{LMP}", "$F{lmp}", "$P{EDD}", "$F{edd}");
		addItems(jasperDesign, columnWidth, "$P{IndicationOfUSG}", "$F{indicationOfUSG}", fieldWidth, true, 0);
		addItems(jasperDesign, columnWidth, "$P{PA}", "$F{pa}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PS}", "$F{ps}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{PV}", "$F{pv}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{EcgDetails}", "$F{ecgDetails}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Echo}", "$F{echo}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{XRayDetails}", "$F{xRayDetails}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Holter}", "$F{holter}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Observations}", "$F{observations}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Investigations}", "$F{investigations}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$F{provisionalDiagnosis}", fieldWidth, false,
				0);
		addItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$F{diagnosis}", fieldWidth, false, 0);
		addItems(jasperDesign, columnWidth, "$P{Notes}", "$F{notes}", fieldWidth, false, 0);

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
		band.setPrintWhenExpression(new JRDesignExpression("!$F{diagrams}.equals( null )"));
		band.setHeight(126);
		band.setSplitType(SplitTypeEnum.STRETCH);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Diagrams}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(80);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		StandardListComponent listComponent = new StandardListComponent();
		listComponent.setPrintOrderValue(PrintOrderEnum.HORIZONTAL);

		DesignListContents contents = new DesignListContents();
		contents.setHeight(126);
		contents.setWidth(150);
		JRDesignImage jrDesignImage = new JRDesignImage(null);
		jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
		expression = new JRDesignExpression();
		expression.setText("$F{url}");
		jrDesignImage.setExpression(expression);
		jrDesignImage.setX(0);
		jrDesignImage.setY(2);
		jrDesignTextField.setStretchWithOverflow(true);
		jrDesignImage.setHeight(100);
		jrDesignImage.setWidth(120);
		contents.addElement(jrDesignImage);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField
				.setPrintWhenExpression(new JRDesignExpression("!$F{tags}.isEmpty() && !$F{tags}.equals( null )"));
		jrDesignTextField.setExpression(new JRDesignExpression("$F{tags}"));
		jrDesignTextField.setY(108);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(100);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		jrDesignTextField.setStretchWithOverflow(true);
		contents.addElement(jrDesignTextField);

		listComponent.setContents(contents);
		listComponent.setDatasetRun(dsr);
		JRDesignComponentElement reportElement = new JRDesignComponentElement();
		reportElement.setComponentKey(
				new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "list"));
		reportElement.setHeight(126);
		reportElement.setWidth(columnWidth - 80);
		reportElement.setX(80);
		reportElement.setY(0);
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
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
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
<<<<<<< Updated upstream
		
		Boolean showIntructions = (Boolean) parameters.get("showIntructions") != null ? (Boolean) parameters.get("showIntructions") : false;
        Boolean showDirection = (Boolean) parameters.get("showDirection") != null ? (Boolean) parameters.get("showDirection") : false;
        
        int drugWidth = 0, dosageWidth=0, directionWidth=0, durationWidth=0, instructionWidth=0; 
        if(showDirection != null && showIntructions != null){
        	if(showDirection && showIntructions){
            	drugWidth = (30*(columnWidth-31))/100;dosageWidth=(16*(columnWidth-31)/100);directionWidth=(23*(columnWidth-31)/100);durationWidth=(13*(columnWidth-31)/100);instructionWidth=(18*(columnWidth-31)/100);
            }else if(showDirection){
            	drugWidth = (30*(columnWidth-31))/100;dosageWidth=(21*(columnWidth-31)/100);directionWidth=(29*(columnWidth-31)/100);durationWidth=(20*(columnWidth-31)/100);
            }else if(showIntructions){
            	drugWidth = (30*(columnWidth-31))/100;dosageWidth=(21*(columnWidth-31)/100);durationWidth=(20*(columnWidth-31)/100);instructionWidth=(29*(columnWidth-31)/100);
            }else{
            	drugWidth = (40*(columnWidth-31))/100;dosageWidth=(30*(columnWidth-31)/100);durationWidth=(30*(columnWidth-31)/100);
            }
        }
        
        Integer titleFontSize = contentFontSize;
        if(contentFontSize > 13)titleFontSize = 13;
        jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
        band.addElement(jrDesignLine);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(35);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
        band.addElement(jrDesignTextField);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{DrugName}"));
        jrDesignTextField.setX(35);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(drugWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
        band.addElement(jrDesignTextField);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Frequency}"));
        jrDesignTextField.setX(35+drugWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(dosageWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
        band.addElement(jrDesignTextField);
        
        if(showDirection){
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{Direction}"));
            jrDesignTextField.setX(35+drugWidth+dosageWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(directionWidth);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
            band.addElement(jrDesignTextField);
        }
        
        if(showDirection && showIntructions){
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{Duration}"));
            jrDesignTextField.setX(35+drugWidth+dosageWidth+directionWidth+15);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(durationWidth-15+5);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
            band.addElement(jrDesignTextField);
            
            jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{Instruction}"));
            jrDesignTextField.setX(35+drugWidth+dosageWidth+directionWidth+durationWidth+10);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(instructionWidth);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
            band.addElement(jrDesignTextField);
            
        }else{
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{Duration}"));
            jrDesignTextField.setX(35+drugWidth+dosageWidth+directionWidth+15);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(durationWidth-15);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
            band.addElement(jrDesignTextField);
            if(showIntructions){
            	jrDesignTextField = new JRDesignTextField();
                jrDesignTextField.setExpression(new JRDesignExpression("$P{Instruction}"));
                jrDesignTextField.setX(35+drugWidth+dosageWidth+directionWidth+durationWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(instructionWidth);
                jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
                band.addElement(jrDesignTextField);
            }
        }
        
//        if(showIntructions){
//        	jrDesignTextField = new JRDesignTextField();
//            jrDesignTextField.setExpression(new JRDesignExpression("$P{Instruction}"));
//            jrDesignTextField.setX(35+drugWidth+dosageWidth+directionWidth+durationWidth+2);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(instructionWidth-2);
//            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setFontSize(new Float(titleFontSize));
//            band.addElement(jrDesignTextField);
//        }
        jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(22); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
        band.addElement(jrDesignLine);
        
=======

		Boolean showIntructions = (Boolean) parameters.get("showIntructions");
		Boolean showDirection = (Boolean) parameters.get("showDirection");

		int drugWidth = 0, dosageWidth = 0, directionWidth = 0, durationWidth = 0, instructionWidth = 0;
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

		if (showDirection && showIntructions) {
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
			if (showIntructions) {
				jrDesignTextField = new JRDesignTextField();
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

>>>>>>> Stashed changes
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
		jrDesignTextField.setWidth(drugWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{dosage}"));
		jrDesignTextField.setX(35 + drugWidth);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(dosageWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		if (showDirection) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{direction}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(directionWidth);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);
		}

		if (showDirection && showIntructions) {
			jrDesignTextField = new JRDesignTextField();
			jrDesignTextField.setExpression(new JRDesignExpression("$F{duration}"));
			jrDesignTextField.setX(35 + drugWidth + dosageWidth + directionWidth + 15);
			jrDesignTextField.setY(0);
			jrDesignTextField.setHeight(18);
			jrDesignTextField.setWidth(durationWidth - 15 + 5);
			jrDesignTextField.setStretchWithOverflow(true);
			band.addElement(jrDesignTextField);

			if (showIntructions) {
				jrDesignTextField = new JRDesignTextField();
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

			if (showIntructions) {
				jrDesignTextField = new JRDesignTextField();
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
			boolean isHTML, Integer xSpaceForTitle) {

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
		jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
		if (isHTML)
			jrDesignTextField.setMarkup("html");
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private JRBand createPageFooter(int columnWidth, Integer contentFontSize) throws JRException {
		band = new JRDesignBand();
		band.setHeight(54);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{footerSignature}.isEmpty()"));
		jrDesignTextField.setExpression(new JRDesignExpression("$P{footerSignature}"));
		jrDesignTextField.setBold(true);
		jrDesignTextField.setFontSize(new Float(contentFontSize + 2));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(3);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		jrDesignLine = new JRDesignLine();
		jrDesignLine.setPrintWhenExpression(new JRDesignExpression("!$P{footerBottomText}.isEmpty()"));
		jrDesignLine.setX(0);
		jrDesignLine.setY(25);
		jrDesignLine.setHeight(1);
		jrDesignLine.setWidth(columnWidth);
		band.addElement(jrDesignLine);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{footerBottomText}.isEmpty()"));
		jrDesignTextField.setExpression(new JRDesignExpression("$P{footerBottomText}"));
		jrDesignTextField.setX(0);
		jrDesignTextField.setY(27);
		jrDesignTextField.setHeight(26);
		jrDesignTextField.setWidth(columnWidth);
		jrDesignTextField.setMarkup("html");
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);

		return band;
	}

	private void createTreatmentServices(JasperDesign jasperDesign, Map<String, Object> parameters,
			Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, JRDesignStyle normalStyle)
			throws JRException {

		band = new JRDesignBand();
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENT}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentServices(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$P{services}", normalStyle));

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
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{INVOICE}"));
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

		Boolean showInvoiceItemQuantity = (Boolean) parameters.get("showInvoiceItemQuantity");

		int serviceWidth = (25* (columnWidth - 30)) / 100, quantityWidth = (12 * (columnWidth - 30)) / 100,
				otherFieldsWidth = (11 * (columnWidth - 30)) / 100, xSpace = 0,
				statusWidth = (16 * (columnWidth - 30)) / 100, discountWidth = (15 * (columnWidth - 30)) / 100;

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

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{Tax}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(4);
		jrDesignTextField.setHeight(15);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + otherFieldsWidth;

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

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{discount}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(discountWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + discountWidth;

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{tax}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(otherFieldsWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + otherFieldsWidth;

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
		band.setHeight(10);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);

		JRDesignBand band = new JRDesignBand();
		band.setHeight(21);

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENT}"));
		jrDesignTextField.setX(1);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(20);
		jrDesignTextField.setWidth(220);
		jrDesignTextField.setBold(true);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentServices(parameters, contentFontSize,
				columnWidth, pageWidth, pageHeight, "$F{services}", normalStyle));

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

		Boolean showTreatmentQuantity = (Boolean) parameters.get("showTreatmentQuantity");

		int serviceWidth = (30 * (columnWidth - 30)) / 100, quantityWidth = (12 * (columnWidth - 30)) / 100,
				otherFieldsWidth = (11 * (columnWidth - 30)) / 100, xSpace = 0,
				statusWidth = (20 * (columnWidth - 30)) / 100, discountWidth = (15 * (columnWidth - 30)) / 100;

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

		if (showTreatmentQuantity) {
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
		jrDesignTextField.setExpression(new JRDesignExpression("$F{treatmentServiceName}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(serviceWidth);
		jrDesignTextField.setStretchWithOverflow(true);
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

		jrDesignTextField = new JRDesignTextField();
		jrDesignTextField.setExpression(new JRDesignExpression("$F{discount}"));
		jrDesignTextField.setX(xSpace);
		jrDesignTextField.setY(0);
		jrDesignTextField.setHeight(18);
		jrDesignTextField.setWidth(discountWidth);
		jrDesignTextField.setStretchWithOverflow(true);
		band.addElement(jrDesignTextField);
		xSpace = xSpace + discountWidth;

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
		jrDesignTextField.setX(0);
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
}

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
    private String MONGO_HOST_URI ;

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

    @Override
    @Transactional
    public JasperReportResponse createPDF(ComponentType componentType, Map<String, Object> parameters, String fileName, String layout, String pageSize, Integer topMargin, Integer bottonMargin, Integer leftMargin, Integer rightMargin, Integer contentFontSize, String pdfName, String... subReportFileName) {
    	JasperReportResponse jasperReportResponse = null;
    	BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
    	AmazonS3 s3client = new AmazonS3Client(credentials);
    	
    	try {
		    MongoDbConnection mongoConnection = new MongoDbConnection(MONGO_HOST_URI, null, null);
	
		    parameters.put("REPORT_CONNECTION", mongoConnection);
		    parameters.put("SUBREPORT_DIR", JASPER_TEMPLATES_RESOURCE);
	
		    JasperDesign design = createDesign(parameters, pageSize, contentFontSize, topMargin+45, bottonMargin, leftMargin+28, rightMargin+28, componentType); 
		    JasperReport jasperReport = JasperCompileManager.compileReport(design);
			
		    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
	
		    JasperExportManager.exportReportToPdfFile(jasperPrint, JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
		    jasperReportResponse = new JasperReportResponse();
		    jasperReportResponse.setPath(JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf");
		    FileSystemResource fileSystemResource = new FileSystemResource(JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
		    jasperReportResponse.setFileSystemResource(fileSystemResource);
		    ObjectMetadata metadata = new ObjectMetadata();
		    metadata.setContentEncoding("pdf");
		    metadata.setContentType("application/pdf");
		    metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
		    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf", jasperReportResponse.getFileSystemResource().getFile());
		    putObjectRequest.setMetadata(metadata);
		    s3client.putObject(putObjectRequest);
		    return jasperReportResponse;

	} catch (JRException e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    public JasperDesign createDesign(Map<String, Object> parameters, String pageSize, Integer contentFontSize, Integer topMargin, Integer bottonMargin, Integer leftMargin, Integer rightMargin, ComponentType componentType) throws JRException {
        JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data-A4.jrxml");
        jasperDesign.setName("sampleDynamicJasperDesign");
        jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.NO_PAGES);
        int pageWidth = 595, pageHeight = 842;
        if(pageSize.equalsIgnoreCase(PageSize.A5.name())){
        	topMargin = topMargin - 25;
        	pageWidth = 420; pageHeight = 595;
        }
        int columnWidth = pageWidth-leftMargin-rightMargin;
        jasperDesign.setPageWidth(pageWidth);jasperDesign.setPageHeight(pageHeight);
        jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(leftMargin);jasperDesign.setRightMargin(rightMargin);
        if(topMargin != null)jasperDesign.setTopMargin(topMargin);
	    if(bottonMargin != null)jasperDesign.setBottomMargin(bottonMargin);
	    
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Noto Sans");normalStyle.setDefault(true); normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize));normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252");normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
        
        JRDesignDatasetRun dsr = new JRDesignDatasetRun();  dsr.setDatasetName("mongo-print-settings-dataset_1"); 
        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("new net.sf.jasperreports.engine.JREmptyDataSource(1)");
        dsr.setDataSourceExpression(expression);
        
        Boolean showTableOne = (Boolean) parameters.get("showTableOne");
        jasperDesign.setPageHeader(createPageHeader(dsr, columnWidth, showTableOne)); 
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPatienDetailBand(dsr, jasperDesign, columnWidth, showTableOne));
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
        
        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("clinicalNotes") != null)
        	((JRDesignSection) jasperDesign.getDetailSection()).addBand(createClinicalNotesSubreport(parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
        else if(componentType.getType().equalsIgnoreCase(ComponentType.CLINICAL_NOTES.getType()))createClinicalNotes(jasperDesign, columnWidth, contentFontSize);
        
        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("prescriptions") != null)
        	((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPrescriptionSubreport(parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
        else if (componentType.getType().equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType()))createPrescription(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle);
        
        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("clinicalNotes") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createDiagramsSubreport(parameters, dsr, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
    
        if((componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("treatments") != null) || (componentType.getType().equalsIgnoreCase(ComponentType.TREATMENT.getType())))
        	((JRDesignSection) jasperDesign.getDetailSection()).addBand(addTreatmentServices(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$P{treatments}"));
        
        if(parameters.get("followUpAppointment") != null){
        	JRDesignBand band = new JRDesignBand();
        	band.setHeight(21);
        	JRDesignTextField jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{followUpAppointment}"));
            jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
            band.addElement(jrDesignTextField);
            ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
        }
        jasperDesign.setPageFooter(createPageFooter(columnWidth, contentFontSize));
//        dsr.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.JREmptyDataSource(1)"));
//        String logoURL = (String) parameters.get("logoURL");
//        parameters.put("headerLeftText", "<table width='10000px' height='2000px' style='background-color:#E5DD6F;'>"
//        		+ "<tr width='10000px' height='2000px' border='2px'>"+
//        					"<td width='100%' style='font-family:Noto Sans;font-size:100px;background-color:#E6E6FA;'>"
//        					+ "<p><b>Dr. (Mrs.) R.K. KANDHARI</b></p>"+
//        					"<p style='text-align:right;'>M.D., D.G.O.</p>"
//        					+ "<p>Consulting Obstetrician & Gynaecologist</p>"+
//        					"<p>0712 - 2286215, &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Mob-  9822203359</p>"+
//        					"<hr><p>MVC REGD. NO. 48001 HOSPITAL REGD. NO. 195</p></td>"
//        					+ "<td width='30%'><td width='30%'><img style='width:200px;height:200px;' src='"+logoURL+"'/></td>"
//        					+ "<td width='100%' style='font-family:Noto Sans;font-size:100px;background-color:red;' border='1px'>"+
//        					"<p><b>KANDHARI <br>MATERNITY & <br>NURSING HOME</b></p>"
//        					+ "<p>plot no.5 behind gulmohar hall</p>"+
//        					"<p>Pandey Layout, Khamla, Nagpur - 440025</p></td>"
//        					+ "</tr></table>");
//        jasperDesign.setPageHeader(createPageHeader(dsr, columnWidth)); 
//        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
//        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPatienDetailBand(dsr, jasperDesign, columnWidth));
//        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createLine(0, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
//        
//        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("clinicalNotes") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createClinicalNotesSubreport(parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
//        else if(componentType.getType().equalsIgnoreCase(ComponentType.CLINICAL_NOTES.getType()))createClinicalNotes(jasperDesign, columnWidth, contentFontSize);
//        
//        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("prescriptions") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPrescriptionSubreport(parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
//        else if (componentType.getType().equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType()))createPrescription(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle);
//        
//        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("clinicalNotes") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createDiagramsSubreport(parameters, dsr, contentFontSize, pageWidth, pageHeight, columnWidth, normalStyle));
////    
//        jasperDesign.setPageFooter(createPageFooter(columnWidth));
        return jasperDesign;
    }

	private void createClinicalNotes(JasperDesign jasperDesign, int columnWidth, Integer contentFontSize) {
		int fieldWidth = 118;
		if(contentFontSize > 13)fieldWidth = 140;
		else if(contentFontSize > 11)fieldWidth = 128;
	
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$P{vitalSigns}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$P{presentComplaint}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Complaints}", "$P{complaints}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$P{presentComplaintHistory}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$P{menstrualHistory}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$P{obstetricHistory}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$P{generalExam}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{SystemExam}", "$P{systemExam}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Observations}", "$P{observations}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Investigations}", "$P{investigations}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$P{provisionalDiagnosis}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$P{diagnosis}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Notes}", "$P{notes}", fieldWidth);

        JRDesignDatasetRun dsr = new JRDesignDatasetRun();  dsr.setDatasetName("dataset1"); 
        dsr.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{diagrams})"));
   
        JRDesignBand band = new JRDesignBand();
        band.setPrintWhenExpression(new JRDesignExpression("!$P{diagrams}.isEmpty()"));
        band.setHeight(135);
        band.setSplitType(SplitTypeEnum.STRETCH);
        
        JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Diagrams}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(8);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(80);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        StandardListComponent listComponent = new StandardListComponent();
        listComponent.setPrintOrderValue(PrintOrderEnum.HORIZONTAL);
        
        DesignListContents contents = new DesignListContents();
        contents.setHeight(126);contents.setWidth(150);
        JRDesignImage jrDesignImage = new JRDesignImage(null);
        jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
        jrDesignImage.setExpression(new JRDesignExpression("$F{url}"));
        jrDesignImage.setX(0);jrDesignImage.setY(2);jrDesignTextField.setStretchWithOverflow(true); jrDesignImage.setHeight(100);jrDesignImage.setWidth(120);
        contents.addElement(jrDesignImage);     
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$F{tags}"));
        jrDesignTextField.setY(108);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(100);
        jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);jrDesignTextField.setStretchWithOverflow(true);
        contents.addElement(jrDesignTextField);
        
        listComponent.setContents(contents);
        listComponent.setDatasetRun(dsr);
        JRDesignComponentElement reportElement = new JRDesignComponentElement();  
        reportElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components","jr", "list"));  
        reportElement.setHeight(126);  reportElement.setWidth(columnWidth-80);  reportElement.setX(80);  reportElement.setY(8);  
        reportElement.setComponent(listComponent);
        
        band.addElement(reportElement);
        
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void createPrescription(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		JRDesignBand band = new JRDesignBand();
		band.setHeight(20);
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(220);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);       
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDrugs(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$P{prescriptionItems}", normalStyle));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabTest(jasperDesign, columnWidth, "$P{labTest}", contentFontSize));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addAdvice(jasperDesign, columnWidth, "$P{advice}", contentFontSize));

	}

	private JRBand createLine(int yPoint, int columnWidth, PositionTypeEnum positionTypeEnum) {
		JRDesignBand band = new JRDesignBand(); 
        band.setHeight(1);
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(yPoint); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        jrDesignLine.setPositionType(positionTypeEnum);
        band.addElement(jrDesignLine);
		return band;
	}

	private JRBand createPatienDetailBand(JRDesignDatasetRun dsr, JasperDesign jasperDesign, int columnWidth, Boolean showTableOne) throws JRException { 

        JRDesignBand band = new JRDesignBand(); 
        band.setHeight(10);
        
        JRDesignDatasetParameter param = new JRDesignDatasetParameter();  param.setName("patientLeftText");    
        param.setExpression(new JRDesignExpression("$P{patientLeftText}"));
        dsr.addParameter(param);
        
        param = new JRDesignDatasetParameter();  param.setName("patientRightText");
        param.setExpression(new JRDesignExpression("$P{patientRightText}"));
        dsr.addParameter(param);
       
        DesignCell columnHeader = new DesignCell();  columnHeader.setHeight(10);  
        
        JRDesignTextField textField = new JRDesignTextField();
        textField.setX(0);textField.setY(1);textField.setWidth((50*columnWidth)/100);textField.setHeight(9);textField.setMarkup("html");textField.setStretchWithOverflow(true);textField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        textField.setExpression(new JRDesignExpression("$P{patientLeftText}"));  
        columnHeader.addElement(textField); 

        textField = new JRDesignTextField();
        textField.setX((((62*columnWidth)/100)));textField.setY(1); textField.setWidth((38*columnWidth)/100);textField.setHeight(9);textField.setMarkup("html");textField.setStretchWithOverflow(true);textField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        textField.setExpression(new JRDesignExpression("$P{patientRightText}"));
        columnHeader.addElement(textField);

        StandardColumn column = new StandardColumn();  column.setDetailCell(columnHeader);  column.setWidth(columnWidth);
        StandardTable table = new StandardTable();  table.addColumn(column);  table.setDatasetRun(dsr);
        JRDesignComponentElement reportElement = new JRDesignComponentElement();  
        reportElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components","jr", "table"));  
        reportElement.setHeight(10);  reportElement.setWidth(columnWidth);  reportElement.setX(0); reportElement.setY(0);
        reportElement.setComponent(table);
    
        band.addElement(reportElement);
          
		return band;
	}

	private JRBand createPageHeader(JRDesignDatasetRun dsr, int columnWidth, Boolean showTableOne) throws JRException {
		JRDesignBand band = new JRDesignBand();
        band.setHeight(5); 
        band.setPrintWhenExpression(new JRDesignExpression("!$P{logoURL}.isEmpty() || !$P{headerLeftText}.isEmpty() || !$P{headerRightText}.isEmpty()"));
        JRDesignDatasetParameter param = new JRDesignDatasetParameter();  param.setName("logoURL");    
        JRDesignExpression exp = new JRDesignExpression("$P{logoURL}");  param.setExpression(exp);
        dsr.addParameter(param);
        
        param = new JRDesignDatasetParameter();  param.setName("headerLeftText");
        exp = new JRDesignExpression("$P{headerLeftText}");  param.setExpression(exp);
        dsr.addParameter(param);
//        band.setPrintWhenExpression(new JRDesignExpression("!$P{logoURL}.isEmpty() && !$P{headerLeftText}.isEmpty() && !$P{headerRightText}.isEmpty()"));
//        JRDesignDatasetParameter param = new JRDesignDatasetParameter();  param.setName("logoURL");    
//        param.setExpression(new JRDesignExpression("$P{logoURL}"));
//        dsr.addParameter(param);
//        
//        param = new JRDesignDatasetParameter();  param.setName("headerLeftText");
//        param.setExpression(new JRDesignExpression("$P{headerLeftText}"));
//        dsr.addParameter(param);
//       
//        param = new JRDesignDatasetParameter();  param.setName("headerRightText");
//        param.setExpression(new JRDesignExpression("$P{headerRightText}"));
//        dsr.addParameter(param);
//        param = new JRDesignDatasetParameter();  param.setName("logoURL");
//        exp = new JRDesignExpression("$P{logoURL}");  param.setExpression(exp);
//        dsr.addParameter(param);
       
        DesignCell columnHeader = new DesignCell();  
        if(showTableOne)columnHeader.setHeight(50);  
        else columnHeader.setHeight(90);
        
        JRDesignTextField jrDesignTextField = new JRDesignTextField();
        JRDesignExpression jrExpression = new JRDesignExpression();jrExpression.setText("$P{headerLeftText}");jrDesignTextField.setExpression(jrExpression);
        jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth((38*columnWidth)/100);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);jrDesignTextField.setStretchWithOverflow(true);
        columnHeader.addElement(jrDesignTextField);
        
        JRDesignImage jrDesignImage = new JRDesignImage(null);
        jrDesignImage.setPrintWhenExpression(new JRDesignExpression("!$P{logoURL}.isEmpty()"));
        jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);jrExpression = new JRDesignExpression();jrExpression.setText("$P{logoURL}");jrDesignImage.setExpression(jrExpression);
        jrDesignImage.setX(((38*columnWidth)/100)+1);jrDesignImage.setY(0);jrDesignImage.setHeight(50);jrDesignImage.setWidth(50);
        columnHeader.addElement(jrDesignImage);     
        
        jrDesignTextField = new JRDesignTextField();
        jrExpression = new JRDesignExpression();jrExpression.setText("$P{headerRightText}");jrDesignTextField.setExpression(jrExpression);
        jrDesignTextField.setX((((62*columnWidth)/100)));jrDesignTextField.setY(0);jrDesignTextField.setHeight(20); jrDesignTextField.setWidth((38*columnWidth)/100);jrDesignTextField.setMarkup("html");
        jrDesignTextField.setStretchWithOverflow(true); jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        columnHeader.addElement(jrDesignTextField);
        
//        JRDesignLine jrDesignLine = new JRDesignLine();
//        jrDesignLine.setX(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);jrDesignLine.setY(-17);
//        
//        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
//        band.addElement(jrDesignLine);        
        
//      HtmlComponent htmlComponent = new HtmlComponent();
//      htmlComponent.setHtmlContentExpression(new JRDesignExpression("$P{headerLeftText}"));
//      htmlComponent.setScaleType(ScaleImageEnum.FILL_FRAME);
//      JRDesignComponentElement reportElement = new JRDesignComponentElement();  
//      reportElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/htmlcomponent","hc", "html"));  
//      reportElement.setHeight(40);  reportElement.setWidth(columnWidth);  reportElement.setX(0);  reportElement.setY(0);  
//      reportElement.setComponent(htmlComponent);htmlComponent.setScaleType(ScaleImageEnum.REAL_SIZE);
//      band.addElement(reportElement);   
//      band.setHeight(40);  

		return band;
	}

	private JRBand createClinicalNotesSubreport(Map<String, Object> parameters, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-clinical-notes_subreport-A4.jrxml"); 
		jasperDesign.setName("clinical Notes");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);
        jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        jasperDesign.addStyle(normalStyle);
   
    	int fieldWidth = 118;
		if(contentFontSize > 13)fieldWidth = 140;
		else if(contentFontSize > 11)fieldWidth = 128;
	
        //add clinical notes items
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{VitalSigns}", "$F{vitalSigns}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{PresentComplaints}", "$F{presentComplaint}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Complaints}", "$F{complaints}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{PresentComplaintHistory}", "$F{presentComplaintHistory}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{MenstrualHistory}", "$F{menstrualHistory}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{ObstetricHistory}", "$F{obstetricHistory}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{GeneralExam}", "$F{generalExam}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{SystemExam}", "$F{systemExam}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Observations}", "$F{observations}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Investigations}", "$F{investigations}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{ProvisionalDiagnosis}", "$F{provisionalDiagnosis}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Diagnosis}", "$F{diagnosis}", fieldWidth);
		addClinicalNotesItems(jasperDesign, columnWidth, "$P{Notes}", "$F{notes}", fieldWidth);
		
        JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-clinical-notes_subreport-A4.jasper");
    	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(0);
        jSubreport.setX(0);jSubreport.setY(0);

        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{clinicalNotes})");
        jSubreport.setDataSourceExpression(expression);
       
        expression = new JRDesignExpression();
        expression.setText("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-clinical-notes_subreport-A4.jasper\"");
        jSubreport.setExpression(expression);

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        JRDesignBand band = new JRDesignBand();
        band.setHeight(0);
        band.addElement(jSubreport);
        return band;
	}

	private JRBand createDiagramsSubreport(Map<String, Object> parameters, JRDesignDatasetRun dsr, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		 
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data_diagrams-A4.jrxml"); 
		jasperDesign.setName("clinical Notes diagrams");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);
        jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        dsr = new JRDesignDatasetRun();  dsr.setDatasetName("dataset1"); 
        dsr.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($F{diagrams})"));
        
        jasperDesign.addStyle(normalStyle);
   
        JRDesignBand band = new JRDesignBand();
        band.setPrintWhenExpression(new JRDesignExpression("!$F{diagrams}.equals( null )"));
        band.setHeight(126);
        band.setSplitType(SplitTypeEnum.STRETCH);
        
        JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Diagrams}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(80);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        StandardListComponent listComponent = new StandardListComponent();
        listComponent.setPrintOrderValue(PrintOrderEnum.HORIZONTAL);
        
        DesignListContents contents = new DesignListContents();
        contents.setHeight(126);contents.setWidth(150);
        JRDesignImage jrDesignImage = new JRDesignImage(null);
        jrDesignImage.setScaleImage(ScaleImageEnum.FILL_FRAME);
        JRDesignExpression jrExpression = new JRDesignExpression();jrExpression.setText("$F{url}");jrDesignImage.setExpression(jrExpression);
        jrDesignImage.setX(0);jrDesignImage.setY(2);jrDesignTextField.setStretchWithOverflow(true); jrDesignImage.setHeight(100);jrDesignImage.setWidth(120);
        contents.addElement(jrDesignImage);     
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$F{tags}.isEmpty() && !$F{tags}.equals( null )"));
        jrDesignTextField.setExpression(new JRDesignExpression("$F{tags}"));
        jrDesignTextField.setY(108);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(100);
        jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);jrDesignTextField.setStretchWithOverflow(true);
        contents.addElement(jrDesignTextField);
        
        listComponent.setContents(contents);
        listComponent.setDatasetRun(dsr);
        JRDesignComponentElement reportElement = new JRDesignComponentElement();  
        reportElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components","jr", "list"));  
        reportElement.setHeight(126);  reportElement.setWidth(columnWidth-80);  reportElement.setX(80);  reportElement.setY(0);  
        reportElement.setComponent(listComponent);
        
        band.addElement(reportElement);
        
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
		
        JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data_diagrams-A4.jasper");
    	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(0);
        jSubreport.setX(0);jSubreport.setY(0);

        jSubreport.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{clinicalNotes})"));
       
        jSubreport.setExpression(new JRDesignExpression("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data_diagrams-A4.jasper\""));

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        band = new JRDesignBand();
        band.setHeight(0);
        band.addElement(jSubreport);
        return band;

	}

	private JRBand createPrescriptionSubreport(Map<String, Object> parameters, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription-subreport-A4.jrxml"); 
		jasperDesign.setName("Prescription");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        jasperDesign.addStyle(normalStyle);
   
        JRDesignBand band = new JRDesignBand();
		band.setHeight(21);
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(220);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);       
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDrugs(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$F{items}", normalStyle));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabTest(jasperDesign, columnWidth, "$F{labTest}", contentFontSize));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addAdvice(jasperDesign, columnWidth, "$F{advice}", contentFontSize));

        JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription-subreport-A4.jasper");
  	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(0);
        jSubreport.setX(0);jSubreport.setY(0);

        jSubreport.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{prescriptions})"));
        jSubreport.setExpression(new JRDesignExpression("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription-subreport-A4.jasper\""));

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        band = new JRDesignBand();
        band.setHeight(0);
        band.addElement(jSubreport);
        return band;
	}

	private JRDesignBand addAdvice(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
				
		JRDesignBand band = new JRDesignBand();
		band.setHeight(22);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".equals( null ) && !"+value+".isEmpty()"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Advice}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
            
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-70);
        jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        return band;
	}

	private JRDesignBand addLabTest(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(22);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".equals( null )"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{LabTest}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
            
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-71);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        return band;
	}

	private JRDesignBand addDrugs(Map<String, Object> parameters, Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, String itemsValue, JRDesignStyle normalStyle) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription_items_subreport-A4.jrxml"); 
		jasperDesign.setName("Prescription Items");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        jasperDesign.addStyle(normalStyle);
   
        JRDesignBand band = new JRDesignBand();
		band.setHeight(26);
		
        Boolean showIntructions = (Boolean) parameters.get("showIntructions");
        Boolean showDirection = (Boolean) parameters.get("showDirection");
        
        int drugWidth = 0, dosageWidth=0, directionWidth=0, durationWidth=0, instructionWidth=0; 
        if(showDirection && showIntructions){
        	drugWidth = (30*(columnWidth-31))/100;dosageWidth=(16*(columnWidth-31)/100);directionWidth=(23*(columnWidth-31)/100);durationWidth=(15*(columnWidth-31)/100);instructionWidth=(16*(columnWidth-31)/100);
        }else if(showDirection){
        	drugWidth = (30*(columnWidth-31))/100;dosageWidth=(21*(columnWidth-31)/100);directionWidth=(29*(columnWidth-31)/100);durationWidth=(20*(columnWidth-31)/100);
        }else if(showIntructions){
        	drugWidth = (30*(columnWidth-31))/100;dosageWidth=(21*(columnWidth-31)/100);durationWidth=(20*(columnWidth-31)/100);instructionWidth=(29*(columnWidth-31)/100);
        }else{
        	drugWidth = (40*(columnWidth-31))/100;dosageWidth=(30*(columnWidth-31)/100);durationWidth=(30*(columnWidth-31)/100);
        }
        
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
        band.addElement(jrDesignLine);
        
        JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(39);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{DrugName}"));
        jrDesignTextField.setX(39);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(drugWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Frequency}"));
        jrDesignTextField.setX(39+drugWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(dosageWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        if(showDirection){
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{Direction}"));
            jrDesignTextField.setX(39+drugWidth+dosageWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(directionWidth);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
            band.addElement(jrDesignTextField);
        }
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Duration}"));
        jrDesignTextField.setX(39+drugWidth+dosageWidth+directionWidth+15);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(durationWidth-15);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        if(showIntructions){
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$P{Instruction}"));
            jrDesignTextField.setX(39+drugWidth+dosageWidth+directionWidth+durationWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(instructionWidth);
            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
            band.addElement(jrDesignTextField);
        }
        jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(22); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
        band.addElement(jrDesignLine);
        
		jasperDesign.setColumnHeader(band);
        
		band = new JRDesignBand();
		band.setSplitType(SplitTypeEnum.STRETCH);
		if(parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.SMALL.name()))band.setHeight(22);
		else if(parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.MEDIUM.name()))band.setHeight(27);
		else if(parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.LARGE.name()))band.setHeight(32);
		
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(39);
        jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$F{drug}"));
        jrDesignTextField.setX(39);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(drugWidth);
        jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$F{dosage}"));
        jrDesignTextField.setX(39+drugWidth);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(dosageWidth);
        jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        if(showDirection){
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$F{direction}"));
            jrDesignTextField.setX(39+drugWidth+dosageWidth);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(directionWidth);
            jrDesignTextField.setStretchWithOverflow(true);
            band.addElement(jrDesignTextField);
        }
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$F{duration}"));
        jrDesignTextField.setX(39+drugWidth+dosageWidth+directionWidth+15);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(durationWidth-15);
        jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        if(showIntructions){
        	jrDesignTextField = new JRDesignTextField();
            jrDesignTextField.setExpression(new JRDesignExpression("$F{instruction}"));
            jrDesignTextField.setX(39+drugWidth+dosageWidth+directionWidth+durationWidth);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(instructionWidth);
            jrDesignTextField.setStretchWithOverflow(true);
            band.addElement(jrDesignTextField);
        }
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
        
        band = new JRDesignBand();
        band.setHeight(13);
        jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
//        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
        band.addElement(jrDesignLine);        
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(12);jrDesignTextField.setWidth(columnWidth);
        jrDesignTextField.setStretchWithOverflow(true);
        jrDesignTextField.setBlankWhenNull(true);
        band.addElement(jrDesignTextField);
        
        jasperDesign.setColumnFooter(band);
        
		JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription_items_subreport-A4.jasper");
  	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(0);
        jSubreport.setX(0);jSubreport.setY(0);

        jSubreport.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource("+itemsValue+")"));
       
        jSubreport.setExpression(new JRDesignExpression("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription_items_subreport-A4.jasper\""));

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        band = new JRDesignBand();
        band.setHeight(0);
        band.addElement(jSubreport);
        
        return band;

	}

	private void addClinicalNotesItems(JasperDesign jasperDesign, int columnWidth, String key, String value, Integer fieldWidth) {
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(20);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".equals( null )"));
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(key));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
        band.addElement(jrDesignTextField);
        
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setStretchType(StretchTypeEnum.ELEMENT_GROUP_HEIGHT);
        band.addElement(jrDesignTextField);	
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private JRBand createPageFooter(int columnWidth, Integer contentFontSize) throws JRException {
		JRDesignBand band = new JRDesignBand();
        band.setHeight(54); 
              
        JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{footerSignature}.isEmpty()"));
        jrDesignTextField.setExpression(new JRDesignExpression("$P{footerSignature}"));
        jrDesignTextField.setBold(true);
        jrDesignTextField.setFontSize(new Float(contentFontSize+2));
        jrDesignTextField.setX(0);jrDesignTextField.setY(3);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(columnWidth);
        jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setPrintWhenExpression(new JRDesignExpression("!$P{footerBottomText}.isEmpty()"));
        jrDesignLine.setX(0); jrDesignLine.setY(25); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        band.addElement(jrDesignLine);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{footerBottomText}.isEmpty()"));
        jrDesignTextField.setExpression(new JRDesignExpression("$P{footerBottomText}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(27);jrDesignTextField.setHeight(26); jrDesignTextField.setWidth(columnWidth);
        jrDesignTextField.setMarkup("html");
        jrDesignTextField.setStretchWithOverflow(true); 
        band.addElement(jrDesignTextField);
        
 		return band;
	}

	private JRBand addTreatmentServices(Map<String, Object> parameters, Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, String itemsValue) throws JRException {
//		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-treatment-services.jrxml"); 
//		jasperDesign.setName("Treatment");
//        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
//     
//        JRDesignStyle normalStyle = new JRDesignStyle();
//        normalStyle.setName("Noto Sans");normalStyle.setDefault(true);normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize)); normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252"); normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
//   
//        JRDesignBand band = new JRDesignBand();
//		band.setHeight(26);
//		
//        Boolean showTreatmentQuantity = (Boolean) parameters.get("showTreatmentQuantity");
//        
//        int serviceWidth = 0, quantityWidth=0; 
//        if(showTreatmentQuantity){
//        	serviceWidth = (50*(columnWidth-31))/100; quantityWidth = (50*(columnWidth-31))/100;
//        }else{
//        	serviceWidth = columnWidth;
//        }
//        
//        JRDesignLine jrDesignLine = new JRDesignLine();
//        jrDesignLine.setX(0); jrDesignLine.setY(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
//        band.addElement(jrDesignLine);
//        
//        JRDesignTextField jrDesignTextField = new JRDesignTextField();
//        jrDesignTextField.setExpression(new JRDesignExpression("$P{SNo}"));
//        jrDesignTextField.setX(0);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(39);
//        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
//        band.addElement(jrDesignTextField);
//        
//        jrDesignTextField = new JRDesignTextField();
//        jrDesignTextField.setExpression(new JRDesignExpression("$P{Service}"));
//        jrDesignTextField.setX(39);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(serviceWidth);
//        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
//        band.addElement(jrDesignTextField);
//        
//        if(showTreatmentQuantity){
//        	jrDesignTextField = new JRDesignTextField();
//            jrDesignTextField.setExpression(new JRDesignExpression("$P{Quantity}"));
//            jrDesignTextField.setX(39+serviceWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(quantityWidth);
//            jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
//            band.addElement(jrDesignTextField);
//        }
//        jrDesignLine = new JRDesignLine();
//        jrDesignLine.setX(0); jrDesignLine.setY(22); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
//        band.addElement(jrDesignLine);
//        
//		jasperDesign.setColumnHeader(band);
//        
//		band = new JRDesignBand();
//		band.setSplitType(SplitTypeEnum.STRETCH);
//		band.setHeight(22);
//		
//		jrDesignTextField = new JRDesignTextField();
//        jrDesignTextField.setExpression(new JRDesignExpression("$F{no}"));
//        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(39);
//        jrDesignTextField.setStretchWithOverflow(true);
//        band.addElement(jrDesignTextField);
//        
//		jrDesignTextField = new JRDesignTextField();
//        jrDesignTextField.setExpression(new JRDesignExpression("$F{treatmentServiceName}"));
//        jrDesignTextField.setX(39);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(serviceWidth);
//        jrDesignTextField.setStretchWithOverflow(true);
//        band.addElement(jrDesignTextField);
//        
//        if(showTreatmentQuantity){
//        	jrDesignTextField = new JRDesignTextField();
//            jrDesignTextField.setExpression(new JRDesignExpression("$F{quantity}"));
//            jrDesignTextField.setX(39+serviceWidth);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(quantityWidth);
//            jrDesignTextField.setStretchWithOverflow(true);
//            band.addElement(jrDesignTextField);
//        }
//        
//        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
//        
//        band = new JRDesignBand();
//        band.setHeight(1);
//        jrDesignLine = new JRDesignLine();
//        jrDesignLine.setX(0); jrDesignLine.setY(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
//        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
//        band.addElement(jrDesignLine);        
//        jasperDesign.setColumnFooter(band);
//        
//		JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-treatment-services.jasper");
//  	
//	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
//        jSubreport.setUsingCache(false); 
//        jSubreport.setRemoveLineWhenBlank(true); 
//        jSubreport.setPrintRepeatedValues(false);
//        jSubreport.setWidth(columnWidth);
//        jSubreport.setHeight(60);
//        jSubreport.setX(0);jSubreport.setY(21);
//
//        JRDesignExpression expression = new JRDesignExpression();
//        expression.setText("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource("+itemsValue+")");
//        jSubreport.setDataSourceExpression(expression);
//       
//        expression = new JRDesignExpression();
//        expression.setText("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-treatment-services.jasper\"");
//        jSubreport.setExpression(expression);
//
//        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
//        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
//        
//        band = new JRDesignBand();
//        band.setHeight(81);
//        
//        jrDesignTextField = new JRDesignTextField();
//        jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENT}"));
//        jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(220);
//        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
//        band.addElement(jrDesignTextField);
//        
//        band.addElement(jSubreport);
        
		  JRDesignBand band = new JRDesignBand();
	      band.setHeight(22);
	      
	      JRDesignTextField jrDesignTextField = new JRDesignTextField();
	      jrDesignTextField.setExpression(new JRDesignExpression("$P{TREATMENT}"));
	      jrDesignTextField.setX(1);jrDesignTextField.setY(3);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(80);
	      jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
	      band.addElement(jrDesignTextField);
        
	      jrDesignTextField = new JRDesignTextField();
	      jrDesignTextField.setExpression(new JRDesignExpression("$P{treatments}"));
	      jrDesignTextField.setX(82);jrDesignTextField.setY(3);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-81);
	      jrDesignTextField.setStretchWithOverflow(true);
	      band.addElement(jrDesignTextField);
	      return band;
	}

}

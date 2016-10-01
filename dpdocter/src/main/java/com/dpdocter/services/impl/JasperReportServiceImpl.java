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
	
//		    JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
//		    JRPropertiesUtil jrPropertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
//		    jrPropertiesUtil.setProperty("net.sf.jasperreports.extension.registry.factory.queryexecuters.mongodb", "com.jaspersoft.mongodb.query.MongoDbQueryExecuterExtensionsRegistryFactory");
//		    jrPropertiesUtil.setProperty("net.sf.jasperreports.default.font.size", contentFontSize+"");

		    JasperDesign design = createDesign(parameters, pageSize, contentFontSize, topMargin, bottonMargin, leftMargin, rightMargin, componentType); 
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
        
        int pageWidth = 595, pageHeight = 842;
        if(pageSize.equalsIgnoreCase(PageSize.A5.name())){
        	pageWidth = 420; pageHeight = 595;
        }
        int columnWidth = pageWidth-leftMargin-rightMargin;
        jasperDesign.setPageWidth(pageWidth);jasperDesign.setPageHeight(pageHeight);
        jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(leftMargin);jasperDesign.setRightMargin(rightMargin);
        if(topMargin != null)jasperDesign.setTopMargin(topMargin+10);
	    if(bottonMargin != null)jasperDesign.setBottomMargin(bottonMargin);
	    
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Noto Sans");normalStyle.setDefault(true); normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize));normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252");normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
        
        JRDesignDatasetRun dsr = new JRDesignDatasetRun();  dsr.setDatasetName("mongo-print-settings-dataset_1"); 
        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("new net.sf.jasperreports.engine.JREmptyDataSource(1)");
        dsr.setDataSourceExpression(expression);
        
        jasperDesign.setPageHeader(createPageHeader(dsr, columnWidth)); 
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPatienDetailBand(dsr, jasperDesign, columnWidth));
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(createLine(-15, columnWidth, PositionTypeEnum.FIX_RELATIVE_TO_TOP));
        
        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("clinicalNotes") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createClinicalNotesSubreport(parameters, contentFontSize, pageWidth, pageHeight, columnWidth));
        else if(componentType.getType().equalsIgnoreCase(ComponentType.CLINICAL_NOTES.getType()))createClinicalNotes(jasperDesign, columnWidth, contentFontSize);
        
        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("prescriptions") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createPrescriptionSubreport(parameters, contentFontSize, pageWidth, pageHeight, columnWidth));
        else if (componentType.getType().equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType()))createPrescription(jasperDesign, parameters, contentFontSize, pageWidth, pageHeight, columnWidth);
        
        if(componentType.getType().equalsIgnoreCase(ComponentType.VISITS.getType()) && parameters.get("clinicalNotes") != null)((JRDesignSection) jasperDesign.getDetailSection()).addBand(createDiagramsSubreport(parameters, dsr, contentFontSize, pageWidth, pageHeight, columnWidth));
    
        jasperDesign.setPageFooter(createPageFooter(columnWidth));
        return jasperDesign;
    }

	private void createClinicalNotes(JasperDesign jasperDesign, int columnWidth, Integer contentFontSize) {
		addVitalSigns(jasperDesign, columnWidth, "$P{vitalSigns}", contentFontSize);
        addComplaints(jasperDesign, columnWidth, "$P{complaintIds}", contentFontSize);
        addObservations(jasperDesign, columnWidth, "$P{observationIds}", contentFontSize);
        addInvestigations(jasperDesign,columnWidth, "$P{investigationIds}", contentFontSize);
        addDiagnosis(jasperDesign, columnWidth, "$P{diagnosesIds}", contentFontSize);
        addNotes(jasperDesign, columnWidth, "$P{noteIds}", contentFontSize);
        
        
        JRDesignDatasetRun dsr = new JRDesignDatasetRun();  dsr.setDatasetName("dataset1"); 
        dsr.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{diagramIds})"));
   
        JRDesignBand band = new JRDesignBand();
        band.setPrintWhenExpression(new JRDesignExpression("!$P{diagramIds}.isEmpty()"));
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
        JRDesignExpression jrExpression = new JRDesignExpression();jrExpression.setText("$F{url}");jrDesignImage.setExpression(jrExpression);
        jrDesignImage.setX(0);jrDesignImage.setY(2);jrDesignTextField.setStretchWithOverflow(true); jrDesignImage.setHeight(100);jrDesignImage.setWidth(120);
        contents.addElement(jrDesignImage);     
        
        jrDesignTextField = new JRDesignTextField();
        jrExpression = new JRDesignExpression();jrExpression.setText("$F{tags}");jrDesignTextField.setExpression(jrExpression);
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

	private void createPrescription(JasperDesign jasperDesign, Map<String, Object> parameters, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth) throws JRException {
		JRDesignBand band = new JRDesignBand();
		band.setHeight(30);
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(8);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(220);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);       
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDrugs(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$P{prescriptionItems}"));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabTest(jasperDesign, columnWidth, "$P{labTest}"));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addAdvice(jasperDesign, columnWidth, "$P{advice}"));

	}

	private JRBand createLine(int yPoint, int columnWidth, PositionTypeEnum positionTypeEnum) {
		JRDesignBand band = new JRDesignBand(); 
        band.setHeight(0);
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(yPoint); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        jrDesignLine.setPositionType(positionTypeEnum);
        band.addElement(jrDesignLine);
		return band;
	}

	private JRBand createPatienDetailBand(JRDesignDatasetRun dsr, JasperDesign jasperDesign, int columnWidth) throws JRException { 

        JRDesignBand band = new JRDesignBand(); 
        band.setHeight(0);
        
        JRDesignDatasetParameter param = new JRDesignDatasetParameter();  param.setName("patientLeftText");    
        JRDesignExpression exp = new JRDesignExpression("$P{patientLeftText}");  param.setExpression(exp);
        dsr.addParameter(param);
        
        param = new JRDesignDatasetParameter();  param.setName("patientRightText");
        exp = new JRDesignExpression("$P{patientRightText}");  param.setExpression(exp);
        dsr.addParameter(param);
       
        DesignCell columnHeader = new DesignCell();  columnHeader.setHeight(41);  
        
        JRDesignTextField textField = new JRDesignTextField();
        textField.setX(0);textField.setY(1);textField.setWidth((50*columnWidth)/100);textField.setHeight(40);textField.setMarkup("html");textField.setStretchWithOverflow(true);textField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        JRDesignExpression jrExpression = new JRDesignExpression();jrExpression.setText("$P{patientLeftText}"); 
        textField.setExpression(jrExpression);  
        columnHeader.addElement(textField); 

        textField = new JRDesignTextField();
        textField.setX((((62*columnWidth)/100)));textField.setY(1); textField.setWidth((38*columnWidth)/100);textField.setHeight(40);textField.setMarkup("html");textField.setStretchWithOverflow(true);textField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        jrExpression = new JRDesignExpression();jrExpression.setText("$P{patientRightText}");
        textField.setExpression(jrExpression);
        columnHeader.addElement(textField);

        StandardColumn column = new StandardColumn();  column.setDetailCell(columnHeader);  column.setWidth(columnWidth);
        StandardTable table = new StandardTable();  table.addColumn(column);  table.setDatasetRun(dsr);
        JRDesignComponentElement reportElement = new JRDesignComponentElement();  
        reportElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components","jr", "table"));  
        reportElement.setHeight(10);  reportElement.setWidth(columnWidth);  reportElement.setX(0);  reportElement.setY(-27);  
        reportElement.setComponent(table);
    
        band.addElement(reportElement);
          
		return band;
	}

	private JRBand createPageHeader(JRDesignDatasetRun dsr, int columnWidth) throws JRException {
		JRDesignBand band = new JRDesignBand();
        band.setHeight(5); 
        band.setPrintWhenExpression(new JRDesignExpression("!$P{logoURL}.isEmpty() && !$P{headerLeftText}.isEmpty() && !$P{headerRightText}.isEmpty()"));
        JRDesignDatasetParameter param = new JRDesignDatasetParameter();  param.setName("logoURL");    
        JRDesignExpression exp = new JRDesignExpression("$P{logoURL}");  param.setExpression(exp);
        dsr.addParameter(param);
        
        param = new JRDesignDatasetParameter();  param.setName("headerLeftText");
        exp = new JRDesignExpression("$P{headerLeftText}");  param.setExpression(exp);
        dsr.addParameter(param);
       
        param = new JRDesignDatasetParameter();  param.setName("headerRightText");
        exp = new JRDesignExpression("$P{headerRightText}");  param.setExpression(exp);
        dsr.addParameter(param);
       
        DesignCell columnHeader = new DesignCell();  columnHeader.setHeight(50);  
        
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
        
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(-17); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
        band.addElement(jrDesignLine);        
        
        StandardColumn column = new StandardColumn();  column.setDetailCell(columnHeader);  column.setWidth(columnWidth);
        StandardTable table = new StandardTable();  table.addColumn(column);  table.setDatasetRun(dsr);
        JRDesignComponentElement reportElement = new JRDesignComponentElement();  
        reportElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components","jr", "table"));  
        reportElement.setHeight(5);  reportElement.setWidth(columnWidth);  reportElement.setX(0);  reportElement.setY(0);  
        reportElement.setComponent(table);
         
        band.addElement(reportElement);  
        band.setHeight(16);  

		return band;
	}

	private JRBand createClinicalNotesSubreport(Map<String, Object> parameters, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth) throws JRException {
		
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-clinical-notes_subreport-A4.jrxml"); 
		jasperDesign.setName("clinical Notes");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);
        jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Noto Sans");normalStyle.setDefault(true);normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize)); normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252"); normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
   
        //add clinical notes items
        addVitalSigns(jasperDesign, columnWidth, "$F{vitalSigns}", contentFontSize);
        addComplaints(jasperDesign, columnWidth, "$F{complaints}", contentFontSize);
        addObservations(jasperDesign, columnWidth, "$F{observations}", contentFontSize);
        addInvestigations(jasperDesign,columnWidth, "$F{investigations}", contentFontSize);
        addDiagnosis(jasperDesign, columnWidth, "$F{diagnosis}", contentFontSize);
        addNotes(jasperDesign, columnWidth, "$F{notes}", contentFontSize);
        
        JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-clinical-notes_subreport-A4.jasper");
    	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(80);
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
        band.setHeight(80);
        band.addElement(jSubreport);
        return band;
	}

	private JRBand createDiagramsSubreport(Map<String, Object> parameters, JRDesignDatasetRun dsr, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth) throws JRException {
		 
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data_diagrams-A4.jrxml"); 
		jasperDesign.setName("clinical Notes diagrams");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);
        jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        dsr = new JRDesignDatasetRun();  dsr.setDatasetName("dataset1"); 
        dsr.setDataSourceExpression(new JRDesignExpression("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($F{diagrams})"));
        
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Noto Sans");normalStyle.setDefault(true);normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize)); normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252"); normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
   
        JRDesignBand band = new JRDesignBand();
        band.setPrintWhenExpression(new JRDesignExpression("!$F{diagrams}.equals( null )"));
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
        JRDesignExpression jrExpression = new JRDesignExpression();jrExpression.setText("$F{url}");jrDesignImage.setExpression(jrExpression);
        jrDesignImage.setX(0);jrDesignImage.setY(2);jrDesignTextField.setStretchWithOverflow(true); jrDesignImage.setHeight(100);jrDesignImage.setWidth(120);
        contents.addElement(jrDesignImage);     
        
        jrDesignTextField = new JRDesignTextField();
        jrExpression = new JRDesignExpression();jrExpression.setText("$F{tags}");jrDesignTextField.setExpression(jrExpression);
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
		
        JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data_diagrams-A4.jasper");
    	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(50);
        jSubreport.setX(0);jSubreport.setY(0);

        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{clinicalNotes})");
        jSubreport.setDataSourceExpression(expression);
       
        expression = new JRDesignExpression();
        expression.setText("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-multiple-data_diagrams-A4.jasper\"");
        jSubreport.setExpression(expression);

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        band = new JRDesignBand();
        band.setHeight(50);
        band.addElement(jSubreport);
        return band;

	}

	private JRBand createPrescriptionSubreport(Map<String, Object> parameters, Integer contentFontSize, int pageWidth, int pageHeight, int columnWidth) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription-subreport-A4.jrxml"); 
		jasperDesign.setName("Prescription");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Noto Sans");normalStyle.setDefault(true);normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize)); normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252"); normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
   
        JRDesignBand band = new JRDesignBand();
		band.setHeight(30);
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{PRESCRIPTION}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(8);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(220);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);       
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addDrugs(parameters, contentFontSize, columnWidth, pageWidth, pageHeight, "$F{items}"));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addLabTest(jasperDesign, columnWidth, "$F{labTest}"));
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(addAdvice(jasperDesign, columnWidth, "$F{advice}"));

        JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription-subreport-A4.jasper");
  	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(80);
        jSubreport.setX(0);jSubreport.setY(0);

        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{prescriptions})");
        jSubreport.setDataSourceExpression(expression);
       
        expression = new JRDesignExpression();
        expression.setText("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription-subreport-A4.jasper\"");
        jSubreport.setExpression(expression);

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        band = new JRDesignBand();
        band.setHeight(80);
        band.addElement(jSubreport);
        return band;
	}

	private JRDesignBand addAdvice(JasperDesign jasperDesign, int columnWidth, String value) {
		JRDesignBand band = new JRDesignBand();
		band.setHeight(22);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".equals( null ) && !"+value+".isEmpty()"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Advice}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(70);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
            
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(71);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-70);
        jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        return band;
	}

	private JRDesignBand addLabTest(JasperDesign jasperDesign, int columnWidth, String value) {
		JRDesignBand band = new JRDesignBand();
		band.setHeight(27);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".equals( null )"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{LabTest}"));
        jrDesignTextField.setX(1);jrDesignTextField.setY(2);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(70);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
            
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(71);jrDesignTextField.setY(2);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(columnWidth-71);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        return band;
	}

	private JRDesignBand addDrugs(Map<String, Object> parameters, Integer contentFontSize, int columnWidth, int pageWidth, int pageHeight, String itemsValue) throws JRException {
		JasperDesign jasperDesign = JRXmlLoader.load(JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription_items_subreport-A4.jrxml"); 
		jasperDesign.setName("Prescription Items");
        jasperDesign.setPageWidth(pageWidth); jasperDesign.setPageHeight(pageHeight);jasperDesign.setColumnWidth(columnWidth);jasperDesign.setColumnSpacing(0);jasperDesign.setBottomMargin(0);jasperDesign.setLeftMargin(0);jasperDesign.setRightMargin(0);jasperDesign.setTopMargin(0);
     
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Noto Sans");normalStyle.setDefault(true);normalStyle.setFontName("Noto Sans");normalStyle.setFontSize(new Float(contentFontSize)); normalStyle.setPdfFontName("Helvetica");normalStyle.setPdfEncoding("Cp1252"); normalStyle.setPdfEmbedded(false);jasperDesign.addStyle(normalStyle);
   
        JRDesignBand band = new JRDesignBand();
		band.setHeight(23);
		
        Boolean showIntructions = (Boolean) parameters.get("showIntructions");
        Boolean showDirection = (Boolean) parameters.get("showDirection");
        
        int drugWidth = 0, dosageWidth=0, directionWidth=0, durationWidth=0, instructionWidth=0; 
        if(showDirection && showIntructions){
        	drugWidth = (35*(columnWidth-31))/100;dosageWidth=(15*(columnWidth-31)/100);directionWidth=(20*(columnWidth-31)/100);durationWidth=(15*(columnWidth-31)/100);instructionWidth=(15*(columnWidth-31)/100);
        }else if(showDirection){
        	drugWidth = (40*(columnWidth-31))/100;dosageWidth=(18*(columnWidth-31)/100);directionWidth=(24*(columnWidth-31)/100);durationWidth=(18*(columnWidth-31)/100);
        }else if(showIntructions){
        	drugWidth = (40*(columnWidth-31))/100;dosageWidth=(20*(columnWidth-31)/100);durationWidth=(20*(columnWidth-31)/100);instructionWidth=(20*(columnWidth-31)/100);
        }else{
        	drugWidth = (50*(columnWidth-31))/100;dosageWidth=(25*(columnWidth-31)/100);durationWidth=(25*(columnWidth-31)/100);
        }
        
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(0); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
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
        jrDesignTextField.setX(39+drugWidth+dosageWidth+directionWidth);jrDesignTextField.setY(4);jrDesignTextField.setHeight(15);jrDesignTextField.setWidth(durationWidth);
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
		if(parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.SMALL.name()))band.setHeight(20);
		else if(parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.MEDIUM.name()))band.setHeight(25);
		else if(parameters.get("contentLineSpace").toString().equalsIgnoreCase(LineSpace.LARGE.name()))band.setHeight(30);
		
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
        jrDesignTextField.setX(39+drugWidth+dosageWidth+directionWidth);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(durationWidth);
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
        
		JasperCompileManager.compileReportToFile(jasperDesign, JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription_items_subreport-A4.jasper");
  	
	    JRDesignSubreport jSubreport = new JRDesignSubreport(jasperDesign); 
        jSubreport.setUsingCache(false); 
        jSubreport.setRemoveLineWhenBlank(true); 
        jSubreport.setPrintRepeatedValues(false);
        jSubreport.setWidth(columnWidth);
        jSubreport.setHeight(80);
        jSubreport.setX(0);jSubreport.setY(0);

        JRDesignExpression expression = new JRDesignExpression();
        expression.setText("new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource("+itemsValue+")");
        jSubreport.setDataSourceExpression(expression);
       
        expression = new JRDesignExpression();
        expression.setText("\""+JASPER_TEMPLATES_RESOURCE+"new/mongo-prescription_items_subreport-A4.jasper\"");
        jSubreport.setExpression(expression);

        JRDesignSubreportParameter designSubreportParameter = new JRDesignSubreportParameter();  
        designSubreportParameter.setName("REPORT_CONNECTION");designSubreportParameter.setExpression(new JRDesignExpression("$P{REPORT_CONNECTION}"));
        
        band = new JRDesignBand();
        band.setHeight(81);
        band.addElement(jSubreport);
        
        jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(50); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        jrDesignLine.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_BOTTOM);
        band.addElement(jrDesignLine);        
        
        return band;

	}

	private void addNotes(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".isEmpty()"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Notes}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
            
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addDiagnosis(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".isEmpty()"));

		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Diagnosis}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);

		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addInvestigations(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".isEmpty()"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Investigations}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
               
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addObservations(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".isEmpty()"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Observations}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private void addComplaints(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		
		JRDesignBand band = new JRDesignBand();
		band.setHeight(18);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".isEmpty()"));
		
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{Complaints}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
       
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(0);jrDesignTextField.setHeight(18);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
        band.addElement(jrDesignTextField);
        
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);		
	}

	private void addVitalSigns(JasperDesign jasperDesign, int columnWidth, String value, Integer contentFontSize) {
		
		int fieldWidth = 60;
		if(contentFontSize > 13)fieldWidth = 82;
		else if(contentFontSize > 11)fieldWidth = 70;
		JRDesignBand band = new JRDesignBand();
		band.setHeight(25);
		band.setPrintWhenExpression(new JRDesignExpression("!"+value+".equals( null )"));
		JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{VitalSigns}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(4);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(fieldWidth);
        jrDesignTextField.setBold(true);jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
        band.addElement(jrDesignTextField);
        
		jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression(value));
        jrDesignTextField.setX(fieldWidth+1);jrDesignTextField.setY(4);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(columnWidth-fieldWidth-1);
        jrDesignTextField.setMarkup("html");jrDesignTextField.setStretchWithOverflow(true);jrDesignTextField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
        band.addElement(jrDesignTextField);	
        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(band);
	}

	private JRBand createPageFooter(int columnWidth) throws JRException {
		JRDesignBand band = new JRDesignBand();
        band.setHeight(54); 
              
        JRDesignTextField jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setPrintWhenExpression(new JRDesignExpression("!$P{footerSignature}.isEmpty()"));
        jrDesignTextField.setExpression(new JRDesignExpression("$P{footerSignature}"));
        jrDesignTextField.setX(columnWidth-150);jrDesignTextField.setY(3);jrDesignTextField.setHeight(20);jrDesignTextField.setWidth(150);
        jrDesignTextField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);jrDesignTextField.setStretchWithOverflow(true);
        band.addElement(jrDesignTextField);
        
        JRDesignLine jrDesignLine = new JRDesignLine();
        jrDesignLine.setX(0); jrDesignLine.setY(25); jrDesignLine.setHeight(1);jrDesignLine.setWidth(columnWidth);
        band.addElement(jrDesignLine);
        
        jrDesignTextField = new JRDesignTextField();
        jrDesignTextField.setExpression(new JRDesignExpression("$P{footerBottomText}"));
        jrDesignTextField.setX(0);jrDesignTextField.setY(25);jrDesignTextField.setHeight(26); jrDesignTextField.setWidth(columnWidth);
        jrDesignTextField.setMarkup("html");
        jrDesignTextField.setStretchWithOverflow(true); 
        band.addElement(jrDesignTextField);
        
 		return band;
	}


}

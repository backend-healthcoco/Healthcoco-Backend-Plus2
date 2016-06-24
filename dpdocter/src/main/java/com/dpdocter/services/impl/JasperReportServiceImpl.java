package com.dpdocter.services.impl;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.IOUtils;
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
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.services.JasperReportService;
import com.jaspersoft.mongodb.connection.MongoDbConnection;

import ar.com.fdvs.dj.domain.constants.Page;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRProperties;
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

    @SuppressWarnings("deprecation")
    @Override
    @Transactional
    public JasperReportResponse createPDF(Map<String, Object> parameters, String fileName, String layout, String pageSize, String margins, String pdfName) {
    	JasperReportResponse jasperReportResponse = null;
    	BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
    	AmazonS3 s3client = new AmazonS3Client(credentials);
    	
    	try {
		    MongoDbConnection mongoConnection = new MongoDbConnection(MONGO_HOST_URI, null, null);
	
		    parameters.put("REPORT_CONNECTION", mongoConnection);
		    parameters.put("SUBREPORT_DIR", JASPER_TEMPLATES_RESOURCE);
	
		    DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
		    context.setValue("net.sf.jasperreports.extension.registry.factory.queryexecuters.mongodb",
			    "com.jaspersoft.mongodb.query.MongoDbQueryExecuterExtensionsRegistryFactory");
		    // JRPropertiesUtil propertiesUtil =
		    // JRPropertiesUtil.getInstance(context);
	
		    JRProperties.setProperty("net.sf.jasperreports.query.executer.factory.MongoDbQuery", "com.jaspersoft.mongodb.query.MongoDbQueryExecuterFactory");
		    JasperDesign design = JRXmlLoader.load(new File(JASPER_TEMPLATES_RESOURCE + fileName + ".jrxml"));
	
		    if (layout.equals("LANDSCAPE")) {
			if (pageSize.equalsIgnoreCase("LETTER")) {
			    design.setPageHeight(Page.Page_Letter_Landscape().getHeight());
			    design.setPageWidth(Page.Page_Letter_Landscape().getWidth());
			} else if (pageSize.equalsIgnoreCase("LEGAL")) {
			    design.setPageHeight(Page.Page_Legal_Landscape().getHeight());
			    design.setPageWidth(Page.Page_Legal_Landscape().getWidth());
			} else {
			    design.setPageHeight(Page.Page_A4_Landscape().getHeight());
			    design.setPageWidth(Page.Page_A4_Landscape().getWidth());
			}
		    } else {
			if (pageSize.equalsIgnoreCase("LETTER")) {
			    design.setPageHeight(Page.Page_Letter_Portrait().getHeight());
			    design.setPageWidth(Page.Page_Letter_Portrait().getWidth());
			} else if (pageSize.equalsIgnoreCase("LEGAL")) {
			    design.setPageHeight(Page.Page_Legal_Portrait().getHeight());
			    design.setPageWidth(Page.Page_Legal_Portrait().getWidth());
			} else {
			    design.setPageHeight(Page.Page_A4_Portrait().getHeight());
			    design.setPageWidth(Page.Page_A4_Portrait().getWidth());
			}
		    }
		    JasperReport jasperReport = JasperCompileManager.compileReport(design);
	
		    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
	
		    JasperExportManager.exportReportToPdfFile(jasperPrint, JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
		    
		    jasperReportResponse = new JasperReportResponse();
		    jasperReportResponse.setPath(JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf");
		    FileSystemResource fileSystemResource = new FileSystemResource(JASPER_TEMPLATES_RESOURCE + pdfName + ".pdf");
		    jasperReportResponse.setFileSystemResource(fileSystemResource);
		    ObjectMetadata metadata = new ObjectMetadata();
		    metadata.setContentEncoding("pdf");
		    metadata.setContentType("application/pdf");
		    metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
		    PutObjectRequest putObjectRequest =new PutObjectRequest(bucketName, JASPER_TEMPLATES_ROOT_PATH + pdfName + ".pdf", jasperReportResponse.getFileSystemResource().getFile());
		    putObjectRequest.setMetadata(metadata);
		    s3client.putObject(putObjectRequest);
		    return jasperReportResponse;

	} catch (JRException e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }
}

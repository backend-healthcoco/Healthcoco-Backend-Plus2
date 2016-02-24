package com.dpdocter.tests;

import java.io.IOException;
import java.util.Date;

class GeneralTests {

    // public static boolean backupDataWithOutDatabase(String dumpExePath,
    // String host, String port, String database, String backupPath) {
    // boolean status = false;
    // try {
    // Process p = null;
    //
    // DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    // Date date = new Date();
    // String filepath = "backup-" + database + "-(" + dateFormat.format(date) +
    // ").bson";
    //
    // String batchCommand = "";
    //
    // batchCommand = dumpExePath + " -h " + host + " --port " + port + " -d
    // "+database + " -o \"" + backupPath + "" + filepath + "\"";
    //
    // System.out.println(batchCommand);
    // Runtime runtime = Runtime.getRuntime();
    // p = runtime.exec(batchCommand);
    // int processComplete = p.waitFor();
    //
    // if (processComplete == 0) {
    // status = true;
    //// log.info("Backup created successfully for without DB " + database + "
    // in " + host + ":" + port);
    // } else {
    // status = false;
    //// log.info("Could not create the backup for without DB " + database + "
    // in " + host + ":" + port);
    // }
    //
    // } catch (IOException ioe) {
    //// log.error(ioe, ioe.getCause());
    // } catch (Exception e) {
    //// log.error(e, e.getCause());
    // }
    // return status;
    // }

    private static String bucketName = "healthcoco";

    private static String keyName = "records/circle.jpg";

    private static String uploadFileName = "/home/suresh/Pictures/circle.jpg";

    // @Autowired
    // public static VelocityEngine velocityEngine;

    public static void main(String[] args) throws IOException {
	System.out.println(new Date(Long.parseLong("1455872437573")));
	// try{
	// BasicAWSCredentials credentials = new
	// BasicAWSCredentials("AKIAIHOF7FWQ2ZPMKKHQ",
	// "J+ksAueQN+ouU2uhHoO3RpfqhNZg0O0n8c61eT/m");
	//
	//
	// Map<String, Object> model = new HashMap<String, Object>();
	// model.put("fName", "Neha");
	//// model.put("link", uriInfo.getBaseUri() + link + tokenId);
	// model.put("imageURL",
	// "https://s3.amazonaws.com/healthcoco/templatesImage/");
	// String body =
	// "njsnnmsnmnms";//VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
	// "mailTemplate.vm", "UTF-8", model);
	//
	// Session session = Session.getInstance(new Properties());
	// MimeMessage mimeMessage = new MimeMessage(session);
	// mimeMessage.setSubject("n");
	//
	// MimeMultipart mimeMultipart = new MimeMultipart();
	// BodyPart p = new MimeBodyPart();
	// p.setContent(body, "text/html");
	// mimeMultipart.addBodyPart(p);
	// mimeMessage.setContent(mimeMultipart, "multipart/mixed");
	//// if(mailAttachment != null){
	//// mimeMessage.setFileName(mailAttachment.getAttachmentName());
	//// DataSource ds = new ByteArrayDataSource(new
	// FileInputStream(mailAttachment.getFileSystemResource().getFile()),
	// "application/octet-stream");
	//// mimeMessage.setDataHandler(new DataHandler(ds));
	//// }
	//
	// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	// mimeMessage.writeTo(outputStream);
	// RawMessage rawMessage = new
	// RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
	//
	// List<String> list = Arrays.asList("neha.pateliya25@gmail.com");
	// SendRawEmailRequest rawEmailRequest = new
	// SendRawEmailRequest(rawMessage);
	// rawEmailRequest.setDestinations(list);
	// rawEmailRequest.setSource("support@health3.in");
	// new
	// AmazonSimpleEmailServiceClient(credentials).sendRawEmail(rawEmailRequest);
	// }catch (Exception ex) {
	// System.out.println("The email was not sent.");
	// System.out.println("Error message: " + ex.getMessage());
	// }
	//
	// GeoApiContext context = new
	// GeoApiContext().setApiKey("AIzaSyCKFWg02TFUWOLsvJt0A6PMI_aAfqfLFwI");
	// try {
	// TimeZone resp = TimeZoneApi.getTimeZone(context, new
	// LatLng(21.125297,79.097443)).await();
	// System.out.println(resp);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new BusinessException(ServiceError.Forbidden, "Couldn't Geocode
	// the location"+e.getMessage());
	// }

	// System.out.println(ImageIO.read(new
	// File("s3.amazonaws.com/healthcoco/profile-image/11Wed Dec 23 11_49_27
	// UTC 2015REPORTS1455775916464.jpeg")));
    }
}

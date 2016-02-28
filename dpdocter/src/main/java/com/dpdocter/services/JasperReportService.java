package com.dpdocter.services;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

public interface JasperReportService {

    String createPDF(Map<String, Object> parameters, String fileName, String layout, String pageSize, String margins, String pdfName);
}

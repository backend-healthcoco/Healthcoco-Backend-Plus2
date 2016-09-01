package com.dpdocter.services;

import java.io.IOException;
import java.util.Map;

import com.dpdocter.response.JasperReportResponse;

public interface JasperReportService {

    JasperReportResponse createPDF(Map<String, Object> parameters, String fileName, String layout, String pageSize, Integer topMargin, Integer bottonMargin, Integer contentFontSize, String pdfName, String... subReportFileName) throws IOException;
}

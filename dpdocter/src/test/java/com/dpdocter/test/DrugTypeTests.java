package com.dpdocter.test;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class DrugTypeTests {

	public static void main(String[] args) throws IOException {
		PDDocument document = PDDocument.load(new File("/home/harish/Desktop/DocxToPdf.pdf"));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		document.getPages().getCount();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < document.getPages().getCount(); i++) {
			// note that the page number parameter is zero based
			BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 500, ImageType.RGB);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			// suffix in filename will be used as the file format
			ImageIOUtil.writeImage(bim, "/home/harish/Desktop/DocxToPdf-" + (i) + ".jpg", 500);
			ImageIOUtil.writeImage(bim, "jpg", outstream);
			list.add("/home/harish/Desktop/DocxToPdf-" + (i) + ".jpg");
			new File("/home/harish/Desktop/DocxToPdf-" + (i) + ".jpg").delete();
		}
		System.out.println(list.size());
		document.close();
	}
}


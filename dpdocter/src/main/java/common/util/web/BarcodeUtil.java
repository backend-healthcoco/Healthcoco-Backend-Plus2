package common.util.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

public class BarcodeUtil {
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static byte[] generateBarcode(String patientBarcodeData) throws WriterException, IOException {
		ByteArrayOutputStream outputStream = null;
		try {
			Code128Writer barcodeWriter = new Code128Writer();
			BitMatrix bitMatrix = barcodeWriter.encode(patientBarcodeData, BarcodeFormat.CODE_128, 300, 100);

			BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

			outputStream = new ByteArrayOutputStream();
			ImageIO.write(barcodeImage, "png", outputStream);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}

}

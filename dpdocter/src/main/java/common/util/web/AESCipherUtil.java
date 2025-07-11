//package common.util.web;
//
//import java.security.Key;
//import java.util.Base64;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.springframework.security.crypto.codec.Base64;
//
//
//public class AESCipherUtil {
//
//	private static final String ALGO = "AES";
//	private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't',
//			'K', 'e', 'y' };
//
//	/**
//	 * Encrypt a string with AES algorithm.
//	 *
//	 * @param data
//	 *            is a string
//	 * @return the encrypted string
//	 */
//	public static String encrypt(String data) throws Exception {
//		Key key = generateKey();
//		Cipher c = Cipher.getInstance(ALGO);
//		c.init(Cipher.ENCRYPT_MODE, key);
//		byte[] encVal = c.doFinal(data.getBytes());
//		return Base64.getEncoder().encodeToString(encVal);
//	}
//
//	/**
//	 * Decrypt a string with AES algorithm.
//	 *
//	 * @param encryptedData
//	 *            is a string
//	 * @return the decrypted string
//	 */
//	public static String decrypt(String encryptedData) throws Exception {
//		Key key = generateKey();
//		Cipher c = Cipher.getInstance(ALGO);
//		c.init(Cipher.DECRYPT_MODE, key);
//		byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
//		byte[] decValue = c.doFinal(decordedValue);
//		return new String(decValue);
//	}
//	
//	
//	/**
//	 * Encrypt a string with AES algorithm.
//	 *
//	 * @param data
//	 *            is a string
//	 *            
//	 * @param customKeyValue
//	 *            is a byte array
//	 * @return the encrypted string
//	 */
//	public static String encrypt(String data , byte[] customKeyValue) throws Exception {
//		Key key = generateKey(customKeyValue);
//		Cipher c = Cipher.getInstance(ALGO);
//		c.init(Cipher.ENCRYPT_MODE, key);
//		byte[] encVal = c.doFinal(data.getBytes());
//		return Base64.getEncoder().encodeToString(encVal);
//	}
//
//	/**
//	 * Decrypt a string with AES algorithm.
//	 *
//	 * @param encryptedData
//	 *            is a string
//	 *  @param customKeyValue
//	 *            is a byte array
//	 * @return the decrypted string
//	 */
//	public static String decrypt(String encryptedData , byte[] customKeyValue) throws Exception {
//		Key key = generateKey(customKeyValue);
//		Cipher c = Cipher.getInstance(ALGO);
//		c.init(Cipher.DECRYPT_MODE, key);
//		byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
//		byte[] decValue = c.doFinal(decordedValue);
//		return new String(decValue);
//	}
//
//	/**
//	 * Generate a new encryption key.
//	 */
//	private static Key generateKey() throws Exception {
//		return new SecretKeySpec(keyValue, ALGO);
//	}
//	
//	
//	private static Key generateKey(byte[] customKeyValue) throws Exception {
//		return new SecretKeySpec(keyValue, ALGO);
//	}
//
//	public static void main(String[] args) throws Exception {
//		String plainText = "asrwsv2124rfdsdafv";
//		String encodedText = encrypt(plainText);
//		String decodedText = decrypt(encodedText);
//	}
//
//}

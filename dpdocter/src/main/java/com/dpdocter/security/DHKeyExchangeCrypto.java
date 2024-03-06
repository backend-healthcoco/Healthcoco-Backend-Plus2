package com.dpdocter.security;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import com.dpdocter.beans.DataEncryptionResponse;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class DHKeyExchangeCrypto {
    public static final String ALGORITHM = "ECDH";
    public static final String CURVE = "curve25519";
    public static final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
   
    // Driver function
    public static DataEncryptionResponse convert(String strToPerformActionOn,String nounce,String keypair,boolean b, String sharedNounce,String receiverPrivateKey1) throws Exception {
    	DataEncryptionResponse response=new DataEncryptionResponse();
    	Security.addProvider(new BouncyCastleProvider());

        System.out.println("Details");
        System.out.println("ALGORITHM: " + ALGORITHM);
        System.out.println("CURVE: " + CURVE);
        System.out.println("DATA: " + strToPerformActionOn);
        System.out.println("<---------------- BEGIN ------------------->");
        System.out.println("\n");

   String     strToPerformActionOn1="{\n" + 
   		"        \"id\": \"Prescrip-dbf18d24-9d81-4212-8225-f1dfee8fd76d\",\n" + 
   		"        \"timestamp\": \"2021-01-06T16:02:10.010+05:30\",\n" + 
   		"        \"entry\": [\n" + 
   		"            {\n" + 
   		"                \"fullUrl\": \"Composition/8518720\",\n" + 
   		"                \"resource\": {\n" + 
   		"                    \"author\": [\n" + 
   		"                        {\n" + 
   		"                            \"reference\": \"Practitioner/8518720\"\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"title\": \"Prescription Recordd\",\n" + 
   		"                    \"status\": \"final\",\n" + 
   		"                    \"subject\": {\n" + 
   		"                        \"reference\": \"Patient/8518720\"\n" + 
   		"                    },\n" + 
   		"                    \"type\": {\n" + 
   		"                        \"text\": \"Prescription record\",\n" + 
   		"                        \"coding\": [\n" + 
   		"                            {\n" + 
   		"                                \"system\": \"http://snomed.info/sct\",\n" + 
   		"                                \"display\": \"Prescription record\",\n" + 
   		"                                \"code\": \"440545006\"\n" + 
   		"                            }\n" + 
   		"                        ]\n" + 
   		"                    },\n" + 
   		"                    \"date\": \"2021-01-09T16:02:10.010+05:30\",\n" + 
   		"                    \"identifier\": {\n" + 
   		"                        \"system\": \"https://ndhm.in/phr\",\n" + 
   		"                        \"value\": \"63399db7-af19-4a58-a3d6-302b947e6709\"\n" + 
   		"                    },\n" + 
   		"                    \"section\": [\n" + 
   		"                        {\n" + 
   		"                            \"title\": \"Prescription recordd\",\n" + 
   		"                            \"entry\": [\n" + 
   		"                                {\n" + 
   		"                                    \"reference\": \"MedicationRequest/16917053\"\n" + 
   		"                                }\n" + 
   		"                            ],\n" + 
   		"                            \"code\": {\n" + 
   		"                                \"coding\": [\n" + 
   		"                                    {\n" + 
   		"                                        \"system\": \"http://snomed.info/sct\",\n" + 
   		"                                        \"display\": \"Prescription record\",\n" + 
   		"                                        \"code\": \"440545006\"\n" + 
   		"                                    }\n" + 
   		"                                ]\n" + 
   		"                            }\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"resourceType\": \"Composition\"\n" + 
   		"                }\n" + 
   		"            },\n" + 
   		"            {\n" + 
   		"                \"fullUrl\": \"MedicationRequest/16917053\",\n" + 
   		"                \"resource\": {\n" + 
   		"                    \"requester\": {\n" + 
   		"                        \"reference\": \"Practitioner/8518720\"\n" + 
   		"                    },\n" + 
   		"                    \"status\": \"active\",\n" + 
   		"                    \"subject\": {\n" + 
   		"                        \"reference\": \"Patient/8518720\"\n" + 
   		"                    },\n" + 
   		"                    \"authoredOn\": \"2021-01-05\",\n" + 
   		"                    \"medicationCodeableConcept\": {\n" + 
   		"                        \"text\": \"TAB.AZITHROMYCIN 250MG\"\n" + 
   		"                    },\n" + 
   		"                    \"intent\": \"order\",\n" + 
   		"                    \"dosageInstruction\": [\n" + 
   		"                        {\n" + 
   		"                            \"text\": \"Take two tablets orally with or after meal once a day\"\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"reasonReference\": [\n" + 
   		"                        {\n" + 
   		"                            \"reference\": \"Condition/8518720\"\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"resourceType\": \"MedicationRequest\"\n" + 
   		"                }\n" + 
   		"            },\n" + 
   		"            {\n" + 
   		"                \"fullUrl\": \"Practitioner/8518720\",\n" + 
   		"                \"resource\": {\n" + 
   		"                    \"name\": [\n" + 
   		"                        {\n" + 
   		"                            \"text\": \"Dr.Cello Pen\"\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"identifier\": [\n" + 
   		"                        {\n" + 
   		"                            \"system\": \"healthcoco\",\n" + 
   		"                            \"value\": \"-\",\n" + 
   		"                            \"type\": {\n" + 
   		"                                \"coding\": [\n" + 
   		"                                    {\n" + 
   		"                                        \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n" + 
   		"                                        \"display\": \"Medical License number\",\n" + 
   		"                                        \"code\": \"MD\"\n" + 
   		"                                    }\n" + 
   		"                                ]\n" + 
   		"                            }\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"resourceType\": \"Practitioner\"\n" + 
   		"                }\n" + 
   		"            },\n" + 
   		"            {\n" + 
   		"                \"fullUrl\": \"Patient/8518720\",\n" + 
   		"                \"resource\": {\n" + 
   		"                    \"identifier\": [\n" + 
   		"                        {\n" + 
   		"                            \"system\": \"healthcoco\",\n" + 
   		"                            \"value\": \"1735955\",\n" + 
   		"                            \"type\": {\n" + 
   		"                                \"coding\": [\n" + 
   		"                                    {\n" + 
   		"                                        \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n" + 
   		"                                        \"display\": \"Medical record number\",\n" + 
   		"                                        \"code\": \"MR\"\n" + 
   		"                                    }\n" + 
   		"                                ]\n" + 
   		"                            }\n" + 
   		"                        }\n" + 
   		"                    ],\n" + 
   		"                    \"resourceType\": \"Patient\"\n" + 
   		"                }\n" + 
   		"            },\n" + 
   		"            {\n" + 
   		"                \"fullUrl\": \"Condition/8518720\",\n" + 
   		"                \"resource\": {\n" + 
   		"                    \"subject\": {\n" + 
   		"                        \"reference\": \"Patient/8518720\"\n" + 
   		"                    },\n" + 
   		"                    \"clinicalStatus\": {\n" + 
   		"                        \"coding\": [\n" + 
   		"                            {\n" + 
   		"                                \"system\": \"http://terminology.hl7.org/CodeSystem/condition-clinical\",\n" + 
   		"                                \"display\": \"Active\",\n" + 
   		"                                \"code\": \"active\"\n" + 
   		"                            }\n" + 
   		"                        ]\n" + 
   		"                    },\n" + 
   		"                    \"code\": {\n" + 
   		"                        \"text\": \"Cold and Cough 3 days\"\n" + 
   		"                    },\n" + 
   		"                    \"resourceType\": \"Condition\"\n" + 
   		"                }\n" + 
   		"            }\n" + 
   		"        ],\n" + 
   		"        \"type\": \"document\",\n" + 
   		"        \"identifier\": {\n" + 
   		"            \"system\": \"http://hip.in\",\n" + 
   		"            \"value\": \"c1f656a7-ddb5-46f8-a9c6-afaba4bdc599\"\n" + 
   		"        },\n" + 
   		"        \"meta\": {\n" + 
   		"            \"lastUpdated\": \"2021-01-06T16:02:10.010\",\n" + 
   		"            \"versionId\": \"1\",\n" + 
   		"            \"security\": [\n" + 
   		"                {\n" + 
   		"                    \"system\": \"http://terminology.hl7.org/CodeSystem/v3-Confidentiality\",\n" + 
   		"                    \"display\": \"very restricted\",\n" + 
   		"                    \"code\": \"V\"\n" + 
   		"                }\n" + 
   		"            ],\n" + 
   		"            \"profile\": [\n" + 
   		"                \"https://nrces.in/ndhm/fhir/r4/StructureDefinition/DocumentBundle\"\n" + 
   		"            ]\n" + 
   		"        },\n" + 
   		"        \"resourceType\": \"Bundle\"\n" + 
   		"    }";
        // Generate the DH keys for sender and receiver
        KeyPair receiverKeyPair = generateKeyPair();
        String receiverPrivateKey = getBase64String(getEncodedPrivateKey(receiverKeyPair.getPrivate()));
        //KeyMaterial.dhPublicKey.keyValue from POST
        String receiverPublicKey =keypair;
        KeyPair senderKeyPair = generateKeyPair();
        String senderPrivateKey = getBase64String(getEncodedPrivateKey(senderKeyPair.getPrivate()));
        String senderPublicKey = getBase64String(getEncodedPublicKey(senderKeyPair.getPublic()));
        String senderPublicKey1 = getBase64String(getEncodedPublicKeyForProjectEKAHIU(senderKeyPair.getPublic()));
        String senderPrivateKey1 = getBase64String(getEncodedPrivateKey(senderKeyPair.getPrivate()));
        // Generate random key for sender and receiver
        String randomSender = generateRandomKey();
        // nonce from POST
        String randomReceiver = nounce;

        // Generating Xor of random Keys
        byte[] xorOfRandom = xorOfRandom(randomSender, randomReceiver);
        String encryptedData=null;
        String decryptedData=null;
        if(b==false) {
        encryptedData = encrypt(xorOfRandom, senderPrivateKey1, receiverPublicKey, strToPerformActionOn);
        }
        else {
        	 byte[] xorOfRandom1 = xorOfRandom(randomReceiver, sharedNounce);
        decryptedData = decrypt(xorOfRandom1,receiverPrivateKey1, receiverPublicKey, strToPerformActionOn);
        }
        System.out.println("\n");
        // POST in content
        System.out.println("encrypted Data: " + encryptedData);
        //System.out.println("decrypted data: " + decryptedData);
        //System.out.println("senderKeyPair DH Key: "+senderPublicKey);
        
        //POST in keyValue
        System.out.println("senderKeyPair DH Key 1: "+senderPublicKey1);
        //POST in Nonce
        System.out.println("generateRandomKey Nonce: "+randomSender);
        System.out.println("\n");
        System.out.println("<---------------- DONE ------------------->");
        response.setEncryptedData(encryptedData);
        response.setDecryptedData(decryptedData);
        response.setRandomSender(randomSender);
        response.setRandomReceiver(randomReceiver);
        response.setSenderPublicKey(senderPublicKey1);
        return response;
    }

    // Method for encryption
    public static String encrypt(byte[] xorOfRandom, String senderPrivateKey, String receiverPublicKey, String stringToEncrypt) throws Exception {
        System.out.println("<------------------- ENCRYPTION -------------------->");
        // Generating shared secret
        String sharedKey = doECDH(getBytesForBase64String(senderPrivateKey), getBytesForBase64String(receiverPublicKey));
        System.out.println("Shared key: " + sharedKey);

        // Generating iv and HKDF-AES key
        byte[] iv = Arrays.copyOfRange(xorOfRandom, xorOfRandom.length - 12, xorOfRandom.length);
        byte[] aesKey = generateAesKey(xorOfRandom, sharedKey);
        System.out.println("HKDF AES key: " + getBase64String(aesKey));

        // Perform Encryption
        String encryptedData = "";
        try {
            byte[] stringBytes = stringToEncrypt.getBytes();

            GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            AEADParameters parameters =
                    new AEADParameters(new KeyParameter(aesKey), 128, iv, null);

            cipher.init(true, parameters);
            byte[] plainBytes = new byte[cipher.getOutputSize(stringBytes.length)];
            int retLen = cipher.processBytes
                    (stringBytes, 0, stringBytes.length, plainBytes, 0);
            cipher.doFinal(plainBytes, retLen);

            encryptedData = getBase64String(plainBytes);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println("EncryptedData: " + encryptedData);
        System.out.println("<---------------- Done ------------------->");
        return encryptedData;
    }

    // Method for decryption
    public static String decrypt(byte[] xorOfRandom, String receiverPrivateKey, String senderPublicKey, String stringToDecrypt) throws Exception {
        System.out.println("<------------------- DECRYPTION -------------------->");
        // Generating shared secret
        String sharedKey = doECDH1(getBytesForBase64String(receiverPrivateKey),getBytesForBase64String(senderPublicKey));
        System.out.println("Shared key: " + sharedKey);

        // Generating iv and HKDF-AES key
        byte[] iv = Arrays.copyOfRange(xorOfRandom, xorOfRandom.length - 12, xorOfRandom.length);
        byte[] aesKey = generateAesKey(xorOfRandom, sharedKey);
        System.out.println("HKDF AES key: " + getBase64String(aesKey));

        // Perform Decryption
        String decryptedData = "";
        try {
            byte[] encryptedBytes = getBytesForBase64String(stringToDecrypt);

            GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            AEADParameters parameters =
                    new AEADParameters(new KeyParameter(aesKey), 128, iv, null);

            cipher.init(false, parameters);
            byte[] plainBytes = new byte[cipher.getOutputSize(encryptedBytes.length)];
            int retLen = cipher.processBytes
                    (encryptedBytes, 0, encryptedBytes.length, plainBytes, 0);
            cipher.doFinal(plainBytes, retLen);

            decryptedData = new String(plainBytes);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println("DecryptedData: " + decryptedData);
        System.out.println("<---------------- Done ------------------->");
        return decryptedData;
    }
    // Method for generating random string
    public static String generateRandomKey() {
        byte[] salt = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return getBase64String(salt);
    }

    // Method for generating DH Keys
    public static KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        X9ECParameters ecParameters = CustomNamedCurves.getByName(CURVE);
        ECParameterSpec ecSpec=new ECParameterSpec(ecParameters.getCurve(), ecParameters.getG(),
                ecParameters.getN(), ecParameters.getH(), ecParameters.getSeed());

        keyPairGenerator.initialize(ecSpec, new SecureRandom());
        //System.out.println("DH keys "+ keyPairGenerator.generateKeyPair());
        return keyPairGenerator.generateKeyPair();
    }

    private static PrivateKey loadPrivateKey (byte [] data) throws Exception
    {
        X9ECParameters ecP = CustomNamedCurves.getByName(CURVE);
        ECParameterSpec params=new ECParameterSpec(ecP.getCurve(), ecP.getG(),
                ecP.getN(), ecP.getH(), ecP.getSeed());
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return kf.generatePrivate(privateKeySpec);
    }

    private static PublicKey loadPublicKey (byte [] data) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        X9ECParameters ecP = CustomNamedCurves.getByName(CURVE);
        ECParameterSpec ecNamedCurveParameterSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(),
                ecP.getN(), ecP.getH(), ecP.getSeed());

        return KeyFactory.getInstance(ALGORITHM, PROVIDER)
                .generatePublic(new ECPublicKeySpec(ecNamedCurveParameterSpec.getCurve().decodePoint(data),
                        ecNamedCurveParameterSpec));
    }

    // Method for generating shared secret
    private static String doECDH (byte[] dataPrv, byte[] dataPub) throws Exception
    {
        KeyAgreement ka = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
        ka.init(loadPrivateKey(dataPrv));
        ka.doPhase(loadPublicKey(dataPub), true);
        byte [] secret = ka.generateSecret();
        return getBase64String(secret);
    }
    
    private static String doECDH1 (byte[] dataPrv, byte[] dataPub) throws Exception
    {
        KeyAgreement ka = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
        ka.init(loadPrivateKey(dataPrv));
        ka.doPhase(loadPublicKeyForProjectEKAHIU(dataPub), true);
        byte [] secret = ka.generateSecret();
        return getBase64String(secret);
    }

    // method to perform Xor of random keys
    private static byte [] xorOfRandom(String randomKeySender, String randomKeyReceiver)
    {
        byte[] randomSender = getBytesForBase64String(randomKeySender);
        byte[] randomReceiver = getBytesForBase64String(randomKeyReceiver);

        byte[] out = new byte[randomSender.length];
        for (int i = 0; i < randomSender.length; i++) {
            out[i] = (byte) (randomSender[i] ^ randomReceiver[i%randomReceiver.length]);
        }
        return out;
    }

    // Method for generating HKDF AES key
    private static byte [] generateAesKey(byte[] xorOfRandoms, String sharedKey ){
        byte[] salt = Arrays.copyOfRange(xorOfRandoms, 0, 20);
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
        HKDFParameters hkdfParameters = new HKDFParameters(getBytesForBase64String(sharedKey), salt, null);
        hkdfBytesGenerator.init(hkdfParameters);
        byte[] aesKey = new byte[32];
        hkdfBytesGenerator.generateBytes(aesKey, 0, 32);
        return aesKey;
    }

    public static String getBase64String(byte[] value){

        return new String(org.bouncycastle.util.encoders.Base64.encode(value));
    }

    public static byte[] getBytesForBase64String(String value){
        return org.bouncycastle.util.encoders.Base64.decode(value);
    }

    public static byte [] getEncodedPublicKey(PublicKey key) throws Exception
    {
        ECPublicKey ecKey = (ECPublicKey)key;
        return ecKey.getQ().getEncoded(true);
    }
    
    public byte [] getEncodedPublicKeyHiu(PublicKey key) throws Exception
    {
        ECPublicKey ecKey = (ECPublicKey)key;
        return ecKey.getQ().getEncoded(false);
    }

    public static byte [] getEncodedPrivateKey(PrivateKey key) throws Exception
    {
        ECPrivateKey ecKey = (ECPrivateKey)key;
        return ecKey.getD().toByteArray();
    }

    /*
     If using ProjectEka HIU for the decryption then Please use below methods for converting public keys
    * */
    // Replacement for ------> getEncodedPublicKey
    public static byte[] getEncodedPublicKeyForProjectEKAHIU(PublicKey key){
        ECPublicKey ecKey = (ECPublicKey)key;
        return ecKey.getEncoded();
    }

    // Replacement for ------> loadPublicKey
    private static PublicKey loadPublicKeyForProjectEKAHIU (byte [] data) throws Exception
    {
        KeyFactory ecKeyFac = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(data);
        PublicKey publicKey = ecKeyFac.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }
}
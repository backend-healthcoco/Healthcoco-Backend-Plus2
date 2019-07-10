//package com.dpdocter.beans;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.net.Socket;
//import java.net.SocketAddress;
//import java.net.UnknownHostException;
//import java.security.KeyManagementException;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.Security;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.TrustManagerFactory;
//import javax.xml.bind.annotation.XmlAttachmentRef;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openssl.PEMReader;
//import org.xml.sax.SAXException;
//
//import com.lowagie.text.DocumentException;
//
//
////@XmlRootElement(name="SSLFactoryMongo")
//public class SSLFactoryMongo extends SSLSocketFactory {
//	
//	static {
//	    Security.addProvider(new BouncyCastleProvider());
//	}
//	
//	@XmlAttachmentRef
//	SSLSocketFactory sslSocketFactory = null;
//	
//	public SSLFactoryMongo() throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, NullPointerException, KeyStoreException, CertificateException, SAXException, IOException, DocumentException, ParserConfigurationException {
//		this.sslSocketFactory = createSSLSocketFactory();
//	}
//	SSLSocketFactory createSSLSocketFactory() throws SAXException, IOException, DocumentException, ParserConfigurationException, NoSuchAlgorithmException, NullPointerException, KeyStoreException, UnrecoverableKeyException, CertificateException, KeyManagementException {
//			System.out.println("creating factory");
//			SSLContext sslcontext = SSLContext.getInstance("TLS");
//		  
//			
//			PEMReader reader = new PEMReader(new FileReader("/home/ubuntu/healthcoco.pem"));
//			InputStream inputStream = new FileInputStream(new File("/home/ubuntu/healthcoco.pem"));
//		    Certificate cert =  (Certificate) reader.readObject();        
//
//		    KeyStore keystore = KeyStore.getInstance("JKS");
////		    keystore.load(inputStream, "healthcoco@14718".toCharArray());
//		    keystore.setCertificateEntry("alias", cert);
//		    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//		    kmf.init(keystore, null); 
//			
////			KeyManagerFactory kmf =  KeyManagerFactory.getInstance("SunX509");
////		    KeyStore ks = KeyStore.getInstance("JKS");
////		    ks.load(new FileInputStream("/home/ubuntu/healthcoco.pem"), "healthcoco@14718".toCharArray());
////		    kmf.init(ks, "healthcoco@14718".toCharArray());
//
//		     TrustManagerFactory tmf = TrustManagerFactory
//		            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
//		    tmf.init(keystore);
//		    
//		    
////		    TrustStoreConfiguration ts = TrustStoreConfiguration.
//		    
//		    
//		    TrustManager[] tm = tmf.getTrustManagers();
//
//		    sslcontext.init(kmf.getKeyManagers(), tm, null);
//		    sslSocketFactory = sslcontext.getSocketFactory();
//		    return sslSocketFactory;
//		    }
//	@Override
//	public String[] getDefaultCipherSuites() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public String[] getSupportedCipherSuites() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
//		SocketAddress addr = new InetSocketAddress("10.0.0.238", 27017);
//        Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr);
//        Socket socket = new Socket(proxy);
//        InetSocketAddress dest = new InetSocketAddress("10.0.0.238", 27017);
//        socket.connect(dest);
//        return socket;
//	}
//	@Override
//	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
//			throws IOException, UnknownHostException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}

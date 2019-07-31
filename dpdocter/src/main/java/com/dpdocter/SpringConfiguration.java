//package com.dpdocter;
//
//import java.util.Properties;
//
//import org.apache.velocity.app.VelocityEngine;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SpringConfiguration {
//	@Bean
//	public VelocityEngine velocityEngine() throws Exception {
//		System.out.println("set VelocityEngine");
//	    Properties properties = new Properties();
//	    properties.setProperty("input.encoding", "UTF-8");
//	    properties.setProperty("output.encoding", "UTF-8");
//	    properties.setProperty("resource.loader", "class, file");
//	    properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//	    properties.setProperty("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.FileResourceLoader");
//	    properties.setProperty("file.resource.loader.path","/opt/tomcat/latest/webapps/dpdocter/WEB-INF/classes");
//	    properties.setProperty("class.resource.loader.cache", "false");
//	    properties.setProperty("file.resource.loader.cache", "true");
//
//	    VelocityEngine velocityEngine = new VelocityEngine(properties);
//	    return velocityEngine;
//	}
//}

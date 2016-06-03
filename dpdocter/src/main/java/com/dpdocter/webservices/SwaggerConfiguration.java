//package com.dpdocter.webservices;
//
//import javax.ws.rs.core.Application;
//
//import org.springframework.stereotype.Component;
//
//import io.swagger.jaxrs.config.BeanConfig;
//
///**
// * Configuration bean to set up Swagger.
// */
//@Component
//public class SwaggerConfiguration extends Application {
//
//    public SwaggerConfiguration() {
//        BeanConfig beanConfig = new BeanConfig();
//        beanConfig.setVersion("1.0.2");
//        beanConfig.setSchemes(new String[]{"http"});
//        beanConfig.setHost("localhost:8080");
//        beanConfig.setBasePath("/api");
//        beanConfig.setResourcePackage("com.dpdocter.webservices");
//        beanConfig.setScan(true);
//    }
//
////    @Override
////    public Set<Class<?>> getClasses() {
////        // set your resources here
////    }
//}
////    @Value("${swagger.resourcePackage}")
////    private String resourcePackage;
////
////    @Value("${swagger.basePath}")
////    private String basePath;
////
////    @Value("${swagger.apiVersion}")
////    private String apiVersion;
//
////    @PostConstruct
////    public void init() {
////        final ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
////        scanner.setResourcePackage(resourcePackage);
////
////        ScannerFactory.setScanner(scanner);
////        ClassReaders.setReader(new DefaultJaxrsApiReader());
////        ModelConverters.addConverter(new AccessHiddenModelConverter(), true);
////        FilterFactory.setFilter(new AccessHiddenSpecFilter());
////
////        final SwaggerConfig config = ConfigFactory.config();
////        config.setApiVersion(apiVersion);
////        config.setBasePath(basePath);
////    }
////
////    public String getResourcePackage() {
////        return resourcePackage;
////    }
////
////    public void setResourcePackage(String resourcePackage) {
////        this.resourcePackage = resourcePackage;
////    }
////
////    public String getBasePath() {
////        return basePath;
////    }
////
////    public void setBasePath(String basePath) {
////        this.basePath = basePath;
////    }
////
////    public String getApiVersion() {
////        return apiVersion;
////    }
////
////    public void setApiVersion(String apiVersion) {
////        this.apiVersion = apiVersion;
////    }
////}

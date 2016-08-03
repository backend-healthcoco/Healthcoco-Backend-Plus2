#!/bin/bash

WAR_FILE_PATH="/var/lib/jenkins/jobs/dev-dist/workspace/dpdocter/target/dpdocter.war"
WAR_FILE_DEPLOYMENT_PATH="/var/lib/tomcat7/webapps/dpdocter"
MIREDOT_FILE_PATH="/var/lib/jenkins/jobs/dev-dist/workspace/dpdocter/target/miredot"
MIREDOT_FILE_DEPLOYMENT_PATH="/var/lib/tomcat7/webapps/miredot"
CATALINA_FILE_PATH="/var/lib/tomcat7/logs/catalina.out"

echo Deployment Started...!

service tomcat7 stop &&

rm -r "${WAR_FILE_DEPLOYMENT_PATH/*}" &&

rm "$CATALINA_FILE_PATH" &&

rm -r "${MIREDOT_FILE_DEPLOYMENT_PATH/*}" &&

unzip "$WAR_FILE_PATH" -d "$WAR_FILE_DEPLOYMENT_PATH" &&

cp -r  "$MIREDOT_FILE_PATH" "$MIREDOT_FILE_DEPLOYMENT_PATH" &&

service tomcat7 start && tail -f "$CATALINA_FILE_PATH" &&

echo Deployment Done...!

#!/bin/sh

function replace_properties_in_application {
  log 'replace properties in application.yml'
  perl -i -p -e "s|--DATABASE_USERNAME--|${DATABASE_USERNAME}|g" app/application.yml
  perl -i -p -e "s|--DATABASE_PASSWORD--|${DATABASE_PASSWORD}|g" app/application.yml
  perl -i -p -e "s|--ENVIRONMENT--|${ENVIRONMENT}|g" app/application.yml
  perl -i -p -e "s|--PE_CONNECT_TOKEN_URI--|${PE_CONNECT_TOKEN_URI}|g" app/application.yml
  perl -i -p -e "s|--PE_CONNECT_USER_INFO_URI--|${PE_CONNECT_USER_INFO_URI}|g" app/application.yml
  perl -i -p -e "s|--PE_CONNECT_CLIENT_ID--|${PE_CONNECT_CLIENT_ID}|g" app/application.yml
  perl -i -p -e "s|--PE_CONNECT_CLIENT_SECRET--|${PE_CONNECT_CLIENT_SECRET}|g" app/application.yml
  perl -i -p -e "s|--PE_CONNECT_JWK_SET_URI--|${PE_CONNECT_JWK_SET_URI}|g" app/application.yml
  perl -i -p -e "s|--PE_CONNECT_ISSUER_URI--|${PE_CONNECT_ISSUER_URI}|g" app/application.yml
  perl -i -p -e "s|--POLE_EMPLOI_IO_URI--|${POLE_EMPLOI_IO_URI}|g" app/application.yml
  perl -i -p -e "s|--TOMCAT_THREADS_MAX--|${TOMCAT_THREADS_MAX}|g" app/application.yml
  perl -i -p -e "s|--TOMCAT_THREADS_MIN_SPARE--|${TOMCAT_THREADS_MIN_SPARE}|g" app/application.yml
  perl -i -p -e "s|--SENDINBLUE_API_URL--|${SENDINBLUE_API_URL}|g" app/application.yml
  perl -i -p -e "s|--SENDINBLUE_API_KEY--|${SENDINBLUE_API_KEY}|g" app/application.yml
  perl -i -p -e "s|--SENDINBLUE_CONTACT_LIST_ID--|${SENDINBLUE_CONTACT_LIST_ID}|g" app/application.yml
}

function start_estime_application {
  log 'start Estime application'
  java -cp app:app/lib/* fr.poleemploi.estime.EstimeApplication
}

function start {
  replace_properties_in_application &&
  start_estime_application
}

function log {
  echo "<EVT TIMESTAMP="$(date '+%Y-%m-%d %H:%M:%S,000')" TYPE="INFO"><![CDATA[INFO startup.sh : $1]]></EVT>"
}

start

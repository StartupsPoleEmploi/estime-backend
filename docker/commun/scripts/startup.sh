#!/bin/sh



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
  perl -i -p -e "s|--ESD_COORDONNEES_API_URI--|${ESD_COORDONNEES_API_URI}|g" app/application.yml
  perl -i -p -e "s|--ESD_DATE_NAISSANCE_API_URI--|${ESD_DATE_NAISSANCE_API_URI}|g" app/application.yml
  perl -i -p -e "s|--ESD_DETAIL_INDEMNISATION_API_URI--|${ESD_DETAIL_INDEMNISATION_API_URI}|g" app/application.yml
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
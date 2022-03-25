package fr.poleemploi.estime.clientsexternes.mailjet;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.exceptions.InternalServerException;

@Component
public class MailjetClient {

    @Value("${mailjet-api-url}")
    private String mailjetApiUrl;

    @Value("${mailjet-api-secret-key}")
    private String mailjetApiSecretKey;

    @Value("${mailjet-api-key}")
    private String mailjetApiKey;

    @Value("${mailjet-contact-list-id}")
    private String mailjetContactListId;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailjetClient.class);

    public void addContactMailjet(String email) {
	createContactMailjet(email);
	addContactToListMailjet(email);
    }

    private void createContactMailjet(String email) {
	try {
	    JSONObject createContactPayload = mapCreateContactPayload(email);
	    HttpEntity<String> request = new HttpEntity<>(createContactPayload.toString(), getHeaders());
	    restTemplate.postForObject(String.format("%s/contact", mailjetApiUrl), request, JSONObject.class);
	} catch (Exception e) {
	    if (!isBadRequestHttpClientError(e)) {
		LOGGER.error(String.format(LoggerMessages.MAILJET_CREATION_CONTACT_KO.getMessage(), e.getMessage()));
		throw new InternalServerException(InternalServerMessages.MAILJET_AJOUT_CONTACT_IMPOSSIBLE.getMessage());
	    } else {
		LOGGER.warn(String.format(LoggerMessages.MAILJET_CONTACT_DEJA_ENREGISTRE.getMessage(), email));
	    }

	}
    }

    private JSONObject mapCreateContactPayload(String email) {
	JSONObject createContactPayload = new JSONObject();
	createContactPayload.accumulate("Email", email);
	return createContactPayload;
    }

    private void addContactToListMailjet(String email) {
	try {
	    JSONObject addContactToListPayload = mapAddContactToListPayload(email);
	    HttpEntity<String> request = new HttpEntity<>(addContactToListPayload.toString(), getHeaders());
	    restTemplate.postForObject(String.format("%s/listrecipient", mailjetApiUrl), request, JSONObject.class);
	} catch (Exception e) {
	    if (isBadRequestHttpClientError(e)) {
		LOGGER.warn(String.format(LoggerMessages.MAILJET_AJOUT_CONTACT_A_LA_LISTE_DEJA_ENREGISTRE.getMessage(), email));
		throw new BadRequestException(String.format(BadRequestMessages.MAILJET_CONTACT_DEJA_ENREGISTRE.getMessage(), email));
	    } else {
		LOGGER.error(String.format(LoggerMessages.MAILJET_CREATION_CONTACT_KO.getMessage(), e.getMessage()));
		throw new InternalServerException(InternalServerMessages.MAILJET_AJOUT_CONTACT_IMPOSSIBLE.getMessage());
	    }
	}
    }

    private JSONObject mapAddContactToListPayload(String email) {
	JSONObject addContactToListPayload = new JSONObject();
	addContactToListPayload.accumulate("ContactAlt", email);
	addContactToListPayload.accumulate("ListID", mailjetContactListId);
	return addContactToListPayload;
    }

    private HttpHeaders getHeaders() {
	HttpHeaders headers = new HttpHeaders();
	String encoding = Base64.getEncoder().encodeToString((mailjetApiKey + ":" + mailjetApiSecretKey).getBytes());
	headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
	headers.setContentType(MediaType.APPLICATION_JSON);
	return headers;
    }

    private boolean isBadRequestHttpClientError(Exception exception) {
	return exception instanceof HttpClientErrorException && ((HttpClientErrorException) exception).getRawStatusCode() == HttpStatus.BAD_REQUEST.value();
    }
}

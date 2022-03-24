package fr.poleemploi.estime.clientsexternes.sendinblue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.enumerations.ParcourUtilisateurEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;

@Component
public class SendinblueClient {

    @Value("${sendinblue-api-url}")
    private String sendinblueApiUrl;

    @Value("${sendinblue-api-key}")
    private String sendinblueApiKey;

    @Value("${sendinblue-contact-list-id}")
    private String sendinblueContactListID;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(SendinblueClient.class);

    private static final String ATTRIBUTES_KEY = "attributes";
    private static final String EMAIL_KEY = "email";
    private static final String EMAILS_KEY = "emails";
    private static final String SUIVI_PARCOURS_KEY = "SUIVI_PARCOURS";

    public void addContactSendinblue(String email) {
	createContactSendinblue(email);
	addContactToListSendinblue(email);
    }

    private void createContactSendinblue(String email) {
	try {
	    JSONObject createContactPayload = mapCreateContactPayload(email);
	    HttpEntity<String> request = new HttpEntity<>(createContactPayload.toString(), getHeaders());
	    restTemplate.postForObject(String.format("%s", sendinblueApiUrl), request, JSONObject.class);
	} catch (Exception e) {
	    if (!isBadRequestHttpClientError(e)) {
		LOGGER.warn(String.format(LoggerMessages.SENDINBLUE_CREATE_CONTACT_KO.getMessage(), e.getMessage()));
	    }
	}
    }

    private JSONObject mapCreateContactPayload(String email) {
	JSONObject createContactPayload = new JSONObject();
	JSONObject attributes = new JSONObject();
	attributes.accumulate(SUIVI_PARCOURS_KEY, ParcourUtilisateurEnum.CONNEXION_REUSSIE.getParcours());
	createContactPayload.accumulate(EMAIL_KEY, email);
	createContactPayload.accumulate(ATTRIBUTES_KEY, attributes);
	return createContactPayload;
    }

    private void addContactToListSendinblue(String email) {
	try {
	    JSONObject addContactToListPayload = mapAddContactToListPayload(email);
	    HttpEntity<String> request = new HttpEntity<>(addContactToListPayload.toString(), getHeaders());
	    restTemplate.postForObject(String.format("%s/lists/%s/contacts/add", sendinblueApiUrl, sendinblueContactListID), request, JSONObject.class);
	} catch (Exception e) {
	    if (!isBadRequestHttpClientError(e)) {
		LOGGER.warn(String.format(LoggerMessages.SENDINBLUE_ADD_CONTACT_KO.getMessage(), e.getMessage()));
	    }
	}
    }

    private JSONObject mapAddContactToListPayload(String email) {
	JSONObject addContactToListPayload = new JSONObject();
	JSONArray emails = new JSONArray();
	emails.put(email);
	addContactToListPayload.put(EMAILS_KEY, emails);
	return addContactToListPayload;
    }

    public void updateContactSendinblue(String email, String suiviParcours) {
	try {
	    JSONObject updateContactPayload = mapUpdateContactPayload(suiviParcours);
	    HttpEntity<String> request = new HttpEntity<>(updateContactPayload.toString(), getHeaders());
	    restTemplate.exchange(String.format("%s/%s", sendinblueApiUrl, email), HttpMethod.PUT, request, JSONObject.class);
	} catch (Exception e) {
	    if (!isBadRequestHttpClientError(e)) {
		LOGGER.warn(String.format(LoggerMessages.SENDINBLUE_UPDATE_CONTACT_KO.getMessage(), e.getMessage()));
	    }
	}
    }

    private JSONObject mapUpdateContactPayload(String suiviParcours) {
	JSONObject updateContactPayload = new JSONObject();
	JSONObject attributes = new JSONObject();
	attributes.accumulate(SUIVI_PARCOURS_KEY, suiviParcours);
	updateContactPayload.accumulate(ATTRIBUTES_KEY, attributes);
	return updateContactPayload;
    }

    private HttpHeaders getHeaders() {
	HttpHeaders headers = new HttpHeaders();
	headers.set("api-key", sendinblueApiKey);
	headers.setContentType(MediaType.APPLICATION_JSON);
	return headers;
    }

    private boolean isBadRequestHttpClientError(Exception exception) {
	return exception instanceof HttpClientErrorException && ((HttpClientErrorException) exception).getRawStatusCode() == HttpStatus.BAD_REQUEST.value();
    }
}

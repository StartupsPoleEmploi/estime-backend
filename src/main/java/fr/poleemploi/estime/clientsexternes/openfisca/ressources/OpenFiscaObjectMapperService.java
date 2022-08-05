package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;

@Service
public class OpenFiscaObjectMapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaObjectMapperService.class);

    private OpenFiscaObjectMapperService() {

    }

    public static ObjectMapper getOpenFiscaObjectMapper() {
	ObjectMapper mapper = new ObjectMapper();
	mapper.setSerializationInclusion(Include.NON_NULL);
	mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

	return mapper;
    }

    public static String getJsonStringFromObject(Object object) {
	try {
	    return String.format("%s", getOpenFiscaObjectMapper().writeValueAsString(object));
	} catch (JsonProcessingException e) {
	    String messageError = String.format(LoggerMessages.SERIALIZATION_OPENFISCA_IMPOSSIBLE.getMessage(), e.getMessage());
	    LOGGER.error(messageError);
	    return "";
	}
    }

    public static OpenFiscaRoot getOpenFiscaRootFromJsonString(String jsonString) {
	try {
	    return getOpenFiscaObjectMapper().readValue(jsonString, OpenFiscaRoot.class);
	} catch (JsonProcessingException e) {
	    String messageError = String.format(LoggerMessages.SERIALIZATION_OPENFISCA_IMPOSSIBLE.getMessage(), e.getMessage());
	    LOGGER.error(messageError);
	    return new OpenFiscaRoot();
	}
    }

}

package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class OpenFiscaObjectMapperService {

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
	    e.printStackTrace();
	    return "";
	}
    }

}

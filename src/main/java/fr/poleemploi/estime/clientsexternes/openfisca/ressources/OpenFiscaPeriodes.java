package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic = true)
public class OpenFiscaPeriodes extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
	return OpenFiscaObjectMapperService.getJsonStringFromObject(this);
    }

}

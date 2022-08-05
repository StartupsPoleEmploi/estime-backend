package fr.poleemploi.estime.services.controleurs;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;

@Component
public class IndividuServiceControleur {

    public void controlerDonneesEntreeServiceAuthentifier(String trafficSource) {
	if (trafficSource == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "trafficSource"));
	}
    }

}

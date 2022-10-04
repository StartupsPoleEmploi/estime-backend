package fr.poleemploi.estime.services.controleurs;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.AutresSituations;

@Component
public class AutresSituationsServiceControleur {

    public void controlerDonneesEntreeServiceAutresSituations(String idEstime, AutresSituations autresSituations) {
	if (idEstime == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "idEstime"));
	}
	if (autresSituations == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "autresSituations"));
	} else {
	    if (autresSituations.isAutre() && (autresSituations.getAutreContenu() == null || autresSituations.getAutreContenu().isBlank())) {
		throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "autreContenu"));
	    }
	}
    }
}

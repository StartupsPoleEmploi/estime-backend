package fr.poleemploi.estime.services.controleurs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.exceptions.BadRequestException;

@Component
public class AideServiceControleur {

    @Autowired
    private AideUtile aideUtile;

    public void controlerDonneesEntreeServiceAuthentifier(String codeAide) {
	if (codeAide == null || codeAide.isBlank()) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "codeAide"));
	}

	if(aideUtile.isCodeAideNotExit(codeAide)) {
	    throw new BadRequestException(String.format(BadRequestMessages.CODE_AIDE_INCORRECT.getMessage(), aideUtile.getListeFormateeCodesAidePossibles()));
	}        
    }
}

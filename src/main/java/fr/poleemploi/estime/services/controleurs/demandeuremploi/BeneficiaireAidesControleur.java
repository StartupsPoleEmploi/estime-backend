package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class BeneficiaireAidesControleur {

    @Autowired
    private RessourcesFinancieresControleur ressourcesFinancieresControleur;

    public void controlerDonnees(DemandeurEmploi demandeurEmploi) {
	BeneficiaireAides beneficiaireAides = demandeurEmploi.getBeneficiaireAides();
	if(beneficiaireAides == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "beneficiaireAides"));
	}
	if(beneficiaireAides.isBeneficiaireAAH()) {
	    ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsCafAAH(demandeurEmploi.getRessourcesFinancieres());
	}
	if(beneficiaireAides.isBeneficiaireRSA()) {
	    ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsCafRSA(demandeurEmploi);
	}
	if(beneficiaireAides.isBeneficiaireASS()) {
	    ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsPoleEmploiASS(demandeurEmploi.getRessourcesFinancieres());
	}
    }
}

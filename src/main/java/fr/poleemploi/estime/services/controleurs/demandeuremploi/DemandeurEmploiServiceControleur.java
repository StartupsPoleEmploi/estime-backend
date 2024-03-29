package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;

@Component
public class DemandeurEmploiServiceControleur {

    @Autowired
    private FuturTravailControleur futurTravailControleur;

    @Autowired
    private BeneficiaireAidesControleur beneficiaireAidesControleur;

    @Autowired
    private InformationsPersonnellesControleur informationsPersonnellesControleur;

    @Autowired
    private SituationFamilialeControleur situationFamilialeControleur;

    public void controlerDonneesEntreeServiceCreerDemandeurEmploi(Individu individu) {
	if (individu == null) {
	    throw new BadRequestException(BadRequestMessages.INDIVIDU_OBLIGATOIRE.getMessage());
	}
    }

    public void controlerDonneesEntreeServiceSimulerMesAides(DemandeurEmploi demandeurEmploi) {
	if (demandeurEmploi == null) {
	    throw new BadRequestException(BadRequestMessages.DEMANDEUR_EMPLOI_OBLIGATOIRE.getMessage());
	} else {
	    futurTravailControleur.controlerDonnees(demandeurEmploi.getFuturTravail());
	    beneficiaireAidesControleur.controlerDonnees(demandeurEmploi);
	    informationsPersonnellesControleur.controlerDonnees(demandeurEmploi.getInformationsPersonnelles());
	    situationFamilialeControleur.controlerDonnees(demandeurEmploi.getSituationFamiliale(), demandeurEmploi.getBeneficiaireAides());
	}
    }

    public void controlerDonneesEntreeServiceSimulerComplementARE(DemandeurEmploi demandeurEmploi) {
	if (demandeurEmploi == null) {
	    throw new BadRequestException(BadRequestMessages.DEMANDEUR_EMPLOI_OBLIGATOIRE.getMessage());
	} else {
	    futurTravailControleur.controlerDonneesComplementARE(demandeurEmploi.getFuturTravail());
	}
    }
}

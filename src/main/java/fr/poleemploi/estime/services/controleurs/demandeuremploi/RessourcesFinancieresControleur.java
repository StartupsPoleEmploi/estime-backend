package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@Component
public class RessourcesFinancieresControleur {

    private static final String MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE = "ressourcesFinancieres dans DemandeurEmploi";

    public void controlerDemandeurEmploiAllocationsPoleEmploiASS(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	if (ressourcesFinancieres == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
	}
	AidesPoleEmploi aidesPoleEmploi = ressourcesFinancieres.getAidesPoleEmploi();
	if (aidesPoleEmploi == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "aidesPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
	}
	if (aidesPoleEmploi.getAllocationASS() != null) {
	    if (aidesPoleEmploi.getAllocationASS().getAllocationJournaliereNet() <= 0) {
		throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationJournaliereNetASS"));
	    }
	}
	controlerHasTravailleAvantSimulation(ressourcesFinancieres);
    }

    public void controlerDemandeurEmploiAllocationsCafAAH(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	if (ressourcesFinancieres == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
	}
	AidesCAF aidesCAF = ressourcesFinancieres.getAidesCAF();
	if (aidesCAF == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
	}
	if (aidesCAF.getAllocationAAH() != null && aidesCAF.getAllocationAAH() <= 0) {
	    throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetAAH"));
	}
	controlerHasTravailleAvantSimulation(ressourcesFinancieres);
    }

    public void controlerDemandeurEmploiAllocationsCafRSA(DemandeurEmploi demandeurEmploi) {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
	if (ressourcesFinancieres == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
	}
	AidesCAF aidesCAF = ressourcesFinancieres.getAidesCAF();
	if (aidesCAF == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
	}
	if (aidesCAF.getAllocationRSA() == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationMensuelleNetRSA"));
	}
	if (aidesCAF.getAllocationRSA() <= 0) {
	    throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetRSA"));
	}
	if (aidesCAF.getProchaineDeclarationTrimestrielle() == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(),
		    "prochaineDeclarationTrimestrielle dans allocationsCAF de RessourcesFinancieres de DemandeurEmploi"));
	}
	if (aidesCAF.getProchaineDeclarationTrimestrielle() < 0 || aidesCAF.getProchaineDeclarationTrimestrielle() >= 4) {
	    throw new BadRequestException(
		    String.format(BadRequestMessages.VALEUR_INCORRECT_PROCHAINE_DECLARATION_TRIMESTRIELLE.getMessage(), aidesCAF.getProchaineDeclarationTrimestrielle()));
	}
	controlerHasTravailleAvantSimulation(ressourcesFinancieres);
    }

    private void controlerHasTravailleAvantSimulation(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	if (ressourcesFinancieres.getHasTravailleAuCoursDerniersMois() == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "hasTravailleAuCoursDerniersMois dans RessourcesFinancieres"));
	}

	if (ressourcesFinancieres.getHasTravailleAuCoursDerniersMois() != null && ressourcesFinancieres.getHasTravailleAuCoursDerniersMois().booleanValue()
		&& ressourcesFinancieres.getPeriodeTravailleeAvantSimulation() == null) {
	    throw new BadRequestException(
		    String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "periodeTravailleeAvantSimulation dans RessourcesFinancieres de DemandeurEmploi"));
	}
    }
}

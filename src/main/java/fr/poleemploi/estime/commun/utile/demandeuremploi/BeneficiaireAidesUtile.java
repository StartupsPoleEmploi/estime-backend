package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class BeneficiaireAidesUtile {

    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE = 29.56f;
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE = 14.77f;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    public boolean isBeneficiaireAides(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null;
    }

    public boolean isBeneficiaireAidePEouCAF(DemandeurEmploi demandeurEmploi) {
	return isBeneficiaireAideMinimaSocial(demandeurEmploi) || isBeneficiaireAREMini(demandeurEmploi);
    }

    public boolean isBeneficiaireARE(Personne personne) {
	return personne.getBeneficiaireAides() != null && personne.getBeneficiaireAides().isBeneficiaireARE();
    }

    public boolean isBeneficiaireARE(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null && demandeurEmploi.getBeneficiaireAides().isBeneficiaireARE();
    }

    public boolean isBeneficiaireASS(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null && demandeurEmploi.getBeneficiaireAides().isBeneficiaireASS();
    }

    public boolean isBeneficiaireASS(Personne personne) {
	return personne.getBeneficiaireAides() != null && personne.getBeneficiaireAides().isBeneficiaireASS();
    }

    public boolean isBeneficiaireAAH(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null && demandeurEmploi.getBeneficiaireAides().isBeneficiaireAAH();
    }

    public boolean isUniquementBeneficiaireAAH(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null && demandeurEmploi.getBeneficiaireAides().isBeneficiaireAAH()
		&& !demandeurEmploi.getBeneficiaireAides().isBeneficiaireASS() && !demandeurEmploi.getBeneficiaireAides().isBeneficiaireRSA()
		&& !demandeurEmploi.getBeneficiaireAides().isBeneficiaireARE();
    }

    public boolean isBeneficiairePensionInvalidite(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides().isBeneficiairePensionInvalidite();
    }

    public boolean isBeneficiaireAideMinimaSocial(DemandeurEmploi demandeurEmploi) {
	return isBeneficiaireAides(demandeurEmploi) && (demandeurEmploi.getBeneficiaireAides().isBeneficiaireAAH() || demandeurEmploi.getBeneficiaireAides().isBeneficiaireASS()
		|| demandeurEmploi.getBeneficiaireAides().isBeneficiaireRSA());
    }

    private boolean isBeneficiaireAREMini(DemandeurEmploi demandeurEmploi) {
	if (isBeneficiaireAides(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute() != null) {
	    float allocationJournaliereBrute = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute();
	    return (!informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && allocationJournaliereBrute <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE)
		    || (informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && allocationJournaliereBrute <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE);
	}
	return false;
    }

    public boolean isBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
	return isBeneficiaireAidePEouCAF(demandeurEmploi) && isFoyerBeneficiaireRSA(demandeurEmploi);
    }

    private boolean isFoyerBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
	return isDemandeurBeneficiaireRSA(demandeurEmploi) || isPersonneAChargeBeneficiaireRSA(demandeurEmploi) || isConjointBeneficiaireRSA(demandeurEmploi);
    }

    private boolean isDemandeurBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null && demandeurEmploi.getBeneficiaireAides().isBeneficiaireRSA();
    }

    private boolean isPersonneAChargeBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
	boolean isPersonneAChargeBeneficiaireRSA = false;
	if (situationFamilialeUtile.hasPersonnesACharge(demandeurEmploi)) {
	    for (Personne personneACharge : demandeurEmploi.getSituationFamiliale().getPersonnesACharge()) {
		if (personneACharge.getBeneficiaireAides() != null && personneACharge.getBeneficiaireAides().isBeneficiaireRSA()) {
		    isPersonneAChargeBeneficiaireRSA = true;
		    break;
		}
	    }
	}
	return isPersonneAChargeBeneficiaireRSA;
    }

    private boolean isConjointBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
	boolean isConjointBeneficiaireRSA = false;
	if (situationFamilialeUtile.isEnCouple(demandeurEmploi)) {
	    Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
	    isConjointBeneficiaireRSA = conjoint.getBeneficiaireAides() != null && conjoint.getBeneficiaireAides().isBeneficiaireRSA();
	}
	return isConjointBeneficiaireRSA;
    }

    public boolean isBeneficiaireAidesFamiliales(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales() != null
		&& (demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getComplementFamilial() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant() > 0);
    }
}

package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class BeneficiaireAidesUtile {

    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE = 29.38f;
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE = 14.68f;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    public boolean isBeneficiaireAides(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getBeneficiaireAides() != null;
    }

    public boolean isBeneficiaireAidePEouCAF(DemandeurEmploi demandeurEmploi) {
	return isBeneficiaireAideMinimaSocial(demandeurEmploi) || isBeneficiaireARE(demandeurEmploi);
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

    private boolean isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(DemandeurEmploi demandeurEmploi) {

	if (isBeneficiaireAides(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereNet() != null) {
	    float indemnisationJournaliereNet = demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereNet();
	    return (!informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && indemnisationJournaliereNet <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE)
		    || (informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && indemnisationJournaliereNet <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE);
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
	return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales() != null
		&& (demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales() > 0
			|| demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial() > 0
			|| demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getComplementFamilial() > 0
			|| demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant() > 0);
    }
}

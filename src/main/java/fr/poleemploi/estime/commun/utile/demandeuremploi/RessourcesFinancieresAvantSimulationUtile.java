package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@Component
public class RessourcesFinancieresAvantSimulationUtile {

    @Autowired
    DateUtile dateUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private AreUtile areUtile;

    public float calculerMontantRessourcesFinancieresMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
	BigDecimal montantTotal = BigDecimal.ZERO;
	LocalDate moisAvantSimulation = dateUtile.enleverMoisALocalDate(dateUtile.getDateJour(), 1);

	montantTotal = montantTotal.add(BigDecimal.valueOf(calculerMontantAidesMoisAvantSimulation(demandeurEmploi, moisAvantSimulation)));
	montantTotal = montantTotal.add(BigDecimal.valueOf(calculerMontantRevenusMoisAvantSimulation(demandeurEmploi)));

	return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
    }

    private float calculerMontantAidesMoisAvantSimulation(DemandeurEmploi demandeurEmploi, LocalDate moisAvantSimulation) {
	BigDecimal montantTotal = BigDecimal.ZERO;
	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(allocationSolidariteSpecifiqueUtile.calculerMontant(demandeurEmploi, moisAvantSimulation)));
	}
	if (beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(areUtile.calculerMontantAreAvantSimulation(demandeurEmploi, moisAvantSimulation)));
	}
	if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationAAH()));
	}
	if (beneficiaireAidesUtile.isBeneficiairePensionInvalidite(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite()));
	    montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getAllocationSupplementaireInvalidite()));
	}
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationRSA()));
	}
	if (hasAllocationsFamiliales(demandeurEmploi)) {
	    montantTotal = montantTotal
		    .add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales()));
	}
	if (hasComplementFamilial(demandeurEmploi)) {
	    montantTotal = montantTotal
		    .add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getComplementFamilial()));
	}
	if (hasAllocationSoutienFamilial(demandeurEmploi)) {
	    montantTotal = montantTotal
		    .add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial()));
	}
	if (hasPrestationAccueilJeuneEnfant(demandeurEmploi)) {
	    montantTotal = montantTotal
		    .add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant()));
	}
	if (hasPensionsAlimentaires(demandeurEmploi)) {
	    montantTotal = montantTotal
		    .add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer()));
	}
	if (hasAidePersonnaliseeLogement(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(
		    getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement())));
	}
	if (hasAllocationLogementFamiliale(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(
		    getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale())));
	}
	if (hasAllocationLogementSociale(demandeurEmploi)) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(
		    getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale())));
	}
	return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
    }

    private float calculerMontantRevenusMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
	BigDecimal montantTotal = BigDecimal.ZERO;
	if (hasRevenusMicroEntreprise(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(getRevenusMicroEntrepriseSur1Mois(demandeurEmploi)));
	}
	if (hasRevenusTravailleurIndependant(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(getRevenusTravailleurIndependantSur1Mois(demandeurEmploi)));
	}
	if (hasRevenusImmobilier(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    montantTotal = montantTotal.add(BigDecimal.valueOf(getRevenusImmobilierSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation())));
	}
	return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
    }

    public float getAllocationsRSANet(DemandeurEmploi demandeurEmploi) {
	if (hasAllocationsRSA(demandeurEmploi)) {
	    return demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationRSA();
	}
	return 0;
    }

    public float getAllocationsLogementSur1Mois(AllocationsLogement allocationsLogement) {
	return BigDecimal.valueOf(allocationsLogement.getMoisNMoins1()).floatValue();
    }

    public float getFuturSalaire(DemandeurEmploi demandeurEmploi) {
	float montantFuturSalaire = 0;
	if (hasFuturSalaire(demandeurEmploi)) {
	    montantFuturSalaire = BigDecimal.valueOf(demandeurEmploi.getFuturTravail().getSalaire().getMontantNet()).floatValue();
	}
	return montantFuturSalaire;
    }

    public float getSalaire(DemandeurEmploi demandeurEmploi) {
	float montantSalaire = 0;
	if (hasSalaire(demandeurEmploi)) {
	    montantSalaire = BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getSalaire().getMontantNet()).floatValue();
	}
	return montantSalaire;
    }

    public float getRevenusImmobilierSur1Mois(DemandeurEmploi demandeurEmploi) {
	float montantRevenusImmobilierSur1Mois = 0;
	if (hasRevenusImmobilier(demandeurEmploi)) {
	    montantRevenusImmobilierSur1Mois = BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getRevenusImmobilier3DerniersMois())
		    .divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).floatValue();
	}
	return montantRevenusImmobilierSur1Mois;
    }

    public float getRevenusImmobilierSur1Mois(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantRevenusImmobilierSur1Mois = 0;
	if (hasRevenusImmobilier(ressourcesFinancieres)) {
	    montantRevenusImmobilierSur1Mois = BigDecimal.valueOf(ressourcesFinancieres.getRevenusImmobilier3DerniersMois()).divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP)
		    .floatValue();
	}
	return montantRevenusImmobilierSur1Mois;
    }

    public float getRevenusImmobilier(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantRevenusImmobilier = 0;
	if (hasRevenusImmobilier(ressourcesFinancieres)) {
	    montantRevenusImmobilier = BigDecimal.valueOf(ressourcesFinancieres.getRevenusImmobilier3DerniersMois()).floatValue();
	}
	return montantRevenusImmobilier;
    }

    public float getRevenusTravailleurIndependantSur1Mois(DemandeurEmploi demandeurEmploi) {
	float montantRevenusIndependantSur1Mois = 0;
	if (hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    montantRevenusIndependantSur1Mois = BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getChiffreAffairesIndependantDernierExercice())
		    .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
	}
	return montantRevenusIndependantSur1Mois;
    }

    public float getRevenusTravailleurIndependantSur1Mois(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantRevenusIndependantSur1Mois = 0;
	if (hasRevenusTravailleurIndependant(ressourcesFinancieres)) {
	    montantRevenusIndependantSur1Mois = BigDecimal.valueOf(ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice())
		    .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
	}
	return montantRevenusIndependantSur1Mois;
    }

    public float getRevenusTravailleurIndependant(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantRevenusIndependant = 0;
	if (hasRevenusTravailleurIndependant(ressourcesFinancieres)) {
	    montantRevenusIndependant = BigDecimal.valueOf(ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice()).floatValue();
	}
	return montantRevenusIndependant;
    }

    public float getRevenusMicroEntrepriseSur1Mois(DemandeurEmploi demandeurEmploi) {
	float montantRevenusMicroEntrepriseSur1Mois = 0;
	if (hasRevenusMicroEntreprise(demandeurEmploi)) {
	    montantRevenusMicroEntrepriseSur1Mois = BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getBeneficesMicroEntrepriseDernierExercice())
		    .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
	}
	return montantRevenusMicroEntrepriseSur1Mois;
    }

    public float getRevenusMicroEntrepriseSur1Mois(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantRevenusMicroEntrepriseSur1Mois = 0;
	if (hasRevenusMicroEntreprise(ressourcesFinancieres)) {
	    montantRevenusMicroEntrepriseSur1Mois = BigDecimal.valueOf(ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice())
		    .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
	}
	return montantRevenusMicroEntrepriseSur1Mois;
    }

    public float getRevenusMicroEntreprise(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantRevenusMicroEntreprise = 0;
	if (hasRevenusMicroEntreprise(ressourcesFinancieres)) {
	    montantRevenusMicroEntreprise = BigDecimal.valueOf(ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice()).floatValue();
	}
	return montantRevenusMicroEntreprise;
    }

    public float getPensionRetraite(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	float montantPensionRetraite = 0;
	if (hasPensionRetraite(ressourcesFinancieres)) {
	    montantPensionRetraite = BigDecimal.valueOf(ressourcesFinancieres.getPensionRetraite()).floatValue();
	}
	return montantPensionRetraite;
    }

    public AidesFamiliales getAidesFamiliales(DemandeurEmploi demandeurEmploi) {
	if (demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null) {
	    return demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales();
	}
	return null;
    }

    public float getAllocationsFamiliales(DemandeurEmploi demandeurEmploi) {
	float allocationsFamiliales = 0;
	if (getAidesFamiliales(demandeurEmploi) != null) {
	    allocationsFamiliales = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales();
	}
	return allocationsFamiliales;
    }

    public float getComplementFamilial(DemandeurEmploi demandeurEmploi) {
	float complementFamilial = 0;
	if (getAidesFamiliales(demandeurEmploi) != null) {
	    complementFamilial = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getComplementFamilial();
	}
	return complementFamilial;
    }

    public float getAllocationSoutienFamilial(DemandeurEmploi demandeurEmploi) {
	float allocationSoutienFamilial = 0;
	if (getAidesFamiliales(demandeurEmploi) != null) {
	    allocationSoutienFamilial = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial();
	}
	return allocationSoutienFamilial;
    }

    public float getPrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi) {
	float prestationAccueilJeuneEnfant = 0;
	if (getAidesFamiliales(demandeurEmploi) != null) {
	    prestationAccueilJeuneEnfant = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant();
	}
	return prestationAccueilJeuneEnfant;
    }

    public float getPensionInvalidite(DemandeurEmploi demandeurEmploi) {
	float pensionInvalidite = 0;
	if (hasPensionInvalidite(demandeurEmploi)) {
	    pensionInvalidite = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite();
	}
	return pensionInvalidite;
    }

    public float getAllocationSupplementaireInvalidite(DemandeurEmploi demandeurEmploi) {
	float allocationSupplementaireInvalidite = 0;
	if (hasAllocationSupplementaireInvalidite(demandeurEmploi)) {
	    allocationSupplementaireInvalidite = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getAllocationSupplementaireInvalidite();
	}
	return allocationSupplementaireInvalidite;
    }

    public boolean hasAllocationsCAF(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null;
    }

    public boolean hasAllocationsRSA(DemandeurEmploi demandeurEmploi) {
	if (hasAllocationsCAF(demandeurEmploi)) {
	    return demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationRSA() != null
		    && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationRSA() != 0;
	}
	return false;
    }

    public boolean hasAidesFamiliales(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales() != null;
    }

    public boolean hasAllocationsPoleEmploi(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null;
    }

    public boolean hasAllocationAdultesHandicapes(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationAAH() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationAAH() > 0;
    }

    public boolean hasAidesLogement(DemandeurEmploi demandeurEmploi) {
	return hasAidePersonnaliseeLogement(demandeurEmploi) || hasAllocationLogementFamiliale(demandeurEmploi) || hasAllocationLogementSociale(demandeurEmploi);
    }

    public boolean hasAidePersonnaliseeLogement(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement() != null
		&& (demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement().getMoisNMoins1() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement().getMoisNMoins2() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement().getMoisNMoins3() > 0);
    }

    public boolean hasAllocationLogementFamiliale(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale() != null
		&& (demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale().getMoisNMoins1() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale().getMoisNMoins2() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale().getMoisNMoins3() > 0);
    }

    public boolean hasAllocationLogementSociale(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale() != null
		&& (demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale().getMoisNMoins1() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale().getMoisNMoins2() > 0
			|| demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale().getMoisNMoins3() > 0);
    }

    public boolean hasAllocationSolidariteSpecifique(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getAidesPoleEmploi() != null && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS() != null
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() > 0;
    }

    public boolean hasAllocationARE(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getAidesPoleEmploi() != null && ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE() != null
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute() != null
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute() > 0
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut() != null
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut() > 0
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getNombreJoursRestants() != null
		&& ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getNombreJoursRestants() > 0;
    }

    public boolean hasPensionInvalidite(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite() > 0;
    }

    public boolean hasAllocationSupplementaireInvalidite(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getAllocationSupplementaireInvalidite() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getAllocationSupplementaireInvalidite() > 0;
    }

    public boolean hasRevenusImmobilier(DemandeurEmploi demandeurEmploi) {
	return hasRevenusImmobilier(demandeurEmploi.getRessourcesFinancieresAvantSimulation());
    }

    public boolean hasRevenusImmobilier(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getRevenusImmobilier3DerniersMois() != null && ressourcesFinancieres.getRevenusImmobilier3DerniersMois() > 0;
    }

    public boolean hasRevenusTravailleurIndependant(DemandeurEmploi demandeurEmploi) {
	return hasRevenusTravailleurIndependant(demandeurEmploi.getRessourcesFinancieresAvantSimulation());
    }

    public boolean hasRevenusTravailleurIndependant(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice() != null
		&& ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice() > 0;
    }

    public boolean hasRevenusMicroEntreprise(DemandeurEmploi demandeurEmploi) {
	return hasRevenusMicroEntreprise(demandeurEmploi.getRessourcesFinancieresAvantSimulation());
    }

    public boolean hasRevenusMicroEntreprise(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice() != null
		&& ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice() > 0;
    }

    public boolean hasPensionRetraite(DemandeurEmploi demandeurEmploi) {
	return hasPensionRetraite(demandeurEmploi.getRessourcesFinancieresAvantSimulation());
    }

    public boolean hasPensionRetraite(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getPensionRetraite() != null && ressourcesFinancieres.getPensionRetraite() > 0;
    }

    public boolean hasFuturSalaire(DemandeurEmploi demandeurEmploi) {
	return hasFuturSalaire(demandeurEmploi.getFuturTravail());
    }

    public boolean hasFuturSalaire(FuturTravail futurTravail) {
	return futurTravail != null && futurTravail.getSalaire() != null && futurTravail.getSalaire().getMontantNet() > 0;
    }

    public boolean hasSalaire(DemandeurEmploi demandeurEmploi) {
	return hasSalaire(demandeurEmploi.getRessourcesFinancieresAvantSimulation());
    }

    public boolean hasSalaire(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getSalaire() != null && ressourcesFinancieres.getSalaire().getMontantNet() > 0;
    }

    public boolean hasTravailleAuCoursDerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
	return hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi.getRessourcesFinancieresAvantSimulation());
    }

    public boolean hasTravailleAuCoursDerniersMoisAvantSimulation(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	return ressourcesFinancieres != null && ressourcesFinancieres.getHasTravailleAuCoursDerniersMois() != null
		&& ressourcesFinancieres.getHasTravailleAuCoursDerniersMois().booleanValue();
    }

    public boolean hasAllocationsFamiliales(DemandeurEmploi demandeurEmploi) {
	return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales() != 0;
    }

    public boolean hasComplementFamilial(DemandeurEmploi demandeurEmploi) {
	return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getComplementFamilial() != 0;
    }

    public boolean hasAllocationSoutienFamilial(DemandeurEmploi demandeurEmploi) {
	return hasAidesFamiliales(demandeurEmploi)
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial() != 0;
    }

    public boolean hasPrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi) {
	return hasAidesFamiliales(demandeurEmploi)
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant() != 0;
    }

    public boolean hasPensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
	return hasAidesFamiliales(demandeurEmploi)
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer() != 0;
    }

    public float getMontantAideLogementDeclare(DemandeurEmploi demandeurEmploi) {
	float montant = 0;
	if (hasAidePersonnaliseeLogement(demandeurEmploi))
	    montant = getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement());
	else if (hasAllocationLogementFamiliale(demandeurEmploi))
	    montant = getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale());
	else if (hasAllocationLogementSociale(demandeurEmploi))
	    montant = getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale());
	return montant;
    }

    public String getTypeAideLogementDeclare(DemandeurEmploi demandeurEmploi) {
	String typeAideLogement = "";
	if (hasAidePersonnaliseeLogement(demandeurEmploi))
	    typeAideLogement = AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode();
	else if (hasAllocationLogementFamiliale(demandeurEmploi))
	    typeAideLogement = AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode();
	else if (hasAllocationLogementSociale(demandeurEmploi))
	    typeAideLogement = AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode();
	return typeAideLogement;
    }

    public boolean isAllocationLogementNotEmpty(AllocationsLogement allocationsLogement) {
	return (allocationsLogement != null && (allocationsLogement.getMoisNMoins1() > 0 || allocationsLogement.getMoisNMoins2() > 0 || allocationsLogement.getMoisNMoins3() > 0));
    }

    public int getProchaineDeclarationTrimestrielle(DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = 1;
	if (demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getProchaineDeclarationTrimestrielle() != null) {
	    prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getProchaineDeclarationTrimestrielle();
	}
	return prochaineDeclarationTrimestrielle;
    }
}

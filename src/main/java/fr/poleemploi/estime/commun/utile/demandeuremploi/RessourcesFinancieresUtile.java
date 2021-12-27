package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class RessourcesFinancieresUtile {

    @Autowired
    DateUtile dateUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifique;

    public float calculerMontantRessourcesFinancieresMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
        BigDecimal montantTotal = BigDecimal.ZERO;
        LocalDate moisAvantSimulation = dateUtile.enleverMoisALocalDate(dateUtile.getDateJour(), 1);
        if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(allocationSolidariteSpecifique.calculerMontant(demandeurEmploi, moisAvantSimulation)));
        }
        if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH()));
        }
        if (beneficiaireAidesUtile.isBeneficiairePensionInvalidite(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite()));
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getAllocationSupplementaireInvalidite()));
        }
        if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA()));
        }
        if (hasRevenusMicroEntreprise(demandeurEmploi.getRessourcesFinancieres())) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(getBeneficesMicroEntrepriseSur1Mois(demandeurEmploi.getRessourcesFinancieres())));
        }
        if (hasBeneficesTravailleurIndependant(demandeurEmploi.getRessourcesFinancieres())) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(getChiffreAffairesIndependantSur1Mois(demandeurEmploi.getRessourcesFinancieres())));
        }
        if (hasAllocationsFamiliales(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales()));
        }
        if (hasComplementFamilial(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getComplementFamilial()));
        }
        if (hasAllocationSoutienFamilial(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial()));
        }
        if (hasPrestationAccueilJeuneEnfant(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant()));
        }
        if (hasPensionsAlimentaires(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer()));
        }
        if (hasRevenusImmobilier(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(getRevenusImmobilierSur1Mois(demandeurEmploi.getRessourcesFinancieres())));
        }
        if (hasAidePersonnaliseeLogement(demandeurEmploi)) {
            montantTotal = montantTotal
                    .add(BigDecimal.valueOf(getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement())));
        }
        if (hasAllocationLogementFamiliale(demandeurEmploi)) {
            montantTotal = montantTotal
                    .add(BigDecimal.valueOf(getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale())));
        }
        if (hasAllocationLogementSociale(demandeurEmploi)) {
            montantTotal = montantTotal
                    .add(BigDecimal.valueOf(getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale())));
        }
        return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
    }

    public float getAllocationsRSANet(DemandeurEmploi demandeurEmploi) {
        if (hasAllocationsRSA(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA();
        }
        return 0;
    }

    public float getAllocationsLogementSur1Mois(AllocationsLogement allocationsLogement) {
        return BigDecimal.valueOf(allocationsLogement.getMoisNMoins1()).floatValue();
    }

    public float getRevenusImmobilierSur1Mois(RessourcesFinancieres ressourcesFinancieres) {
        return BigDecimal.valueOf(ressourcesFinancieres.getRevenusImmobilier3DerniersMois()).divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).floatValue();
    }

    public float getBeneficesMicroEntrepriseSur1Mois(RessourcesFinancieres ressourcesFinancieres) {
        return BigDecimal.valueOf(ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice()).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
    }

    public float getChiffreAffairesIndependantSur1Mois(RessourcesFinancieres ressourcesFinancieres) {
        return BigDecimal.valueOf(ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice()).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
    }

    public AidesFamiliales getAidesFamiliales(DemandeurEmploi demandeurEmploi) {
        if (demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales();
        }
        return null;
    }

    public boolean hasAllocationsCAF(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null;
    }

    public boolean hasAllocationsRSA(DemandeurEmploi demandeurEmploi) {
        if (hasAllocationsCAF(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != 0;
        }
        return false;
    }

    public boolean hasAidesFamiliales(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales() != null;
    }

    public boolean hasAllocationsPoleEmploi(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi() != null;
    }

    public boolean hasAllocationAdultesHandicapes(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH() > 0;
    }

    public boolean hasAidesLogement(DemandeurEmploi demandeurEmploi) {
        return hasAidePersonnaliseeLogement(demandeurEmploi) || hasAllocationLogementFamiliale(demandeurEmploi) || hasAllocationLogementSociale(demandeurEmploi);
    }

    public boolean hasAidePersonnaliseeLogement(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement() != null
                && (demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement().getMoisNMoins1() > 0
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement().getMoisNMoins2() > 0
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement().getMoisNMoins3() > 0);
    }

    public boolean hasAllocationLogementFamiliale(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale() != null
                && (demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale().getMoisNMoins1() > 0
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale().getMoisNMoins2() > 0
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale().getMoisNMoins3() > 0);
    }

    public boolean hasAllocationLogementSociale(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale() != null
                && (demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale().getMoisNMoins1() > 0
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale().getMoisNMoins2() > 0
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale().getMoisNMoins3() > 0);
    }

    public boolean hasAllocationSolidariteSpecifique(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null && ressourcesFinancieres.getAidesPoleEmploi() != null && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() > 0;
    }

    public boolean hasBeneficesTravailleurIndependant(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null && ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice() != null
                && ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice() > 0;
    }

    public boolean hasPensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite() > 0;
    }

    public boolean hasRevenusImmobilier(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getRevenusImmobilier3DerniersMois() != null
                && demandeurEmploi.getRessourcesFinancieres().getRevenusImmobilier3DerniersMois() > 0;
    }

    public boolean hasRevenusMicroEntreprise(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null && ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice() != null && ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice() > 0;
    }

    public boolean hasSalaire(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres.getSalaire() != null && ressourcesFinancieres.getSalaire().getMontantNet() > 0;
    }

    public boolean hasTravailleAuCoursDerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois() != null
                && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois().booleanValue();
    }

    public boolean hasAllocationsFamiliales(DemandeurEmploi demandeurEmploi) {
        return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales() != 0;
    }

    public boolean hasComplementFamilial(DemandeurEmploi demandeurEmploi) {
        return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getComplementFamilial() != 0;
    }

    public boolean hasAllocationSoutienFamilial(DemandeurEmploi demandeurEmploi) {
        return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial() != 0;
    }

    public boolean hasPrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi) {
        return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant() != 0;
    }

    public boolean hasPensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
        return hasAidesFamiliales(demandeurEmploi) && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer() != 0;
    }

    public float getMontantAideLogementDeclare(DemandeurEmploi demandeurEmploi) {
        float montant = 0;
        if (hasAidePersonnaliseeLogement(demandeurEmploi))
            montant = getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement());
        else if (hasAllocationLogementFamiliale(demandeurEmploi))
            montant = getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale());
        else if (hasAllocationLogementSociale(demandeurEmploi))
            montant = getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale());
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
}

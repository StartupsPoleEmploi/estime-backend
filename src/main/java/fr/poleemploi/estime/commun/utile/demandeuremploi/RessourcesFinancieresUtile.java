package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
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
        if(beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(allocationSolidariteSpecifique.calculerMontant(demandeurEmploi, moisAvantSimulation)));
        }
        if(beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH()));
        }
        if(beneficiaireAidesUtile.isBeneficiairePensionInvalidite(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite()));
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getAllocationSupplementaireInvalidite()));
        }
        if(beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA()));
        }
        if(hasRevenusImmobilier(demandeurEmploi)) {
            float revenusImmobilierSur1Mois = getRevenusImmobilierSur1Mois(demandeurEmploi.getRessourcesFinancieres());
            montantTotal = montantTotal.add(BigDecimal.valueOf(revenusImmobilierSur1Mois));
        }
        if(hasRevenusMicroEntreprise(demandeurEmploi.getRessourcesFinancieres())) {
            float revenusTravailleurIndependant1Mois = getRevenusMicroEntrepriseSur1Mois(demandeurEmploi.getRessourcesFinancieres());
            montantTotal = montantTotal.add(BigDecimal.valueOf(revenusTravailleurIndependant1Mois));
        }
        if(hasBeneficesTravailleurIndependant(demandeurEmploi.getRessourcesFinancieres())) {
            float revenusTravailleurIndependant1Mois = getBeneficesTravailleurIndependantSur1Mois(demandeurEmploi.getRessourcesFinancieres());
            montantTotal = montantTotal.add(BigDecimal.valueOf(revenusTravailleurIndependant1Mois));
        }
        if(hasAllocationsFamiliales(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales()));
        }
        if(hasComplementFamilial(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getComplementFamilial()));
        }
        if(hasAllocationSoutienFamilial(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial()));
        }
        if(hasPrestationAccueilJeuneEnfant(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant()));
        }
        return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
    }  
    
    public float getAllocationsRSANet(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsRSA(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA();
        }
        return 0;
    }  
    
    public float getRevenusImmobilierSur1Mois(RessourcesFinancieres ressourcesFinancieres) {
        return BigDecimal.valueOf(ressourcesFinancieres.getRevenusImmobilier3DerniersMois()).divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).floatValue();        
    }

    public float getRevenusMicroEntrepriseSur1Mois(RessourcesFinancieres ressourcesFinancieres) {
        return BigDecimal.valueOf(ressourcesFinancieres.getRevenusMicroEntreprise3DerniersMois()).divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP).floatValue();        
    }

    public float getBeneficesTravailleurIndependantSur1Mois(RessourcesFinancieres ressourcesFinancieres) {
        return BigDecimal.valueOf(ressourcesFinancieres.getBeneficesTravailleurIndependantDernierExercice()).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();        
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
        if(hasAllocationsCAF(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != 0;
        }
        return false;
    }  
    
    public boolean hasAidesFamiliales(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales() != null;
    }

    public boolean hasAllocationsPoleEmploi(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi() != null;
    }

    public boolean hasAllocationAdultesHandicapes(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH() > 0;
    }

    public boolean hasAllocationLogement(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres()!= null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationsLogement() != null 
                && (
                        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationsLogement().getMoisNMoins1() > 0 
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationsLogement().getMoisNMoins2() > 0 
                        || demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationsLogement().getMoisNMoins3() > 0 
                        );
    }
    
    public boolean hasAllocationSolidariteSpecifique(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null
                && ressourcesFinancieres.getAidesPoleEmploi() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() > 0;
    }    
    
    public boolean hasBeneficesTravailleurIndependant(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null 
                && ressourcesFinancieres.getBeneficesTravailleurIndependantDernierExercice() != null
                && ressourcesFinancieres.getBeneficesTravailleurIndependantDernierExercice() > 0;
    }

    public boolean hasPensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer() > 0;
    }
    
    public boolean hasPensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite() > 0;
    }

    public boolean hasRevenusImmobilier(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getRevenusImmobilier3DerniersMois() != null
                && demandeurEmploi.getRessourcesFinancieres().getRevenusImmobilier3DerniersMois() > 0;
    }

    public boolean hasRevenusMicroEntreprise(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null 
                && ressourcesFinancieres.getRevenusMicroEntreprise3DerniersMois() != null
                && ressourcesFinancieres.getRevenusMicroEntreprise3DerniersMois() > 0;
    }
    
    public boolean hasSalaire(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres.getSalaire() != null && ressourcesFinancieres.getSalaire().getMontantNet() > 0;
    }
    
    public boolean hasTravailleAuCoursDerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois().booleanValue();
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

}

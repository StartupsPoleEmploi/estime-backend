package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.AllocationSolidariteSpecifique;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;

@Component
public class RessourcesFinancieresUtile {

    @Autowired
    DateUtile dateUtile;

    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;

    @Autowired
    private AllocationSolidariteSpecifique allocationSolidariteSpecifique;

    public float calculerMontantRessourcesFinancieresMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
        BigDecimal montantTotal = BigDecimal.ZERO;
        LocalDate moisAvantSimulation = dateUtile.enleverMoisALocalDate(dateUtile.getDateJour(), 1);
        if(beneficiaireAidesSocialesUtile.isBeneficiaireASS(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(allocationSolidariteSpecifique.calculerMontant(demandeurEmploi, moisAvantSimulation)));
        }
        if(beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetAAH()));
        }
        if(beneficiaireAidesSocialesUtile.isBeneficiairePensionInvalidite(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAllocationsCPAM().getPensionInvalidite()));
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAllocationsCPAM().getAllocationSupplementaireInvalidite()));
        }
        if(beneficiaireAidesSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA()));
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
        return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
    }


    public boolean hasAllocationsPoleEmploi(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi() != null;
    }

    public boolean hasAllocationAdultesHandicapes(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetAAH() != null
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetAAH() > 0;
    }

    public boolean hasAllocationLogement(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres()!= null && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF() != null && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsLogementMensuellesNetFoyer() != null 
                && (
                        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsLogementMensuellesNetFoyer().getMoisNMoins1() > 0 
                        || demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsLogementMensuellesNetFoyer().getMoisNMoins2() > 0 
                        || demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsLogementMensuellesNetFoyer().getMoisNMoins3() > 0 
                        );
    }

    public boolean hasAllocationFamiliale(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsFamilialesMensuellesNetFoyer() > 0;
    }

    public boolean hasPensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getPensionsAlimentairesFoyer() > 0;
    }

    public boolean hasPrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getPrestationAccueilJeuneEnfant() >= 0;
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
    
    public boolean hasBeneficesTravailleurIndependant(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null 
                && ressourcesFinancieres.getBeneficesTravailleurIndependantDernierExercice() != null
                && ressourcesFinancieres.getBeneficesTravailleurIndependantDernierExercice() > 0;
    }

    public boolean hasAllocationSolidariteSpecifique(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres != null
                && ressourcesFinancieres.getAllocationsPoleEmploi() != null
                && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationJournaliereNet() != null
                && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationJournaliereNet() > 0;
    }

    public boolean hasPensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAllocationsCPAM() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCPAM().getPensionInvalidite() != null
                && demandeurEmploi.getRessourcesFinancieres().getAllocationsCPAM().getPensionInvalidite() > 0;
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

    public boolean hasAllocationsCAF(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF() != null;
    }

    public boolean hasAllocationsRSA(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsCAF(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA() != null && demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA() != 0;
        }
        return false;
    }


    public float getAllocationsRSANet(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsRSA(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA();
        }
        return 0;
    }

    public int getNombreMoisTravaillesDerniersMois(DemandeurEmploi demandeurEmploi) {
        if(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois() != null) {
            return demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois();
        }
        return 0;
    }

    public boolean hasSalairesAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation() != null;
    }
    
    public boolean hasSalaire(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres.getSalaire() != null && ressourcesFinancieres.getSalaire().getMontantNet() > 0;
    }

    public Optional<Salaire> getSalaireAvantSimulation(DemandeurEmploi demandeurEmploi, int nMoisAvant) {
        if(hasSalairesAvantPeriodeSimulation(demandeurEmploi)) {
            switch (nMoisAvant) {
                case 1:
                    return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisMoins1MoisDemandeSimulation());
                case 2:                
                    return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisMoins2MoisDemandeSimulation());            
                default:
                    return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisDemandeSimulation());
            }
        }
        return Optional.empty();
    }

    public boolean hasTravailleAuCoursDerniersMois(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois().booleanValue();
    }
}

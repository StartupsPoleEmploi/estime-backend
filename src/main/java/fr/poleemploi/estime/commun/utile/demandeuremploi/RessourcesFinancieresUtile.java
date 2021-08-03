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
import fr.poleemploi.estime.services.ressources.SalaireAvantPeriodeSimulation;

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
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH()));
        }
        if(beneficiaireAidesSocialesUtile.isBeneficiairePensionInvalidite(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getPrestationsCPAM().getPensionInvalidite()));
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getPrestationsCPAM().getAllocationSupplementaireInvalidite()));
        }
        if(beneficiaireAidesSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            montantTotal = montantTotal.add(BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA()));
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
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi() != null;
    }

    public boolean hasAllocationAdultesHandicapes(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH() != null
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH() > 0;
    }

    public boolean hasAllocationLogement(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres()!= null && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF() != null && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationsLogementMensuellesNetFoyer() != null 
                && (
                        demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationsLogementMensuellesNetFoyer().getMoisNMoins1() > 0 
                        || demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationsLogementMensuellesNetFoyer().getMoisNMoins2() > 0 
                        || demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationsLogementMensuellesNetFoyer().getMoisNMoins3() > 0 
                        );
    }

    public boolean hasPrestationsFamiliales(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getPrestationsFamiliales() != null;
    }

    public boolean hasPensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getPrestationsFamiliales() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getPrestationsFamiliales().getPensionsAlimentairesFoyer() > 0;
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
                && ressourcesFinancieres.getPrestationsPoleEmploi() != null
                && ressourcesFinancieres.getPrestationsPoleEmploi().getAllocationASS() != null
                && ressourcesFinancieres.getPrestationsPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null
                && ressourcesFinancieres.getPrestationsPoleEmploi().getAllocationASS().getAllocationJournaliereNet() > 0;
    }

    public boolean hasPensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCPAM() != null 
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCPAM().getPensionInvalidite() != null
                && demandeurEmploi.getRessourcesFinancieres().getPrestationsCPAM().getPensionInvalidite() > 0;
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
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF() != null;
    }

    public boolean hasAllocationsRSA(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsCAF(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA() != null && demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA() != 0;
        }
        return false;
    }


    public float getAllocationsRSANet(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsRSA(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA();
        }
        return 0;
    }

    /**
     * Fonction qui permet de récupérer le nombre de mois travaillés en cumul salaire afin d'établir la temporalité
     * @param demandeurEmploi
     * @param contexteAAH si vrai : se base sur le champ select (1 à 6 mois) sinon se base sur le nombre de mois travaillés dont le montant du salaire est supérieur à 0
     * @return
     */
    public int getNombreMoisTravaillesDerniersMois(DemandeurEmploi demandeurEmploi, boolean contexteAAH) {
        int nombreMoisTravaillesDerniersMois = 0;
        if(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois()) {
            if(contexteAAH) nombreMoisTravaillesDerniersMois = demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois();
            else {
                nombreMoisTravaillesDerniersMois += this.isMoisTravaille(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisDemandeSimulation())?1:0;
                nombreMoisTravaillesDerniersMois += this.isMoisTravaille(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisMoins1MoisDemandeSimulation())?1:0;
                nombreMoisTravaillesDerniersMois += this.isMoisTravaille(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisMoins2MoisDemandeSimulation())?1:0;
            }
        }
        return nombreMoisTravaillesDerniersMois;
    }
    
    private boolean isMoisTravaille(SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulation) {
        if(salaireAvantPeriodeSimulation != null && salaireAvantPeriodeSimulation.getSalaire() != null) {
            return !salaireAvantPeriodeSimulation.isSansSalaire() && salaireAvantPeriodeSimulation.getSalaire().getMontantNet() > 0;            
        }
        return false;
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
                    return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisMoins1MoisDemandeSimulation().getSalaire());
                case 2:                
                    return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisMoins2MoisDemandeSimulation().getSalaire());            
                default:
                    return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getSalaireMoisDemandeSimulation().getSalaire());
            }
        }
        return Optional.empty();
    }

    public boolean hasTravailleAuCoursDerniersMois(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois().booleanValue();
    }
}

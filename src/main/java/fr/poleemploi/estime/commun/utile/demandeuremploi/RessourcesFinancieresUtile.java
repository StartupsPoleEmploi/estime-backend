package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantPeriodeSimulation;

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
        return montantTotal.setScale(0, RoundingMode.DOWN).floatValue();
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

    public boolean hasAidesFamiliales(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales() != null;
    }

    public boolean hasPensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer() > 0;
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
                && ressourcesFinancieres.getAidesPoleEmploi() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null
                && ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() > 0;
    }

    public boolean hasPensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM() != null 
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite() != null
                && demandeurEmploi.getRessourcesFinancieres().getAidesCPAM().getPensionInvalidite() > 0;
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
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null;
    }

    public boolean hasAllocationsRSA(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsCAF(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != null && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != 0;
        }
        return false;
    }


    public float getAllocationsRSANet(DemandeurEmploi demandeurEmploi) {
        if(hasAllocationsRSA(demandeurEmploi)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA();
        }
        return 0;
    }

    /**
     * Fonction qui permet de récupérer le nombre de mois travaillés en cumul salaire afin d'établir la temporalité
     * @param demandeurEmploi
     * @param contexteAAH si vrai : se base sur le champ select (1 à 6 mois) sinon se base sur le nombre de mois travaillés dont le montant du salaire est supérieur à 0
     * @return nombre mois travailles derniers mois
     */
    public int getNombreMoisTravaillesDerniersMois(DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = 0;
        if(beneficiaireAidesUtile.isUniquementBeneficiaireAAH(demandeurEmploi)) {
            nombreMoisTravaillesDerniersMois = demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois();
        } else {            
            for (int moisAvantSimulation = 0; moisAvantSimulation <= 2 ; moisAvantSimulation++) {
                Optional<MoisTravailleAvantPeriodeSimulation> salaireAvantSimulation = getMoisTravailleAvantPeriodeSimulation(demandeurEmploi, moisAvantSimulation);
                if(salaireAvantSimulation.isPresent() && isMoisTravaille(salaireAvantSimulation.get())) {
                    nombreMoisTravaillesDerniersMois++;                    
                }                
            }
        }
        return nombreMoisTravaillesDerniersMois;
    }

    private boolean isMoisTravaille(MoisTravailleAvantPeriodeSimulation salaireAvantSimulation) {
        return !salaireAvantSimulation.isSansSalaire() && salaireAvantSimulation.getSalaire().getMontantNet() > 0;
    }

    public boolean hasSalairesAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation() != null;
    }

    public boolean hasSalaire(RessourcesFinancieres ressourcesFinancieres) {
        return ressourcesFinancieres.getSalaire() != null && ressourcesFinancieres.getSalaire().getMontantNet() > 0;
    }

    public Optional<MoisTravailleAvantPeriodeSimulation> getMoisTravailleAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int nMoisAvant) {
        if(hasSalairesAvantPeriodeSimulation(demandeurEmploi)) {
            switch (nMoisAvant) {
            case 1:
                return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisMoins1MoisDemandeSimulation());
            case 2:                
                return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisMoins2MoisDemandeSimulation());            
            default:
                return Optional.of(demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisDemandeSimulation());
            }
        }
        return Optional.empty();
    }

    public Salaire getSalaireAvantSimulation(DemandeurEmploi demandeurEmploi, int nMoisAvant) {
        switch (nMoisAvant) {
        case 1:
            return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisMoins1MoisDemandeSimulation().getSalaire();
        case 2:                
            return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisMoins2MoisDemandeSimulation().getSalaire();            
        default:
            return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisDemandeSimulation().getSalaire();
        }
    }

    public boolean hasTravailleAuCoursDerniersMois(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois() != null && demandeurEmploi.getRessourcesFinancieres().getHasTravailleAuCoursDerniersMois().booleanValue();
    }
}

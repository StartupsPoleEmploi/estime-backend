package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.PrimeActivite;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class SituationFamilialeUtile {
    
    @Autowired
    private PersonneUtile personneUtile;

    public boolean isEnCouple(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getSituationFamiliale().getIsEnCouple().booleanValue();
    }
    
    public boolean hasPersonnesACharge(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getSituationFamiliale() != null 
                && demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null 
                && !demandeurEmploi.getSituationFamiliale().getPersonnesACharge().isEmpty();
    }
    
    public boolean hasPersonnesAChargeEligiblesPourPriseEnComptePrimeActivite(DemandeurEmploi demandeurEmploi) {
        return getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, PrimeActivite.AGE_MAX_PERSONNE_A_CHARGE) > 0;
    }
    
    public int getNombrePersonnesAChargeAgeInferieureAgeLimite(DemandeurEmploi demandeurEmploi, int ageLimite) {
        if(demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null) { 
            return (int) demandeurEmploi.getSituationFamiliale().getPersonnesACharge().stream().filter(
                            personneACharge -> personneUtile.calculerAge(personneACharge) < ageLimite).count();
        }
        return 0;
    }
    
    public Optional<List<Personne>> getPersonnesAChargeAgeInferieurAgeLimite(DemandeurEmploi demandeurEmploi, int ageLimite) {
        if(demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null) {
            return Optional.of(demandeurEmploi.getSituationFamiliale().getPersonnesACharge().stream().filter(
                    personneACharge -> personneUtile.calculerAge(personneACharge) < ageLimite).collect(Collectors.toList()));
        }
        return Optional.empty();
    }
    
    public float calculerMontantRsaFamille(DemandeurEmploi demandeurEmploi) {
        BigDecimal montantRSA = BigDecimal.ZERO;
        if(isEnCouple(demandeurEmploi)) {
            montantRSA = montantRSA.add(BigDecimal.valueOf(getMontantRsaConjoint(demandeurEmploi)));
        }
        if(hasPersonnesACharge(demandeurEmploi)) {
            List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
            montantRSA = montantRSA.add(getMontantTotalRSADesPersonnesCharge(personnesACharge));
        }
        return montantRSA.setScale(0, RoundingMode.DOWN).floatValue();
    }
    
    public boolean isSeulPlusDe18Mois(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getSituationFamiliale().getIsSeulPlusDe18Mois() != null 
                &&  demandeurEmploi.getSituationFamiliale().getIsSeulPlusDe18Mois().booleanValue();
    }
    
    private float getMontantRsaConjoint(DemandeurEmploi demandeurEmploi) {
        Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
        if(personneUtile.hasAllocationRSA(conjoint)) {
            return conjoint.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA();
        }
        return 0;
    }
    
    private BigDecimal getMontantTotalRSADesPersonnesCharge(List<Personne> personnesACharge) {
        BigDecimal montantRSA = BigDecimal.ZERO;
        for (Personne personneACharge : personnesACharge) {
            if(personneUtile.hasAllocationRSA(personneACharge)) {
                float allocationMensuelleNetRSA = personneACharge.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA();
                montantRSA = montantRSA.add(BigDecimal.valueOf(allocationMensuelleNetRSA));
            }
        }
        return montantRSA;
    }    
}

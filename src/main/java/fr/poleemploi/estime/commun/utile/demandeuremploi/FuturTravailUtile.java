package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.TypesContratTravailEnum;
import fr.poleemploi.estime.services.ressources.FuturTravail;

@Component
public class FuturTravailUtile {
    
    public static final int DUREE_MINIMUM_CONTRAT_HORS_CDI_ELIGIBILITE_AIDE = 3;
    
    public boolean hasContratCDD(FuturTravail futurTravail) {
        return TypesContratTravailEnum.CDD.name().equals(futurTravail.getTypeContrat());
    }
    
    public boolean hasContratCDI(FuturTravail futurTravail) {
        return TypesContratTravailEnum.CDI.name().equals(futurTravail.getTypeContrat());
    }
    
    public boolean isFuturContratTravailEligible(FuturTravail futurTravail) {
        return hasContratCDI(futurTravail) ||
                hasContratNonCDISuperieurOuEgal3Mois(futurTravail); 
    }
    
    private boolean hasContratNonCDISuperieurOuEgal3Mois(FuturTravail futurTravail) {
        return hasContratCDD(futurTravail) && futurTravail.getNombreMoisContratCDD() >= 3;
    }
}

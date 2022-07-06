package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.services.ressources.FuturTravail;

@Component
public class FuturTravailUtile {

    public static final int DUREE_MINIMUM_CONTRAT_HORS_CDI_ELIGIBILITE_AIDE = 3;

    public boolean hasContratCDD(FuturTravail futurTravail) {
	return TypeContratTravailEnum.CDD.name().equals(futurTravail.getTypeContrat());
    }

    public boolean hasContratCDI(FuturTravail futurTravail) {
	return TypeContratTravailEnum.CDI.name().equals(futurTravail.getTypeContrat());
    }

    public boolean hasContratInterim(FuturTravail futurTravail) {
	return TypeContratTravailEnum.INTERIM.name().equals(futurTravail.getTypeContrat());
    }

    public boolean hasContratIAE(FuturTravail futurTravail) {
	return TypeContratTravailEnum.IAE.name().equals(futurTravail.getTypeContrat());
    }

    public boolean hasContratDureeDeterminee(FuturTravail futurTravail) {
	return TypeContratTravailEnum.CDD.name().equals(futurTravail.getTypeContrat()) || TypeContratTravailEnum.INTERIM.name().equals(futurTravail.getTypeContrat())
		|| TypeContratTravailEnum.IAE.name().equals(futurTravail.getTypeContrat());
    }

    public boolean isFuturContratTravailEligible(FuturTravail futurTravail) {
	return hasContratCDI(futurTravail) || hasContratNonCDISuperieurOuEgal3Mois(futurTravail);
    }

    private boolean hasContratNonCDISuperieurOuEgal3Mois(FuturTravail futurTravail) {
	return hasContratCDD(futurTravail) && futurTravail.getNombreMoisContratCDD() >= 3;
    }

    public String getTypeContratOpenFisca(FuturTravail futurTravail) {
	String typeContratOpenFisca = "";
	if (hasContratCDD(futurTravail))
	    typeContratOpenFisca = TypeContratTravailEnum.CDD.getValeurOpenFisca();
	else if (hasContratCDI(futurTravail))
	    typeContratOpenFisca = TypeContratTravailEnum.CDI.getValeurOpenFisca();
	else if (hasContratInterim(futurTravail))
	    typeContratOpenFisca = TypeContratTravailEnum.INTERIM.getValeurOpenFisca();
	else
	    typeContratOpenFisca = TypeContratTravailEnum.IAE.getValeurOpenFisca();
	return typeContratOpenFisca;
    }
}

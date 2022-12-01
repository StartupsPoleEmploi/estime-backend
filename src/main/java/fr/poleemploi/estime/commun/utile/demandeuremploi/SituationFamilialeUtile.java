package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.AgeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class SituationFamilialeUtile {

    @Autowired
    private AgeUtile ageUtile;

    private static final int MOIS_LIMITE_3_ANS = 37;

    public boolean isEnCouple(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getSituationFamiliale().getIsEnCouple() != null && demandeurEmploi.getSituationFamiliale().getIsEnCouple().booleanValue();
    }

    public boolean hasPersonnesACharge(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getSituationFamiliale() != null && demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null
		&& !demandeurEmploi.getSituationFamiliale().getPersonnesACharge().isEmpty();
    }

    public boolean hasPersonnesAChargeEligiblesPourPriseEnComptePrimeActivite(DemandeurEmploi demandeurEmploi) {
	return getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, SimulateurAidesCAF.AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA) > 0;
    }

    public int getNombrePersonnesAChargeAgeInferieureAgeLimite(DemandeurEmploi demandeurEmploi, int ageLimite) {
	if (demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null) {
	    return (int) demandeurEmploi.getSituationFamiliale().getPersonnesACharge().stream().filter(personneACharge -> ageUtile.calculerAge(personneACharge) < ageLimite)
		    .count();
	}
	return 0;
    }

    public Optional<List<Personne>> getPersonnesAChargeAgeInferieurAgeLimite(DemandeurEmploi demandeurEmploi, int ageLimite) {
	if (demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null) {
	    return Optional.of(demandeurEmploi.getSituationFamiliale().getPersonnesACharge().stream().filter(personneACharge -> ageUtile.calculerAge(personneACharge) < ageLimite)
		    .collect(Collectors.toList()));
	}
	return Optional.empty();
    }

    public Optional<List<Personne>> getPersonnesAChargeAgeInferieur37MoisLimitePourMoisSimule(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	if (demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null) {
	    return Optional.of(demandeurEmploi.getSituationFamiliale().getPersonnesACharge().stream()
		    .filter(personneACharge -> ageUtile.calculerAgeMoisMoisSimule(personneACharge, numeroMoisSimule) < MOIS_LIMITE_3_ANS).collect(Collectors.toList()));
	}
	return Optional.empty();
    }

    public boolean isSeulPlusDe18Mois(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getSituationFamiliale().getIsSeulPlusDe18Mois() != null && demandeurEmploi.getSituationFamiliale().getIsSeulPlusDe18Mois().booleanValue();
    }

    public boolean hasEnfantMoinsDe3AnsPourMoisSimule(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	if (demandeurEmploi.getSituationFamiliale().getPersonnesACharge() != null) {
	    Optional<List<Personne>> personnesACharge = getPersonnesAChargeAgeInferieur37MoisLimitePourMoisSimule(demandeurEmploi, numeroMoisSimule);
	    if (personnesACharge.isPresent()) {
		return !personnesACharge.get().isEmpty();
	    }
	}
	return false;
    }
}

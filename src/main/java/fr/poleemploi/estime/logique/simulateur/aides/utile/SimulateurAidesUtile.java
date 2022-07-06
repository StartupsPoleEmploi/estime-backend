package fr.poleemploi.estime.logique.simulateur.aides.utile;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesUtile {

    public static final int NOMBRE_MOIS_MAX_A_SIMULER = 6;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private FuturTravailUtile futurTravailUtile;

    public LocalDate getDateDebutSimulation(LocalDate dateDemandeSimulation) {
	return dateUtile.getDatePremierJourDuMois(dateDemandeSimulation.plusMonths(1));
    }

    public int getNombreMoisASimuler(DemandeurEmploi demandeurEmploi) {
	if (futurTravailUtile.hasContratDureeDeterminee(demandeurEmploi.getFuturTravail())) {
	    int dureeContratCDDEnMois = demandeurEmploi.getFuturTravail().getNombreMoisContratCDD();
	    if (dureeContratCDDEnMois < NOMBRE_MOIS_MAX_A_SIMULER) {
		return dureeContratCDDEnMois;
	    }
	}
	return NOMBRE_MOIS_MAX_A_SIMULER;
    }

    public boolean isPremierMois(int numeroMoisSimule) {
	return numeroMoisSimule == 1;
    }

    public boolean isDeuxDerniersMois(int numeroMoisSimule) {
	return numeroMoisSimule >= 5;
    }

    public boolean isTroisDerniersMois(int numeroMoisSimule) {
	return numeroMoisSimule >= 4;
    }

    public boolean isQuatreDerniersMois(int numeroMoisSimule) {
	return numeroMoisSimule >= 3;
    }

    public boolean isCinqDerniersMois(int numeroMoisSimule) {
	return numeroMoisSimule >= 2;
    }
}

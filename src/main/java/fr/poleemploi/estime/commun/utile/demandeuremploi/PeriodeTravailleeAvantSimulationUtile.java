package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;

@Component
public class PeriodeTravailleeAvantSimulationUtile {

    @Autowired
    private DateUtile dateUtile;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES = 12;

    /**
     * Fonction qui permet de récupérer le nombre de mois travaillés dans la péridoe de 3 mois avant la simulation
     * @param demandeurEmploi
     * @return nombre mois travailles au cours des 3 derniers mois
     */
    public int getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
	int periodeMoisAvantSimulation = 3;
	int nombreMoisTravaillesDerniersMois = 0;
	for (int moisAvantSimulation = 1; moisAvantSimulation <= periodeMoisAvantSimulation; moisAvantSimulation++) {
	    if (demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation() != null
		    && demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMois() != null) {
		MoisTravailleAvantSimulation moisTravailleAvantSimulation = demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation()
			.getMois()[moisAvantSimulation];
		if (moisTravailleAvantSimulation != null && isMoisTravaille(moisTravailleAvantSimulation)) {
		    nombreMoisTravaillesDerniersMois++;
		}
	    }
	}
	return nombreMoisTravaillesDerniersMois;
    }

    public Salaire getSalaireAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisSimule, int numeroMoisPeriodeOpenfisca) {
	int indexMoisAvantPeriode = NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES - (numeroMoisPeriodeOpenfisca + numeroMoisSimule + 1);
	if (indexMoisAvantPeriode <= 0) {
	    return demandeurEmploi.getFuturTravail().getSalaire();
	}
	return getMoisTravaillesAvantSimulation(demandeurEmploi, numeroMoisSimule)[indexMoisAvantPeriode].getSalaire();
    }

    public MoisTravailleAvantSimulation[] getMoisTravaillesAvantSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	MoisTravailleAvantSimulation[] moisTravaillesAvantSimulation = {};
	if (hasSalairesAvantPeriodeSimulation(demandeurEmploi)) {
	    moisTravaillesAvantSimulation = demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMois();
	}
	return moisTravaillesAvantSimulation;
    }

    public boolean hasSalairesAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMois() != null;
    }

    private boolean isMoisTravaille(MoisTravailleAvantSimulation moisTravailleAvantSimulation) {
	return !moisTravailleAvantSimulation.isSansSalaire() && moisTravailleAvantSimulation.getSalaire().getMontantNet() > 0;
    }

    private String constructKeyMoisTravailleAvantSimulation(int moisMoinsN) {
	LocalDate dateMoinsNMois = dateUtile.enleverMoisALocalDate(dateUtile.getDateJour(), moisMoinsN);
	return dateUtile.convertDateToString(dateMoinsNMois, "MM-yyyy");
    }
}

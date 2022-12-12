package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Salaire;

@Component
public class PeriodeTravailleeAvantSimulationUtile {

    public static final int INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION = 13;

    public int getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
	return getNombreMoisTravaillesAuCoursDesXDerniersMoisAvantSimulation(demandeurEmploi, 3);
    }

    public int getNombreMoisTravaillesAuCoursDes6DerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
	return getNombreMoisTravaillesAuCoursDesXDerniersMoisAvantSimulation(demandeurEmploi, 6);
    }

    /**
     * Fonction qui permet de récupérer le nombre de mois travaillés dans la péridoe de X mois avant la simulation
     * @param demandeurEmploi
     * @return nombre mois travailles au cours des X derniers mois
     */
    public int getNombreMoisTravaillesAuCoursDesXDerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi, int nombreDeMoisAConsiderer) {
	int nombreMoisTravaillesDerniersMois = 0;
	for (int moisAvantSimulation = 0; moisAvantSimulation < nombreDeMoisAConsiderer; moisAvantSimulation++) {
	    if (demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		    && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation() != null
		    && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois() != null) {
		MoisTravailleAvantSimulation moisTravailleAvantSimulation = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation()
			.getMois()[moisAvantSimulation];
		if (moisTravailleAvantSimulation != null && isMoisTravaille(moisTravailleAvantSimulation)) {
		    nombreMoisTravaillesDerniersMois++;
		}
	    }
	}
	return nombreMoisTravaillesDerniersMois;
    }

    /**
     * Méthode permettant de récupérer le salaire du demandeur d'emploi à un mois donné avant ou après sa simulation selon le tableau suivant
     * 
     * 
     * Les colonnes représentent les mois simulés
     * Les lignes représentent les mois voulu dans les 12 derniers mois
     * 
     * 		0		1		2		3		4		5
     * 	0	M-12 [11]	M-11 [10]	M-10 [9]	M-9 [8]		M-8 [7]		M-7 [6]
     * 	1	M-11 [10]	M-10 [9]	M-9  [8]	M-8 [7]		M-7 [6]		M-6 [5]
     * 	2	M-10 [9]	M-9  [8]	M-8  [7]	M-7 [6]		M-6 [5]		M-5 [4]
     * 	3	M-9  [8]	M-8  [7]	M-7  [5]	M-6 [5]		M-5 [4]		M-4 [3]
     * 	4	M-8  [7]	M-7  [6]	M-6  [5]	M-5 [4]		M-4 [3]		M-3 [2]
     * 	5	M-7  [6]	M-6  [5]	M-5  [4]	M-4 [3]		M-3 [2]		M-2 [1]
     * 	6	M-6  [5]	M-5  [4]	M-4  [3]	M-3 [2]		M-2 [1]		M-1 [0]
     * 	7	M-5  [4]	M-4  [3]	M-3  [2]	M-2 [1]		M-1 [0]		M0   /
     * 	8	M-4  [3]	M-3  [2]	M-2  [1]	M-1 [0]		M0   /		M1 futur
     * 	9	M-3  [2]	M-2  [1]	M-1  [0]	M0   /		M1 futur	M2 futur
     * 	10	M-2  [1]	M-1  [0]	M0   /		M1 futur	M2 futur	M3 futur
     * 	11	M-1  [0]	M0   /		M1 futur	M2 futur	M3 futur	M4 futur
     * 
     * Les salaires des mois M0 sont par défault valorisés à 0 car on ne connait pas actuellement le montant du salaire du mois en cours
     * 
     * @param demandeurEmploi
     * @param numeroMoisSimule
     * @param numeroMoisPeriodeOpenfisca numéro du mois recherché parmis les 12 derniers salaires
     * @return
     */
    public Salaire getSalaireAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
	int indexMoisAvantPeriode = INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION + (numeroMoisPeriodeOpenfisca + 1);
	if (indexMoisAvantPeriode > INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION) {
	    return demandeurEmploi.getFuturTravail().getSalaire();
	}
	return getMoisTravaillesAvantSimulation(demandeurEmploi, Math.abs(numeroMoisPeriodeOpenfisca + 1)).getSalaire();
    }

    public Salaire getSalaireAvecCumulAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
	int indexMoisAvantPeriode = INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION + (numeroMoisPeriodeOpenfisca + 1);
	if (indexMoisAvantPeriode > INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION) {
	    return getSalaireCumul(demandeurEmploi);
	}
	return getMoisTravaillesAvantSimulation(demandeurEmploi, Math.abs(numeroMoisPeriodeOpenfisca + 1)).getSalaire();
    }

    public Salaire getSalaireCumul(DemandeurEmploi demandeurEmploi) {
	Salaire futurSalaire = demandeurEmploi.getFuturTravail().getSalaire();
	Salaire ancienSalaire = getMoisTravaillesAvantSimulation(demandeurEmploi, 0).getSalaire();
	float salaireNet = futurSalaire.getMontantMensuelNet() + ancienSalaire.getMontantMensuelNet();
	float salaireBrut = futurSalaire.getMontantMensuelBrut() + ancienSalaire.getMontantMensuelBrut();
	Salaire salaireCumule = new Salaire();
	salaireCumule.setMontantMensuelNet(salaireNet);
	salaireCumule.setMontantMensuelBrut(salaireBrut);

	return salaireCumule;
    }

    public Salaire getSalaireAvantPeriodeSimulationPersonne(Personne personne, int numeroMoisPeriodeOpenfisca) {
	int indexMoisAvantPeriode = INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION + (numeroMoisPeriodeOpenfisca + 1);
	if (indexMoisAvantPeriode > INDEX_MAX_MOIS_TRAVAILLES_AVANT_SIMULATION && personne.getRessourcesFinancieres().getSalaire() != null) {
	    return personne.getRessourcesFinancieres().getSalaire();
	} else {
	    return getMoisTravaillesAvantSimulationPersonne(personne, Math.abs(numeroMoisPeriodeOpenfisca + 1)).getSalaire();
	}
    }

    public MoisTravailleAvantSimulation getMoisTravaillesAvantSimulation(DemandeurEmploi demandeurEmploi, int index) {
	MoisTravailleAvantSimulation moisTravaillesAvantSimulation = getMoisTravailleVide();
	if (hasSalairesAvantPeriodeSimulation(demandeurEmploi, index)
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[index] != null) {
	    moisTravaillesAvantSimulation = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[index];

	}
	return moisTravaillesAvantSimulation;
    }

    public MoisTravailleAvantSimulation getMoisTravaillesAvantSimulationPersonne(Personne personne, int index) {
	MoisTravailleAvantSimulation moisTravaillesAvantSimulation = getMoisTravailleVide();
	if (hasSalairesAvantPeriodeSimulationPersonne(personne, index)) {
	    moisTravaillesAvantSimulation = personne.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMois()[index];
	}
	return moisTravaillesAvantSimulation;
    }

    private MoisTravailleAvantSimulation getMoisTravailleVide() {
	MoisTravailleAvantSimulation moisTravailleAvantSimulationVide = new MoisTravailleAvantSimulation();
	Salaire salaireVide = new Salaire();
	moisTravailleAvantSimulationVide.setSalaire(salaireVide);
	return moisTravailleAvantSimulationVide;
    }

    public boolean hasSalairesAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi) {
	if (demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois() != null) {
	    for (MoisTravailleAvantSimulation mois : demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()) {
		if (mois != null)
		    return true;
	    }
	}
	return false;
    }

    public boolean hasSalairesAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int index) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[index] != null;
    }

    public boolean hasSalairesAvantPeriodeSimulationPersonne(Personne personne, int index) {
	return personne.getRessourcesFinancieres() != null && personne.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation() != null
		&& personne.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMois() != null
		&& personne.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMois()[index] != null;
    }

    public boolean isMoisTravaille(MoisTravailleAvantSimulation moisTravailleAvantSimulation) {
	return moisTravailleAvantSimulation.getSalaire().getMontantMensuelNet() > 0;
    }
}

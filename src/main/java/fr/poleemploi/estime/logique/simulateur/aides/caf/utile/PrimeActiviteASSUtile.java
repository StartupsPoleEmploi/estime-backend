package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteASSUtile {

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    public void simulerAide(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    primeActiviteUtile.reporterPrimeActiviteMoisPrecedent(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule,
		    demandeurEmploi, primeActiviteMoisPrecedent);
	} else if (isPrimeActiviteACalculer(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi)) {
	    primeActiviteUtile.calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	}
    }

    /**
     * Fonction permettant de déterminer si on a calculé le(s) montant(s) de la prime d'activité et/ou du RSA le mois précédent et s'il(s) doi(ven)t être versé(s) ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     *       _________________________________________________________________________________________________________
     *      |                          |          |          |          |          |          |           |          |
     *      | Mois cumul salaire / ass |    M0    |    M1    |    M2    |    M3    |    M4    |     M5    |    M6    |
     *      |                          |          |          |          |          |          |           |          |
     *      |  -        0              |          |          |          |          |   (C1)   |     V1    |    R1    |          
     *      |  -        1              |          |          |          |   (C1)   |    V1    |     R1    | (C2)/R1  |               
     *      |  -        2              |          |          |   (C1)   |    V1    |    R1    |  (C2)/R1  |    V2    |          
     *      |  -        3              |          |   (C1)   |    V1    |    R1    | (C2)/R1  |     V2    |    R2    |
     *      |__________________________|__________|__________|__________|__________|__________|___________|__________|
     */
    private boolean isPrimeActiviteACalculer(Optional<Aide> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (primeActiviteMoisPrecedent.isEmpty()) {
	    // Si le demandeur est bénéficiare de l'ASS et de l'AAH, c'est la temporatlité de l'ASS qui est appliquée
	    if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)
		    || (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi))) {
		return isPrimeActiviteACalculerDemandeurASS(demandeurEmploi, numeroMoisSimule);
	    } else if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
		return isPrimeActiviteACalculerDemandeurAAH(numeroMoisSimule);
	    }
	}
	return false;
    }

    /**
     * Fonction permettant de déterminer si on a calculé le montant de la prime d'activité le mois précédent et s'il doit être versé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     *       _________________________________________________________________________________________________________
     *      |                          |          |          |          |          |          |           |          |
     *      | Mois cumul salaire / ass |    M0    |    M1    |    M2    |    M3    |    M4    |     M5    |    M6    |
     *      |                          |          |          |          |          |          |           |          |
     *      |  -        0              |          |          |          |          |    C1    |    (V1)   |    R1    |          
     *      |  -        1              |          |          |          |    C1    |   (V1)   |     R1    |  C2/R1   |               
     *      |  -        2              |          |          |    C1    |   (V1)   |    R1    |   C2/R1   |   (V2)   |          
     *      |  -        3              |          |    C1    |   (V1)   |    R1    |  C2/R1   |    (V2)   |    R2    |
     *      |__________________________|__________|__________|__________|__________|__________|___________|__________|
     */
    private boolean isPrimeActiviteACalculerDemandeurASS(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return !ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi) && numeroMoisSimule == 5
		|| (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi)
			&& isMoisPourCalculPrimeActiviteASS(numeroMoisSimule, demandeurEmploi));
    }

    private boolean isPrimeActiviteACalculerDemandeurAAH(int numeroMoisSimule) {
	return numeroMoisSimule == 2 || numeroMoisSimule == 5;
    }

    private boolean isMoisPourCalculPrimeActiviteASS(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int nombreMoisTravaillesDerniersMois = periodeTravailleeAvantSimulationUtile
		.getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
	return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4
		|| (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		|| (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
    }

}

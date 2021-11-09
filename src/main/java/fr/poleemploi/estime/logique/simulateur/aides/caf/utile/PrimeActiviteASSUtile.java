package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class PrimeActiviteASSUtile {

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

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
    protected boolean isPrimeActiviteACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi)) {
	    int nombreMoisTravaillesDerniersMois = periodeTravailleeAvantSimulationUtile
		    .getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
	    return nombreMoisTravaillesDerniersMois == 1 && (numeroMoisSimule == 3 || numeroMoisSimule == 6)
		    || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
		    || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 1 || numeroMoisSimule == 4));
	} else
	    return numeroMoisSimule == 4;
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
    protected boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi)) {
	    int nombreMoisTravaillesDerniersMois = periodeTravailleeAvantSimulationUtile
		    .getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
	    return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4
		    || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		    || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
	}
	return numeroMoisSimule == 5;
    }
}

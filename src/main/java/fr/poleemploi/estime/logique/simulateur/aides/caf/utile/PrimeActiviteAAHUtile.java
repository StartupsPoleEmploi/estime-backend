package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteAAHUtile {

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    public void simulerAide(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {

	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();

	if (isPrimeActiviteACalculer(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
	    primeActiviteUtile.reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule);
	} else if (isPrimeActiviteAVerser(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
	    primeActiviteUtile.calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	} else {
	    primeActiviteUtile.reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule);
	}
    }

    /**
     * Fonction permettant de déterminer si le montant de la prime d'activité doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |     M4   |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     |          |    C1    |    V1    | (C2)/R1  |     V2   |    R2    | (C3)/R2  |              
     *      |  -  M1     |          |    C1    |    V1    |    R1    |  (C2)/R1 |    V2    |    R2    |          
     *      |  -  M2     |          |    C1    | (C2)/V1  |    V2    |     R2   | (C3)/R2  |    V3    |          
     *      |  -  M3     |          |    C1    |    V1    | (C2)/R1  |     V2   |    R2    | (C3)/R2  |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     */
    private boolean isPrimeActiviteACalculer(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
	return numeroMoisSimule == 1 || ((prochaineDeclarationTrimestrielle == numeroMoisSimule)
		|| (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));

    }
    

    /**
     * Fonction permettant de déterminer si le montant de la prime d'activité doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     |          |    C1    |   (V1)   |  C2/R1   |   (V2)   |    R2    |  C3/R2   |              
     *      |  -  M1     |          |    C1    |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |          
     *      |  -  M2     |          |    C1    |  C2/(V1) |   (V2)   |    R2    |  C3/R2   |   (V3)   |          
     *      |  -  M3     |          |    C1    |   (V1)   |  C2/R1   |   (V2)   |    R2    |  C3/R2   |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     */
    private boolean isPrimeActiviteAVerser(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
	return numeroMoisSimule == 2
		|| ((prochaineDeclarationTrimestrielle == numeroMoisSimule - 1) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 4));
    }
}

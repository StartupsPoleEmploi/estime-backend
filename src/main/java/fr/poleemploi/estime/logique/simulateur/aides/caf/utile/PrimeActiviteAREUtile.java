package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import org.springframework.stereotype.Component;

@Component
public class PrimeActiviteAREUtile {

    /**
     * Fonction permettant de déterminer si on doit calculer le montant de la prime d'activité ce mois-ci
     * 
     * @param numeroMoisSimule
     * @return
     *       ____________________________________________________________________________
     *      |          |          |          |          |          |          |          |
     *      |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |          |          |          |          |          |          |          |  
     *      |          |          |          |          |   (C1)   |    V1    |    R1    |  
     *      |__________|__________|__________|__________|__________|__________|__________|
     */
    public boolean isPrimeActiviteACalculer(int numeroMoisSimule) {
	return numeroMoisSimule == 4;
    }

    /**
     * Fonction permettant de déterminer si on doit verser le montant de la prime d'activité calculé au mois précédent ce mois-ci
     * 
     * @param numeroMoisSimule
     * @return
     *       ____________________________________________________________________________
     *      |          |          |          |          |          |          |          |
     *      |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |          |          |          |          |          |          |          |  
     *      |          |          |          |          |    C1    |   (V1)   |    R1    |  
     *      |__________|__________|__________|__________|__________|__________|__________|      
     */
    public boolean isPrimeActiviteAVerser(int numeroMoisSimule) {
	return numeroMoisSimule == 5;
    }
}

package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class PrimeActiviteAAHUtile {

    /**
     * Fonction permettant de déterminer si le montant de la prime d'activité doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param demandeurEmploi
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
    protected boolean isPrimeActiviteACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
	return numeroMoisSimule == 1 || ((prochaineDeclarationTrimestrielle == 1 && (numeroMoisSimule == 4))
		|| (prochaineDeclarationTrimestrielle == 2 && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 0 || prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 3 || numeroMoisSimule == 6)));

    }

    /**
     * Fonction permettant de déterminer si le montant de la prime d'activité doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param demandeurEmploi
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
    protected boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
	return numeroMoisSimule == 2 || ((prochaineDeclarationTrimestrielle == 1 && (numeroMoisSimule == 5))
		|| (prochaineDeclarationTrimestrielle == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 0 || prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }
}

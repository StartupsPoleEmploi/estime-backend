package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class PrimeActiviteAREUtile {

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    /**
     * Fonction permettant de déterminer si on doit calculer le montant de la prime d'activité ce mois-ci
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
    public boolean isPrimeActiviteACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return numeroMoisSimule == 1 || ((prochaineDeclarationTrimestrielle == 1 && (numeroMoisSimule == 4))
		|| (prochaineDeclarationTrimestrielle == 2 && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 0 || prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 3 || numeroMoisSimule == 6)));
    }

    /**
     * Fonction permettant de déterminer si on doit verser le montant de la prime d'activité calculé au mois précédent ce mois-ci
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
    public boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return numeroMoisSimule == 2 || ((prochaineDeclarationTrimestrielle == 1 && (numeroMoisSimule == 5))
		|| (prochaineDeclarationTrimestrielle == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 0 || prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }
}

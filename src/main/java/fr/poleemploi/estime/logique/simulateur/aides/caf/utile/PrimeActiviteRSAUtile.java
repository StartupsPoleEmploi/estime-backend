package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class PrimeActiviteRSAUtile {

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

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
     *      |  -  M0     | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    | (C3)/R2  |              
     *      |  -  M1     |    R0    | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    |          
     *      |  -  M2     |    R0    |    R0    |  (C1)/R0 |    V1    |    R1    | (C2)/R1  |    V2    |          
     *      |  -  M3     |    C0    |    R0    |    R0    | (C1)/R0  |    V1    |    R1    | (C2)/R1  |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     *  
     */
    boolean isPrimeActiviteACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return ((prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3)
		|| (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));
    }

    /**
     * Fonction permettant de déterminer si on a calculé le(s) montant(s) de la prime d'activité et/ou du RSA le mois précédent et s'il(s) doi(ven)t être versé(s) ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     |  C1/R0   |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |  C3/R2   |              
     *      |  -  M1     |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |          
     *      |  -  M2     |    R0    |    R0    |   C1/R0  |   (V1)   |    R1    |  C2/R1   |   (V2)   |          
     *      |  -  M3     |    C0    |   (R0)   |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|   
     *       
     */
    boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }

    /**
     * Fonction permettant de déterminer si on a calculé le(s) montant(s) de la prime d'activité et/ou du RSA le mois précédent et s'il(s) doi(ven)t être versé(s) ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     | C1/(R0)  |    V1    |   (R1)   |  C2/(R1) |   (V2)   |   (R2)   |  C3/(R2) |              
     *      |  -  M1     |  (R0)    |  C1/(R0) |   (V1)   |    (R1)  |  C2/(R1) |   (V2)   |   (R2)   |          
     *      |  -  M2     |  (R0)    |   (R0)   |  C1/(R0) |   (V1)   |   (R1)   |  C2/(R1) |   (V2)   |          
     *      |  -  M3     |   C0     |   (R0)   |   (R0)   |  C1/(R0) |   (V1)   |   (R1)   |  C2/(R1) |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|   
     *       
     */
    boolean isPrimeActiviteAReporter(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 2 || numeroMoisSimule == 3 || numeroMoisSimule == 5 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 1 || numeroMoisSimule == 3 || numeroMoisSimule == 4 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 1 || numeroMoisSimule == 2 || numeroMoisSimule == 4 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 3)
			&& (numeroMoisSimule == 1 || numeroMoisSimule == 2 || numeroMoisSimule == 3 || numeroMoisSimule == 5 || numeroMoisSimule == 6)));
    }
}

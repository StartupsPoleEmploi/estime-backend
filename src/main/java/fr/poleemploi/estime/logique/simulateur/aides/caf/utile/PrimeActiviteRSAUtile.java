package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class PrimeActiviteRSAUtile {

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    @Autowired
    private AideUtile aideUtile;

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
    public boolean isPrimeActiviteACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return primeActiviteUtile.isPrimeActiviteACalculerDeclarationTrimestrielle(numeroMoisSimule, prochaineDeclarationTrimestrielle);
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
    public boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return primeActiviteUtile.isPrimeActiviteAVerserDeclarationTrimestrielle(numeroMoisSimule, prochaineDeclarationTrimestrielle);
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
    public boolean isPrimeActiviteAReporter(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return primeActiviteUtile.isPrimeActiviteAReporterDeclarationTrimestrielle(numeroMoisSimule, prochaineDeclarationTrimestrielle);
    }

    public Aide creerAidePrimeActivite(float montantPrimeActivite, boolean isAideReportee) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.PPA_AUTOMATIQUE_SI_BENEFICIAIRE_RSA.getMessage());
	return aideUtile.creerAide(AideEnum.PRIME_ACTIVITE, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), isAideReportee, montantPrimeActivite);
    }
}

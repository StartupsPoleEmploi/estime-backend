package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class RSAUtile {

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresAvantSimulationUtile;

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
    public boolean isRSAACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
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
     *      |  -  M0     |    C0    |   (R0)   |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |              
     *      |  -  M1     |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |          
     *      |  -  M2     |    R0    |    R0    |   C1/R0  |   (V1)   |    R1    |  C2/R1   |   (V2)   |          
     *      |  -  M3     |    C0    |   (R0)   |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|   
     *       
     */
    public boolean isRSAAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
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
    public boolean isRSAAReporter(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 2 || numeroMoisSimule == 3 || numeroMoisSimule == 5 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 1 || numeroMoisSimule == 3 || numeroMoisSimule == 4 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 1 || numeroMoisSimule == 2 || numeroMoisSimule == 4 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 3)
			&& (numeroMoisSimule == 1 || numeroMoisSimule == 2 || numeroMoisSimule == 3 || numeroMoisSimule == 5 || numeroMoisSimule == 6)));
    }

    public void reporterRsa(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	Optional<Aide> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulation, numeroMoisSimule);
	if (rsaMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.RSA.getCode(), rsaMoisPrecedent.get());
	} else if (isEligiblePourReportRSADeclare(numeroMoisSimule, demandeurEmploi)) {
	    aidesPourCeMois.put(AideEnum.RSA.getCode(), getRSADeclare(demandeurEmploi));
	}
    }

    void reporterRsaEtPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	Optional<Aide> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulation, numeroMoisSimule);
	if (rsaMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.RSA.getCode(), rsaMoisPrecedent.get());
	} else if (isEligiblePourReportRSADeclare(numeroMoisSimule, demandeurEmploi)) {
	    aidesPourCeMois.put(AideEnum.RSA.getCode(), getRSADeclare(demandeurEmploi));
	}
	Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulation, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
	}
    }

    public Aide creerAideRSA(float montantRSA, boolean isAideReportee) {
	return aideUtile.creerAide(AideEnum.RSA, Optional.of(OrganismeEnum.CAF), Optional.empty(), isAideReportee, montantRSA);
    }

    private Aide getRSADeclare(DemandeurEmploi demandeurEmploi) {
	float montantDeclare = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationRSA();
	return creerAideRSA(montantDeclare, true);
    }

    private Optional<Aide> getRSASimuleeMoisPrecedent(Simulation simulation, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	return aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.RSA.getCode(), moisNMoins1);
    }

    public boolean isEligiblePourReportRSADeclare(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isEligiblePourReportRSADeclare = false;
	if (ressourcesFinancieresAvantSimulationUtile.hasAllocationsRSA(demandeurEmploi)) {
	    int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	    isEligiblePourReportRSADeclare = prochaineDeclarationTrimestrielle != 0 && ((prochaineDeclarationTrimestrielle == 3) && numeroMoisSimule <= 1)
		    || (prochaineDeclarationTrimestrielle == 1 && numeroMoisSimule <= 2) || (prochaineDeclarationTrimestrielle == 2 && numeroMoisSimule <= 3);

	}
	return isEligiblePourReportRSADeclare;
    }
}

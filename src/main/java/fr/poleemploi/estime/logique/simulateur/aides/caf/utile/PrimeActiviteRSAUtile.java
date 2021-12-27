package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteRSAUtile {

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

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
     *      |  -  M3     | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    | (C3)/R2  |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     *  
     */
    boolean isRSAACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
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
     *      |  -  M3     |  C1/R0   |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |  C3/R2   |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|   
     *       
     */
    boolean isRSAAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
	return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }

    void reporterRsaEtPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	Optional<Aide> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulationAides, numeroMoisSimule);
	if (rsaMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.RSA.getCode(), rsaMoisPrecedent.get());
	} else if (isEligiblePourReportRSADeclare(numeroMoisSimule, demandeurEmploi)) {
	    aidesPourCeMois.put(AideEnum.RSA.getCode(), getRSADeclare(demandeurEmploi));
	}
	Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
	}
    }

    protected Aide creerAideRSA(float montantRSA, boolean isAideReportee) {
	Aide aideRSA = new Aide();
	aideRSA.setCode(AideEnum.RSA.getCode());
	aideRSA.setMontant(montantRSA);
	aideRSA.setNom(AideEnum.RSA.getNom());
	aideRSA.setOrganisme(OrganismeEnum.CAF.getNomCourt());
	aideRSA.setReportee(isAideReportee);
	return aideRSA;
    }

    private Aide getRSADeclare(DemandeurEmploi demandeurEmploi) {
	float montantDeclare = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA();
	return creerAideRSA(montantDeclare, true);
    }

    private Optional<Aide> getRSASimuleeMoisPrecedent(SimulationAides simulationAides, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	return aideUtile.getAidePourCeMoisSimule(simulationAides, AideEnum.RSA.getCode(), moisNMoins1);
    }

    private boolean isEligiblePourReportRSADeclare(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isEligiblePourReportRSADeclare = false;
	if (demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() > 0) {
	    isEligiblePourReportRSADeclare = numeroMoisSimule <= demandeurEmploi.getRessourcesFinancieres().getAidesCAF()
		    .getProchaineDeclarationTrimestrielle();
	}
	return isEligiblePourReportRSADeclare;
    }
}

package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class PrimeActiviteUtile {

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private DateUtile dateUtile;

    public void reporterPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule) {
	Optional<Aide> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulation, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
	}
    }

    public Aide creerAidePrimeActivite(float montantPrimeActivite, boolean isAideReportee, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(getMessageAlerteDemandePPA(dateDebutSimulation, numeroMoisSimule));
	return aideUtile.creerAide(AideEnum.PRIME_ACTIVITE, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), isAideReportee, montantPrimeActivite);
    }

    public Optional<Aide> getPrimeActiviteMoisPrecedent(Simulation simulation, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	return aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.PRIME_ACTIVITE.getCode(), moisNMoins1);
    }

    private String getMessageAlerteDemandePPA(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	LocalDate dateMoisMMoins1 = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisSimule - 2);
	String libelleMoisMMoins1 = dateUtile.getMonthNameFromLocalDate(dateMoisMMoins1);

	if (libelleMoisMMoins1.equals("avril") || libelleMoisMMoins1.equals("août") || libelleMoisMMoins1.equals("octobre")) {
	    return String.format(MessageInformatifEnum.DEMANDE_PPA.getMessage(), "'" + libelleMoisMMoins1);
	} else {
	    return String.format(MessageInformatifEnum.DEMANDE_PPA.getMessage(), "e " + libelleMoisMMoins1);
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
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    | (C3)/R2  |              
     *      |  -  M1     |    R0    | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    |          
     *      |  -  M2     |    R0    |    R0    |  (C1)/R0 |    V1    |    R1    | (C2)/R1  |    V2    |          
     *      |  -  M3     |    C0    |    R0    |    R0    | (C1)/R0  |    V1    |    R1    | (C2)/R1  |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     *  
     */
    public boolean isPrimeActiviteACalculerDeclarationTrimestrielle(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
	return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 3))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 3)));
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
    public boolean isPrimeActiviteAVerserDeclarationTrimestrielle(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
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
    public boolean isPrimeActiviteAReporterDeclarationTrimestrielle(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
	return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 2 || numeroMoisSimule == 3 || numeroMoisSimule == 5 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 1 || numeroMoisSimule == 3 || numeroMoisSimule == 4 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 1 || numeroMoisSimule == 2 || numeroMoisSimule == 4 || numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 3)
			&& (numeroMoisSimule == 1 || numeroMoisSimule == 2 || numeroMoisSimule == 3 || numeroMoisSimule == 5 || numeroMoisSimule == 6)));
    }
}

package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.enumerations.StatutOccupationLogementEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class AidesLogementUtile {

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private AideUtile aideUtile;

    /**
     * Fonction permettant de déterminer si le montant des aides au logement doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return  
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     | (C1)/R0  | (C2)/V1  |    V2    | (C3)/R2  |    V3    |    R3    | (C4)/R3  |              
     *      |  -  M1     |    R0    | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    |          
     *      |  -  M2     |    R0    | (C1)/R0  | (C2)/V1  |    V2    |    R2    | (C3)/R2  |    V3    |          
     *      |  -  M3     | (C1)/R0  | (C2)/V1  |    V2    | (C3)/R2  |    V3    |    R3    | (C4)/R3  | 
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     */
    public boolean isAideLogementACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return ((numeroMoisSimule == 1) || (prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3)
		|| (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));
    }

    /**
     * Fonction permettant de déterminer si on a calculé le montant des aides au logement le mois précédent et s'il doit être versé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return  
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     |  C1/R0   |  C2/(V1) |   (V2)   |  C3/R2   |   (V3)   |    R3    |  C4/R3   |              
     *      |  -  M1     |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |          
     *      |  -  M2     |    R0    |  C1/R0   |  C2/(V1) |   (V2)   |    R2    |  C3/R2   |   (V3)   |          
     *      |  -  M3     |  C1/R0   |  C2/(V1) |   (V2)   |  C3/R2   |   (V3)   |    R3    |  C4/R3   |   
     *      |____________|__________|__________|__________|__________|__________|__________|__________|  
     *      
     */
    public boolean isAideLogementAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return (numeroMoisSimule == 2 || ((prochaineDeclarationTrimestrielle == 0 || prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 3 || numeroMoisSimule == 6)));
    }

    public void reporterAideLogement(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	Optional<Aide> aideLogementMoisPrecedent = getAideLogementSimuleeMoisPrecedent(simulation, numeroMoisSimule);
	if (aideLogementMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(aideLogementMoisPrecedent.get().getCode(), aideLogementMoisPrecedent.get());
	} else if (isEligiblePourReportAideLogementDeclare(demandeurEmploi, numeroMoisSimule)) {
	    Aide aideLogement = getAideLogementDeclare(demandeurEmploi);
	    aidesPourCeMois.put(aideLogement.getCode(), aideLogement);
	}
    }

    private Aide getAideLogementDeclare(DemandeurEmploi demandeurEmploi) {
	float montantDeclare = ressourcesFinancieresUtile.getMontantAideLogementDeclare(demandeurEmploi);
	String typeAideLogement = ressourcesFinancieresUtile.getTypeAideLogementDeclare(demandeurEmploi);
	return creerAideLogement(montantDeclare, typeAideLogement, true);
    }

    private Optional<Aide> getAideLogementSimuleeMoisPrecedent(Simulation simulation, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	Optional<Aide> aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode(), moisNMoins1);
	if (aidePourCeMois.isEmpty())
	    aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode(), moisNMoins1);
	if (aidePourCeMois.isEmpty())
	    aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode(), moisNMoins1);
	return aidePourCeMois;
    }

    private boolean isEligiblePourReportAideLogementDeclare(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return ressourcesFinancieresUtile.hasAidesLogement(demandeurEmploi)
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getProchaineDeclarationTrimestrielle() != null
		&& (numeroMoisSimule == 1 || numeroMoisSimule <= demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getProchaineDeclarationTrimestrielle());
    }

    public Aide creerAideLogement(float montantAideLogement, String typeAide, boolean isAideReportee) {
	Aide aideLogement = new Aide();
	if (typeAide.equals(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())) {
	    aideLogement = creerAidePersonnaliseeLogement(montantAideLogement, isAideReportee);
	}
	if (typeAide.equals(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())) {
	    aideLogement = creerAllocationLogementFamiliale(montantAideLogement, isAideReportee);
	}
	if (typeAide.equals(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())) {
	    aideLogement = creerAllocationLogementSociale(montantAideLogement, isAideReportee);
	}
	return aideLogement;
    }

    public Aide creerAidePersonnaliseeLogement(float montant, boolean isReportee) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.AIDE_PERSONNALISEE_LOGEMENT, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), isReportee, montant);
    }

    public Aide creerAllocationLogementFamiliale(float montant, boolean isReportee) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), isReportee, montant);
    }

    public Aide creerAllocationLogementSociale(float montant, boolean isReportee) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.ALLOCATION_LOGEMENT_SOCIALE, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), isReportee, montant);
    }

    public boolean isEligibleAidesLogement(DemandeurEmploi demandeurEmploi) {
	boolean isEligible = false;
	if (demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement() != null
		&& (demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().equals(StatutOccupationLogementEnum.LOCATAIRE_HLM.getLibelle())
			|| demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement()
				.equals(StatutOccupationLogementEnum.LOCATAIRE_MEUBLE.getLibelle())
			|| demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement()
				.equals(StatutOccupationLogementEnum.LOCATAIRE_NON_MEUBLE.getLibelle()))) {
	    isEligible = true;
	}
	return isEligible;
    }

}
